package edu.acc.neonark.backend.creature;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCreatureRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 120, message = "Name cannot exceed 120 characters")
        String name,

        @NotBlank(message = "Species is required")
        @Size(max = 120, message = "Species cannot exceed 120 characters")
        String species,

        @NotNull(message = "Habitat ID is required")
        Long habitatId,

        String status
) {
}

