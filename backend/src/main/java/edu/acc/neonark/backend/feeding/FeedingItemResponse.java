package edu.acc.neonark.backend.feeding;

public record FeedingItemResponse(
        Long scheduleId,
        Long creatureId,
        String creatureName,
        String species,
        String habitatName,
        String feedingTime,
        String food,
        String instructions
) {
}

