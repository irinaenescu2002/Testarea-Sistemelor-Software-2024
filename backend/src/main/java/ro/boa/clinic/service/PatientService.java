package ro.boa.clinic.service;

import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ro.boa.clinic.exception.AccountAlreadyHasProfile;
import ro.boa.clinic.exception.PatientProfileNotFoundException;
import ro.boa.clinic.model.Account;
import ro.boa.clinic.model.Patient;
import ro.boa.clinic.model.Sex;
import ro.boa.clinic.repository.PatientRepository;

import java.time.LocalDate;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class PatientService {
    private final PatientRepository patientRepository;
    private final AccountService accountService;

    @Transactional
    public Patient createPatientProfile(String firstName, String lastName, Sex sex, LocalDate birthdate,
                                        String accountEmail) {
        log.info("Creating a new patient profile");
        var patient = new Patient(firstName, lastName, sex, birthdate);
        var patientCreated = patientRepository.save(patient);
        boolean wasLinked = accountService.linkProfileToAccount(patientCreated, accountEmail);
        if (!wasLinked) {
            throw new AccountAlreadyHasProfile();
        }
        return patientCreated;
    }

    public Patient getAuthenticatedPatientProfile() {
        log.info("Getting authenticated patient profile");
        var userEmail = accountService.getAuthenticatedUserEmail();
        var patientProfile = patientRepository.findPatientProfileByEmail(userEmail);
        if (patientProfile == null) {
            throw new PatientProfileNotFoundException();
        } else {
            return patientProfile;
        }
    }

    public Optional<Patient> getPatientProfileForAccount(@NonNull Account account) {
        return Optional.ofNullable(patientRepository.findPatientProfileByEmail(account.getEmail()));
    }
}
