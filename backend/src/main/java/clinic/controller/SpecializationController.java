package ro.boa.clinic.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ro.boa.clinic.dto.SpecializationDetectionRequestDto;
import ro.boa.clinic.dto.SpecializationDetectionResponseDto;
import ro.boa.clinic.service.SpecializationService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class SpecializationController {
    final SpecializationService specializationService;

    @PostMapping("/specialization-detection")
    public ResponseEntity<SpecializationDetectionResponseDto> detectSpecialization(@RequestBody SpecializationDetectionRequestDto requestDto) {
        var specialization = specializationService.detectSpecialization(requestDto.description());

        return ResponseEntity.ok(new SpecializationDetectionResponseDto(specialization));
    }

    @GetMapping("/specializations")
    public ResponseEntity<List<String>> getSpecializations() {
        var specializations = specializationService.getSpecializations();

        return ResponseEntity.ok(specializations);
    }
}
