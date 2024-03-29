package ro.boa.clinic.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Pattern;
import ro.boa.clinic.model.Status;

import java.util.Optional;

public record TicketUpdateRequestDto(Optional<String> title,
                                     Optional<String> description,
                                     Optional<@Pattern(regexp = "^(OPENED|CLOSED)$") String> status,
                                     Optional<String> specialization,
                                     Optional<String> response) {

    public TicketUpdateRequestDto(@Nullable String title,
                                  @Nullable String description,
                                  @Nullable Status status,
                                  @Nullable String specialization,
                                  @Nullable String response) {
        this(Optional.ofNullable(title),
             Optional.ofNullable(description),
             Optional.ofNullable(status).map(Status::name),
             Optional.ofNullable(specialization),
             Optional.ofNullable(response));
    }

    public TicketUpdateRequestDto(@Nullable Status status,
                                  @Nullable String specialization,
                                  @Nullable String response) {
        this(null, null, status, specialization, response);
    }
}