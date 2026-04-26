package edu.acc.neonark.backend.creature;

public record CreatureResponse(
        Long id,
        String name,
        String species,
        String status,
        Long habitatId,
        String habitatName,
        String habitatZone,
        String removedAt,
        String createdAt,
        String updatedAt
) {
}

