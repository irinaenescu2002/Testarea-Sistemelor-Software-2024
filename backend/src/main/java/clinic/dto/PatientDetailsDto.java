package ro.boa.clinic.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import ro.boa.clinic.model.Patient;

import java.time.LocalDate;

@Getter
@Setter
public final class PatientDetailsDto extends ProfileDetailsDto {
    @NonNull
    private String firstName;
    @NonNull
    private String lastName;
    @NonNull
    private String sex;
    @NonNull
    private LocalDate birthdate;

    public PatientDetailsDto(@NonNull Long id, @NotNull String firstName, @NotNull String lastName,
                             @NotNull String sex, @NotNull LocalDate birthdate) {
        super(id);
        this.firstName = firstName;
        this.lastName = lastName;
        this.sex = sex;
        this.birthdate = birthdate;
    }

    public static PatientDetailsDto fromPatient(@NonNull Patient patient) {
        return new PatientDetailsDto(
            patient.getId(),
            patient.getFirstName(),
            patient.getLastName(),
            patient.getSex().name(),
            patient.getBirthdate()
        );
    }
}
