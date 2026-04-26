package edu.acc.neonark.backend.feeding;

import edu.acc.neonark.backend.creature.CreatureStatus;
import edu.acc.neonark.backend.error.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class FeedingService {
    private static final DateTimeFormatter HH_MM = DateTimeFormatter.ofPattern("HH:mm");

    private final FeedingScheduleRepository feedingScheduleRepository;

    public FeedingService(FeedingScheduleRepository feedingScheduleRepository) {
        this.feedingScheduleRepository = feedingScheduleRepository;
    }

    @Transactional(readOnly = true)
    public FeedingLookupResponse findFeedings(String timeText) {
        LocalTime time = parseTime(timeText);
        List<FeedingItemResponse> feedings = feedingScheduleRepository
                .findByFeedingTimeAndActiveTrueAndCreature_StatusNotOrderByCreature_NameAsc(time, CreatureStatus.REMOVED)
                .stream()
                .map(this::toResponse)
                .toList();
        String message = feedings.isEmpty()
                ? "No creatures need attending at " + time.format(HH_MM)
                : feedings.size() + " creature(s) require feeding at " + time.format(HH_MM);
        return new FeedingLookupResponse(time.format(HH_MM), message, feedings);
    }

    private LocalTime parseTime(String timeText) {
        if (timeText == null || timeText.isBlank()) {
            throw new BadRequestException("Time query parameter is required in HH:MM format");
        }
        String cleaned = timeText.trim();
        if (!cleaned.matches("\\d{2}:\\d{2}")) {
            throw new BadRequestException("Time must use HH:MM format");
        }
        try {
            return LocalTime.parse(cleaned, HH_MM);
        } catch (DateTimeParseException ex) {
            throw new BadRequestException("Time must be a valid HH:MM value");
        }
    }

    private FeedingItemResponse toResponse(FeedingSchedule schedule) {
        return new FeedingItemResponse(
                schedule.getId(),
                schedule.getCreature().getId(),
                schedule.getCreature().getName(),
                schedule.getCreature().getSpecies(),
                schedule.getCreature().getHabitat().getName(),
                schedule.getFeedingTime().format(HH_MM),
                schedule.getFood(),
                schedule.getInstructions()
        );
    }
}

