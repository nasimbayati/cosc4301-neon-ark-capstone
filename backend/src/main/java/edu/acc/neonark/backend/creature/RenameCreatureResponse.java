package edu.acc.neonark.backend.creature;

public record RenameCreatureResponse(
        Long id,
        String oldName,
        String newName,
        String habitatName,
        String message
) {
}

