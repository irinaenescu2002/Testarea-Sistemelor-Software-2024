package ro.boa.clinic.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ro.boa.clinic.exception.PatientProfileNotFoundException;
import ro.boa.clinic.model.Patient;
import ro.boa.clinic.repository.PatientRepository;
import ro.boa.clinic.service.AccountService;
import ro.boa.clinic.service.PatientService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientServiceGptTest {
    @Mock
    private AccountService accountService;

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    @BeforeEach
    void setUp() {
        // Mock behavior for accountService
        when(accountService.getAuthenticatedUserEmail()).thenReturn("test@example.com");
    }

    @Test
    void testGetAuthenticatedPatientProfile() {
        // Mock behavior for patientRepository
        Patient mockPatient = new Patient(/* Initialize mock patient data */);
        when(patientRepository.findPatientProfileByEmail(anyString())).thenReturn(mockPatient);

        // Call the method under test
        Patient result = patientService.getAuthenticatedPatientProfile();

        // Verify that the method returned the expected result
        assertNotNull(result);
        assertEquals(mockPatient, result);
    }

    @Test
    void testGetAuthenticatedPatientProfile_whenProfileNotFound() {
        // Mock behavior for patientRepository to return null
        when(patientRepository.findPatientProfileByEmail(anyString())).thenReturn(null);

        // Verify that the method under test throws the expected exception
        assertThrows(PatientProfileNotFoundException.class, () -> patientService.getAuthenticatedPatientProfile());
    }
}
