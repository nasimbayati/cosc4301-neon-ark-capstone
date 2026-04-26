package edu.acc.neonark.backend.feeding;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/feedings")
public class FeedingController {
    private final FeedingService feedingService;

    public FeedingController(FeedingService feedingService) {
        this.feedingService = feedingService;
    }

    @GetMapping
    public ResponseEntity<FeedingLookupResponse> findFeedings(@RequestParam(required = false) String time) {
        return ResponseEntity.ok(feedingService.findFeedings(time));
    }
}

