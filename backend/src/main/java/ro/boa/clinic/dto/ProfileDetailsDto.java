package ro.boa.clinic.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class ProfileDetailsDto {
    @NonNull
    private Long id;
}
