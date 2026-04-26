package edu.acc.neonark.backend.creature;

public record DeleteCreatureResponse(
        Long id,
        String name,
        String status,
        String removedAt,
        String message
) {
}

