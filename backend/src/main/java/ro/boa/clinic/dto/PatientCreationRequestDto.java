package ro.boa.clinic.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record PatientCreationRequestDto(
    @NotNull @NotBlank String firstName,
    @NotNull @NotBlank String lastName,
    @NotNull @Pattern(regexp = "^(MALE|FEMALE)$") String sex,
    @NotNull @Past LocalDate birthdate
) {
}
