package edu.acc.neonark.backend.creature;

import edu.acc.neonark.backend.observation.CreatureObservationsResponse;
import edu.acc.neonark.backend.observation.ObservationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/creatures")
public class CreatureController {
    private final CreatureService creatureService;
    private final ObservationService observationService;

    public CreatureController(CreatureService creatureService, ObservationService observationService) {
        this.creatureService = creatureService;
        this.observationService = observationService;
    }

    @GetMapping
    public ResponseEntity<List<CreatureResponse>> listCreatures(
            @RequestParam(defaultValue = "false") boolean includeRemoved
    ) {
        return ResponseEntity.ok(creatureService.listCreatures(includeRemoved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CreatureResponse> getCreature(@PathVariable Long id) {
        return ResponseEntity.ok(creatureService.getCreature(id));
    }

    @PostMapping
    public ResponseEntity<CreatureResponse> createCreature(@Valid @RequestBody CreateCreatureRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(creatureService.createCreature(request));
    }

    @PutMapping("/{id}/name")
    public ResponseEntity<RenameCreatureResponse> renameCreature(
            @PathVariable Long id,
            @Valid @RequestBody RenameCreatureRequest request
    ) {
        return ResponseEntity.ok(creatureService.renameCreature(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteCreatureResponse> softDeleteCreature(@PathVariable Long id) {
        return ResponseEntity.ok(creatureService.softDeleteCreature(id));
    }

    @GetMapping("/{id}/observations")
    public ResponseEntity<CreatureObservationsResponse> getCreatureObservations(@PathVariable Long id) {
        return ResponseEntity.ok(observationService.getCreatureObservations(id));
    }
}

