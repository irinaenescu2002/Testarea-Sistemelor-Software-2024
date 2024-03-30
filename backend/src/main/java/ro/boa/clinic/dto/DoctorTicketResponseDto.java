package ro.boa.clinic.dto;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import ro.boa.clinic.model.Status;

@Setter
@Getter
public class DoctorTicketResponseDto extends TicketResponseDto {
    private String patientName;

    public DoctorTicketResponseDto(Long id,
                                   String title,
                                   String description,
                                   String specialization,
                                   Status status,
                                   @Nullable String response,
                                   String patientName) {
        super(id, title, description, specialization, status, response);
        this.patientName = patientName;
    }
}
