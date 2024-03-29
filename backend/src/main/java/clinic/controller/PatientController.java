package ro.boa.clinic.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ro.boa.clinic.dto.PatientCreationRequestDto;
import ro.boa.clinic.dto.PatientDetailsDto;
import ro.boa.clinic.model.Patient;
import ro.boa.clinic.model.Sex;
import ro.boa.clinic.service.AccountService;
import ro.boa.clinic.service.PatientService;

@RequiredArgsConstructor
@RestController
public class PatientController {
    private final PatientService patientService;
    private final AccountService accountService;

    @PostMapping("/patients")
    public ResponseEntity<Void> createPatientProfile(@Valid @RequestBody PatientCreationRequestDto requestDto) {
        patientService.createPatientProfile(
                requestDto.firstName(),
                requestDto.lastName(),
                Sex.valueOf(requestDto.sex()),
                requestDto.birthdate(),
                accountService.getAuthenticatedUserEmail()
        );

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/patients/0")
    public ResponseEntity<PatientDetailsDto> getCurrentPatientProfileDetails() {
        Patient patient = patientService.getAuthenticatedPatientProfile();
        var patientDetailsDto = new PatientDetailsDto(patient.getId(),
                                                      patient.getFirstName(),
                                                      patient.getLastName(),
                                                      patient.getSex().name(),
                                                      patient.getBirthdate());
        return ResponseEntity.ok(patientDetailsDto);
    }
}
