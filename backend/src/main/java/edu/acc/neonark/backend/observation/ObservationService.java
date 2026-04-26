package edu.acc.neonark.backend.observation;

import edu.acc.neonark.backend.creature.Creature;
import edu.acc.neonark.backend.creature.CreatureResponse;
import edu.acc.neonark.backend.creature.CreatureService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ObservationService {
    private final CreatureService creatureService;
    private final ObservationRepository observationRepository;

    public ObservationService(CreatureService creatureService, ObservationRepository observationRepository) {
        this.creatureService = creatureService;
        this.observationRepository = observationRepository;
    }

    @Transactional(readOnly = true)
    public CreatureObservationsResponse getCreatureObservations(Long creatureId) {
        Creature creature = creatureService.findCreature(creatureId);
        CreatureResponse creatureResponse = creatureService.toResponse(creature);
        List<ObservationResponse> observations = observationRepository
                .findByCreature_IdOrderByObservedAtDesc(creatureId)
                .stream()
                .map(this::toResponse)
                .toList();
        return new CreatureObservationsResponse(creatureResponse, observations);
    }

    private ObservationResponse toResponse(Observation observation) {
        return new ObservationResponse(
                observation.getId(),
                observation.getAuthor().getFullName(),
                observation.getAuthor().getUsername(),
                observation.getObservedAt().toString(),
                observation.getNote()
        );
    }
}

