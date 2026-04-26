package edu.acc.neonark.backend.creature;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CreatureRepository extends JpaRepository<Creature, Long> {
    List<Creature> findAllByOrderByIdAsc();

    List<Creature> findByStatusNotOrderByIdAsc(CreatureStatus status);

    boolean existsByHabitat_IdAndNameIgnoreCase(Long habitatId, String name);

    boolean existsByHabitat_IdAndNameIgnoreCaseAndIdNot(Long habitatId, String name, Long id);
}

