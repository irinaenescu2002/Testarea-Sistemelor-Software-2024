package ro.boa.clinic.service;

import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ro.boa.clinic.exception.AccountAlreadyHasProfile;
import ro.boa.clinic.exception.DoctorProfileNotFoundException;
import ro.boa.clinic.model.Account;
import ro.boa.clinic.model.Doctor;
import ro.boa.clinic.repository.DoctorRepository;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final AccountService accountService;

    public boolean checkSpecializationExists(String specialization) {
        return doctorRepository.existsDoctorBySpecialization(specialization);
    }

    @Transactional
    public Doctor createDoctorProfile(String firstName, String lastName, String specialization, String accountEmail) {
        log.info("Creating a new doctor profile");
        var doctor = new Doctor(firstName, lastName, specialization);
        var doctorCreated = doctorRepository.save(doctor);
        boolean wasLinked = accountService.linkProfileToAccount(doctorCreated, accountEmail);
        if (!wasLinked) {
            throw new AccountAlreadyHasProfile();
        }
        return doctorCreated;
    }

    public Doctor getAuthenticatedDoctorProfile() {
        log.info("Getting authenticated patient profile");
        var userEmail = accountService.getAuthenticatedUserEmail();
        var doctorProfile = doctorRepository.findDoctorProfileByEmail(userEmail);
        if (doctorProfile == null) {
            throw new DoctorProfileNotFoundException();
        } else {
            return doctorProfile;
        }
    }

    public Doctor findFreestDoctorBySpecialization(String specialization) {
        return doctorRepository.findFreestDoctorBySpecialization(specialization);
    }

    public Optional<Doctor> getDoctorProfileByAccount(@NonNull Account account) {
        return Optional.ofNullable(doctorRepository.findDoctorProfileByEmail(account.getEmail()));
    }
}