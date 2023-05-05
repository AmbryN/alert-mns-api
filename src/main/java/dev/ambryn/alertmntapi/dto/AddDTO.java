package dev.ambryn.alertmntapi.dto;

import jakarta.validation.constraints.Positive;

public record AddDTO(
        @Positive
        Long id
) {}