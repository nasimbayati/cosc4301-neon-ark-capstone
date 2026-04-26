package edu.acc.neonark.backend.observation;

import edu.acc.neonark.backend.creature.CreatureResponse;

import java.util.List;

public record CreatureObservationsResponse(
        CreatureResponse creature,
        List<ObservationResponse> observations
) {
}

