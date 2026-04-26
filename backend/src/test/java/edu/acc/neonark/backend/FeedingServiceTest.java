package edu.acc.neonark.backend;

import edu.acc.neonark.backend.creature.CreatureStatus;
import edu.acc.neonark.backend.error.BadRequestException;
import edu.acc.neonark.backend.feeding.FeedingLookupResponse;
import edu.acc.neonark.backend.feeding.FeedingScheduleRepository;
import edu.acc.neonark.backend.feeding.FeedingService;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FeedingServiceTest {

    private final FeedingScheduleRepository feedingScheduleRepository = mock(FeedingScheduleRepository.class);
    private final FeedingService feedingService = new FeedingService(feedingScheduleRepository);

    @Test
    void rejectsMissingTime() {
        assertThrows(BadRequestException.class, () -> feedingService.findFeedings(""));
    }

    @Test
    void rejectsInvalidFormat() {
        assertThrows(BadRequestException.class, () -> feedingService.findFeedings("8:00"));
    }

    @Test
    void returnsEmptyMessageWhenNoCreaturesNeedFeeding() {
        when(feedingScheduleRepository.findByFeedingTimeAndActiveTrueAndCreature_StatusNotOrderByCreature_NameAsc(
                LocalTime.of(8, 0),
                CreatureStatus.REMOVED
        )).thenReturn(List.of());

        FeedingLookupResponse response = feedingService.findFeedings("08:00");

        assertEquals("08:00", response.time());
        assertEquals("No creatures need attending at 08:00", response.message());
        assertTrue(response.feedings().isEmpty());
    }
}

