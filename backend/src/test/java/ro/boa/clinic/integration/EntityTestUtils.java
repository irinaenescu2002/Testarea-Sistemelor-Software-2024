package ro.boa.clinic.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.boa.clinic.model.*;
import ro.boa.clinic.service.AccountService;
import ro.boa.clinic.service.DoctorService;
import ro.boa.clinic.service.PatientService;

@Component
public class EntityTestUtils {
    @Autowired
    private AccountService accountService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private PatientService patientService;

    public Account createAccount(String email, Role role) {
        return accountService.createAccount(email, "TestPassword", role);
    }

    public Doctor createDoctor(String firstName, String specialization) {
        var account = createAccount(firstName + "@example.com", Role.DOCTOR);
        return doctorService.createDoctorProfile(firstName, "TestDoctor", specialization, account.getEmail());
    }

    public Patient createPatient(String firstName) {
        var account = createAccount(firstName + "@example.com", Role.PATIENT);
        return patientService.createPatientProfile(firstName, "testPatient", Sex.FEMALE, null, account.getEmail());
    }
}
