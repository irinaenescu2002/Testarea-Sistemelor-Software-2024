package ro.boa.clinic.dto;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;
import ro.boa.clinic.model.Status;

@Setter
@Getter
public class PatientTicketResponseDto extends TicketResponseDto {
    @Nullable
    private String doctorName;

    public PatientTicketResponseDto(Long id,
                                    String title,
                                    String description,
                                    String specialization,
                                    Status status,
                                    @Nullable String response,
                                    @Nullable String doctorName) {
        super(id, title, description, specialization, status, response);
        this.doctorName = doctorName;
    }
}
