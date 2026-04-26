package edu.acc.neonark.backend.creature;

import edu.acc.neonark.backend.error.BadRequestException;
import edu.acc.neonark.backend.error.ConflictException;
import edu.acc.neonark.backend.error.NotFoundException;
import edu.acc.neonark.backend.feeding.FeedingScheduleRepository;
import edu.acc.neonark.backend.habitat.Habitat;
import edu.acc.neonark.backend.habitat.HabitatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
public class CreatureService {
    private final CreatureRepository creatureRepository;
    private final HabitatRepository habitatRepository;
    private final FeedingScheduleRepository feedingScheduleRepository;

    public CreatureService(
            CreatureRepository creatureRepository,
            HabitatRepository habitatRepository,
            FeedingScheduleRepository feedingScheduleRepository
    ) {
        this.creatureRepository = creatureRepository;
        this.habitatRepository = habitatRepository;
        this.feedingScheduleRepository = feedingScheduleRepository;
    }

    @Transactional(readOnly = true)
    public List<CreatureResponse> listCreatures(boolean includeRemoved) {
        List<Creature> creatures = includeRemoved
                ? creatureRepository.findAllByOrderByIdAsc()
                : creatureRepository.findByStatusNotOrderByIdAsc(CreatureStatus.REMOVED);
        return creatures.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public CreatureResponse getCreature(Long id) {
        return toResponse(findCreature(id));
    }

    @Transactional
    public CreatureResponse createCreature(CreateCreatureRequest request) {
        String name = cleanRequired(request.name(), "Name is required");
        String species = cleanRequired(request.species(), "Species is required");
        Habitat habitat = habitatRepository.findById(request.habitatId())
                .orElseThrow(() -> new BadRequestException("Habitat ID does not exist: " + request.habitatId()));
        CreatureStatus status = parseCreateStatus(request.status());

        if (creatureRepository.existsByHabitat_IdAndNameIgnoreCase(habitat.getId(), name)) {
            throw new ConflictException("Creature name already exists in habitat: " + habitat.getName());
        }

        Creature creature = new Creature(name, species, status, habitat);
        return toResponse(creatureRepository.save(creature));
    }

    @Transactional
    public RenameCreatureResponse renameCreature(Long id, RenameCreatureRequest request) {
        Creature creature = findCreature(id);
        String newName = cleanRequired(request.newName(), "New name is required");
        if (creature.getName().equals(newName)) {
            throw new BadRequestException("New name must be different from the current name");
        }
        if (creatureRepository.existsByHabitat_IdAndNameIgnoreCaseAndIdNot(
                creature.getHabitat().getId(), newName, creature.getId())) {
            throw new ConflictException("Creature name already exists in habitat: " + creature.getHabitat().getName());
        }

        String oldName = creature.getName();
        creature.setName(newName);
        Creature saved = creatureRepository.save(creature);
        return new RenameCreatureResponse(
                saved.getId(),
                oldName,
                saved.getName(),
                saved.getHabitat().getName(),
                "Creature renamed successfully"
        );
    }

    @Transactional
    public DeleteCreatureResponse softDeleteCreature(Long id) {
        Creature creature = findCreature(id);
        if (creature.getStatus() == CreatureStatus.REMOVED) {
            throw new ConflictException("Creature is already removed");
        }
        if (feedingScheduleRepository.existsByCreature_IdAndActiveTrue(id)) {
            throw new ConflictException("Creature cannot be removed while active feeding schedules exist");
        }

        creature.setStatus(CreatureStatus.REMOVED);
        creature.setRemovedAt(LocalDateTime.now());
        Creature saved = creatureRepository.save(creature);
        return new DeleteCreatureResponse(
                saved.getId(),
                saved.getName(),
                saved.getStatus().name(),
                format(saved.getRemovedAt()),
                "Creature status changed to REMOVED"
        );
    }

    @Transactional(readOnly = true)
    public Creature findCreature(Long id) {
        return creatureRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Creature not found with id: " + id));
    }

    public CreatureResponse toResponse(Creature creature) {
        return new CreatureResponse(
                creature.getId(),
                creature.getName(),
                creature.getSpecies(),
                creature.getStatus().name(),
                creature.getHabitat().getId(),
                creature.getHabitat().getName(),
                creature.getHabitat().getZone(),
                format(creature.getRemovedAt()),
                format(creature.getCreatedAt()),
                format(creature.getUpdatedAt())
        );
    }

    private CreatureStatus parseCreateStatus(String rawStatus) {
        if (rawStatus == null || rawStatus.isBlank()) {
            return CreatureStatus.ACTIVE;
        }
        try {
            CreatureStatus status = CreatureStatus.valueOf(rawStatus.trim().toUpperCase(Locale.ROOT));
            if (status == CreatureStatus.REMOVED) {
                throw new BadRequestException("New creatures cannot start with status REMOVED");
            }
            return status;
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Status must be ACTIVE or INTAKE");
        }
    }

    private String cleanRequired(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new BadRequestException(message);
        }
        return value.trim();
    }

    private String format(LocalDateTime value) {
        return value == null ? null : value.toString();
    }
}
