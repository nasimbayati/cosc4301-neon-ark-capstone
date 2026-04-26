package edu.acc.neonark.backend.observation;

public record ObservationResponse(
        Long id,
        String authorFullName,
        String authorUsername,
        String observedAt,
        String note
) {
}

