package edu.acc.neonark.backend.observation;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ObservationRepository extends JpaRepository<Observation, Long> {
    @EntityGraph(attributePaths = "author")
    List<Observation> findByCreature_IdOrderByObservedAtDesc(Long creatureId);
}

