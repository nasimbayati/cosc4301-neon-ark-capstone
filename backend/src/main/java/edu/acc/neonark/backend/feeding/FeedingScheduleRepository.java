package edu.acc.neonark.backend.feeding;

import edu.acc.neonark.backend.creature.CreatureStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;
import java.util.List;

public interface FeedingScheduleRepository extends JpaRepository<FeedingSchedule, Long> {
    boolean existsByCreature_IdAndActiveTrue(Long creatureId);

    @EntityGraph(attributePaths = {"creature", "creature.habitat"})
    List<FeedingSchedule> findByFeedingTimeAndActiveTrueAndCreature_StatusNotOrderByCreature_NameAsc(
            LocalTime feedingTime,
            CreatureStatus status
    );
}

