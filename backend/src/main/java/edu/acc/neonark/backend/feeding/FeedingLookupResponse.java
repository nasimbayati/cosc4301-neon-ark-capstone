package edu.acc.neonark.backend.feeding;

import java.util.List;

public record FeedingLookupResponse(
        String time,
        String message,
        List<FeedingItemResponse> feedings
) {
}

