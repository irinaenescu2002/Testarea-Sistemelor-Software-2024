package ro.boa.clinic.integration;

import jakarta.transaction.Transactional;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import ro.boa.clinic.dto.PatientCreationRequestDto;
import ro.boa.clinic.model.Account;
import ro.boa.clinic.model.Patient;
import ro.boa.clinic.model.Role;
import ro.boa.clinic.repository.PatientRepository;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PatientControllerTest {
    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RequestTester requestTester;

    private Account account;
    private Patient patientProfile;

    @BeforeEach
    public void setUp() throws Exception {
        account = requestTester.createTestAccount(Role.PATIENT);
        requestTester.authenticateAccount();
    }

    @Test
    void creationRequest_authenticated_createsProfile() throws Exception {
        var patientDto = new PatientCreationRequestDto("John", "Doe", "MALE", LocalDate.of(1997, 2, 14));

        mockMvc.perform(requestTester.authenticatedPost("/patients", patientDto)).andExpect(status().isCreated());

        var createdPatient = patientRepository.findPatientProfileByEmail(account.getEmail());
        assertEquals(patientDto.firstName(), createdPatient.getFirstName());
        assertEquals(patientDto.lastName(), createdPatient.getLastName());
        assertEquals(patientDto.sex(), createdPatient.getSex().name());
        assertEquals(patientDto.birthdate(), createdPatient.getBirthdate());
    }

    @Test
    void creationRequest_incorrectData_returnsError() throws Exception {
        var invalidPatientDto = new PatientCreationRequestDto("", "Doe", "MALE", LocalDate.of(1997, 2, 14));

        RequestBuilder requestBuilder = requestTester.authenticatedPost("/patients", invalidPatientDto);
        mockMvc.perform(requestBuilder).andExpect(status().isBadRequest());
    }

    @Test
    void creationRequest_accountAlreadyHasProfile_rollsBackAndReturnsError() throws Exception {
        var existingPatientDto = new PatientCreationRequestDto("John", "Doe", "MALE", LocalDate.of(1997, 2, 14));
        RequestBuilder existingPatientRequest = requestTester.authenticatedPost("/patients", existingPatientDto);
        var newPatientDto = new PatientCreationRequestDto("Different", "Profile", "MALE", LocalDate.of(1997, 2, 14));
        RequestBuilder newPatientRequest = requestTester.authenticatedPost("/patients", newPatientDto);

        mockMvc.perform(existingPatientRequest).andExpect(status().isCreated());
        mockMvc.perform(newPatientRequest)
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Account already has profile"));

        assertNotEquals(
                newPatientDto.firstName(),
                patientRepository.findPatientProfileByEmail(account.getEmail()).getFirstName()
        );
    }

    @Test
    void detailsRequest_authenticated_returnsDetails() throws Exception {
        patientProfile = requestTester.createTestPatient();
        String expectedJson = new JSONObject()
                .put("id", patientProfile.getId())
                .put("firstName", patientProfile.getFirstName())
                .put("lastName", patientProfile.getLastName())
                .put("sex", patientProfile.getSex().name())
                .put("birthdate", patientProfile.getBirthdate())
                .toString();

        mockMvc.perform(requestTester.authenticatedGet("/patients/0")).andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }
}
