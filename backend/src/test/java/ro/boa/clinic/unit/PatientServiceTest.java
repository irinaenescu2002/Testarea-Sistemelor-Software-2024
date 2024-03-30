package ro.boa.clinic.unit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ro.boa.clinic.exception.AccountAlreadyHasProfile;
import ro.boa.clinic.exception.PatientProfileNotFoundException;
import ro.boa.clinic.model.Patient;
import ro.boa.clinic.model.Sex;
import ro.boa.clinic.repository.PatientRepository;
import ro.boa.clinic.service.AccountService;
import ro.boa.clinic.service.PatientService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class PatientServiceTest {
    @InjectMocks
    private PatientService patientService;
    @Mock
    private AccountService accountService;
    @Mock
    private PatientRepository patientRepository;

    @Test
    public void createPatientRequest_profileNotLinked_createPatientProfile() {
        // Given
        var firstName = "John";
        var lastName = "Doe";
        var sex = Sex.MALE;
        var birthdate = LocalDate.now();
        var accountEmail = "john@example.com";
        var patient = new Patient(firstName, lastName, sex, birthdate);
        when(patientRepository.save(any())).thenReturn(patient);
        when(accountService.linkProfileToAccount(any(), any())).thenReturn(true);

        // When
        var patientCreated = patientService.createPatientProfile(firstName, lastName, sex, birthdate, accountEmail);

        // Then
        assertEquals(firstName, patientCreated.getFirstName());
        assertEquals(lastName, patientCreated.getLastName());
        assertEquals(sex, patientCreated.getSex());
        assertEquals(birthdate, patientCreated.getBirthdate());
    }

    @Test
    public void createPatientRequest_profileLinked_throwAccountAlreadyHasProfileException() {
        // Given
        var firstName = "John";
        var lastName = "Doe";
        var sex = Sex.MALE;
        var birthdate = LocalDate.now();
        var accountEmail = "john@example.com";
        var patient = new Patient(firstName, lastName, sex, birthdate);
        when(patientRepository.save(any())).thenReturn(patient);
        when(accountService.linkProfileToAccount(any(), any())).thenReturn(false);

        // When
        // Then
        assertThrows(AccountAlreadyHasProfile.class,
                () -> patientService.createPatientProfile(firstName, lastName, sex, birthdate, accountEmail));
    }

    @Test
    public void getAuthenticatedPatientProfile_returnPatientProfile() {
        // Given
        var firstName = "John";
        var lastName = "Doe";
        var sex = Sex.MALE;
        var birthdate = LocalDate.now();
        var accountEmail = "john@example.com";
        var patient = new Patient(firstName, lastName, sex, birthdate);
        when(accountService.getAuthenticatedUserEmail()).thenReturn(accountEmail);
        when(patientRepository.findPatientProfileByEmail(any())).thenReturn(patient);

        // When
        var patientCreated = patientService.getAuthenticatedPatientProfile();

        // Then
        assertEquals(firstName, patientCreated.getFirstName());
        assertEquals(lastName, patientCreated.getLastName());
        assertEquals(sex, patientCreated.getSex());
        assertEquals(birthdate, patientCreated.getBirthdate());
    }

    @Test
    public void getAuthenticatedPatientProfile_throwPatientProfileNotFoundException() {
        // Given
        var accountEmail = "john@example.com";
        when(accountService.getAuthenticatedUserEmail()).thenReturn(accountEmail);
        when(patientRepository.findPatientProfileByEmail(any())).thenReturn(null);

        // When
        // Then
        assertThrows(PatientProfileNotFoundException.class,
                () -> patientService.getAuthenticatedPatientProfile());
    }
}
