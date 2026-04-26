package edu.acc.neonark.backend.creature;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RenameCreatureRequest(
        @NotBlank(message = "New name is required")
        @Size(max = 120, message = "New name cannot exceed 120 characters")
        String newName
) {
}

