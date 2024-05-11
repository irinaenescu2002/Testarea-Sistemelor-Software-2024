package ro.boa.clinic.unit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ro.boa.clinic.model.Account;
import ro.boa.clinic.model.Role;
import ro.boa.clinic.service.PatientService;
import ro.boa.clinic.service.ProfileService;
import ro.boa.clinic.service.DoctorService;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ProfileServiceTest {
    @InjectMocks
    private ProfileService profileService;

    @Mock
    private PatientService patientService;

    @Mock
    private DoctorService doctorService;

    //in the profileService we are getting the profile details for an account
    //we are using a switch statement, which is equivalent to if ... else
    //we have 3 possible branches or decisions
    //the first one is the profile belongs to a patient
    //the second one is the profile belongs to a doctor
    //the third one is the profile belongs to some other entity, in which case we throw an error.
    //so in order to satisfy the coverage criteria, we must test all 3 branches
    @Test
    public void profile_test_patient() {

        var accountEmail = "john@example.com";
        var password = "test";
        var rol = Role.PATIENT;

        var account = new Account(accountEmail, password, rol);

        assertNotNull(profileService.getProfileDetailsForAccount(account));
    }

    @Test
    public void profile_test_doctor() {

        var accountEmail = "john@example.com";
        var password = "test";
        var rol = Role.DOCTOR;

        var account = new Account(accountEmail, password, rol);

        assertNotNull(profileService.getProfileDetailsForAccount(account));
    }

    @Test
    public void profile_test_admin() {

        var accountEmail = "john@example.com";
        var password = "test";
        var rol = Role.ADMIN;

        var account = new Account(accountEmail, password, rol);

        assertThrows(IllegalArgumentException.class, () -> profileService.getProfileDetailsForAccount(account));
    }
}
