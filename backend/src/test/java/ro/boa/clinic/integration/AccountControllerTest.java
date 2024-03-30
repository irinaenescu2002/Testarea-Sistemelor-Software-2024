package ro.boa.clinic.integration;

import jakarta.transaction.Transactional;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ro.boa.clinic.model.Account;
import ro.boa.clinic.model.Doctor;
import ro.boa.clinic.model.Patient;
import ro.boa.clinic.model.Role;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RequestTester requestTester;

    @Test
    void detailsRequest_noProfile_returnsDetailsWithNullProfile() throws Exception {
        Account account = requestTester.createTestAccount(Role.PATIENT);
        requestTester.authenticateAccount();
        String expectedJson = "{'email': '%s', 'role': '%s', 'profile': null}".formatted(
            account.getEmail(),
            account.getRole().name()
        );
        mockMvc.perform(requestTester.authenticatedGet("/accounts/0")).andExpect(status().isOk())
            .andExpect(content().json(expectedJson));
    }

    @Test
    void detailsRequest_patientProfile_returnsDetailsWithPatientProfile() throws Exception {
        Account account = requestTester.createTestAccount(Role.PATIENT);
        requestTester.authenticateAccount();
        Patient patientProfile = requestTester.createTestPatient();
        JSONObject patientJson = new JSONObject()
            .put("id", patientProfile.getId())
            .put("firstName", patientProfile.getFirstName())
            .put("lastName", patientProfile.getLastName())
            .put("sex", patientProfile.getSex().name())
            .put("birthdate", patientProfile.getBirthdate());
        String expectedJson = new JSONObject()
            .put("email", account.getEmail())
            .put("role", account.getRole().name())
            .put("profile", patientJson).toString();

        mockMvc.perform(requestTester.authenticatedGet("/accounts/0")).andExpect(status().isOk())
            .andExpect(content().json(expectedJson));
    }

    @Test
    void detailsRequest_doctorProfile_returnsDetailsWithDoctorProfile() throws Exception {
        Account account = requestTester.createTestAccount(Role.DOCTOR);
        requestTester.authenticateAccount();
        Doctor doctorProfile = requestTester.createTestDoctor();
        JSONObject doctorJson = new JSONObject()
            .put("id", doctorProfile.getId())
            .put("firstName", doctorProfile.getFirstName())
            .put("lastName", doctorProfile.getLastName())
            .put("specialization", doctorProfile.getSpecialization());
        String expectedJson = new JSONObject()
            .put("email", account.getEmail())
            .put("role", account.getRole().name())
            .put("profile", doctorJson)
            .toString();

        mockMvc.perform(requestTester.authenticatedGet("/accounts/0")).andExpect(status().isOk())
            .andExpect(content().json(expectedJson));
    }
}
