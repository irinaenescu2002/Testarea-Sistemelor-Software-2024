package ro.boa.clinic.dto;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ro.boa.clinic.model.Status;

@Getter
@Setter
@AllArgsConstructor
public class TicketResponseDto {
    private Long id;
    private String title;
    private String description;
    private String specialization;
    private Status status;
    @Nullable
    private String response;
}
