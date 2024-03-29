package ro.boa.clinic.dto;

import jakarta.annotation.Nullable;
import ro.boa.clinic.model.Role;

public record AccountDetailsDto(String email, Role role, @Nullable ProfileDetailsDto profile) {
}
