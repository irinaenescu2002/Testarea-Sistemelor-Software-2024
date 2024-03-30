package ro.boa.clinic.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ro.boa.clinic.dto.DoctorDetailsDto;
import ro.boa.clinic.dto.PatientDetailsDto;
import ro.boa.clinic.dto.ProfileDetailsDto;
import ro.boa.clinic.model.Account;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ProfileService {
    private final PatientService patientService;
    private final DoctorService doctorService;

    public Optional<? extends ProfileDetailsDto> getProfileDetailsForAccount(@NonNull Account account) {
        return switch (account.getRole()) {
            case PATIENT -> patientService.getPatientProfileForAccount(account).map(PatientDetailsDto::fromPatient);
            case DOCTOR -> doctorService.getDoctorProfileByAccount(account).map(DoctorDetailsDto::fromDoctor);
            default -> throw new IllegalArgumentException("Account must have a patient or a doctor profile");
        };
    }
}
