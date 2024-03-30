package ro.boa.clinic.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import ro.boa.clinic.model.Doctor;

@Getter
@Setter
public class DoctorDetailsDto extends ProfileDetailsDto {
    private String firstName;
    private String lastName;
    private String specialization;

    public DoctorDetailsDto(@NonNull Long id, @NonNull String firstName, @NonNull String lastName,
                            @NonNull String specialization) {
        super(id);
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialization = specialization;
    }

    public static DoctorDetailsDto fromDoctor(@NonNull Doctor doctor) {
        return new DoctorDetailsDto(
            doctor.getId(),
            doctor.getFirstName(),
            doctor.getLastName(),
            doctor.getSpecialization()
        );
    }
}
