package edu.acc.neonark.backend.feeding;

import edu.acc.neonark.backend.creature.Creature;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "feeding_schedules")
public class FeedingSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creature_id", nullable = false)
    private Creature creature;

    @Column(name = "feeding_time", nullable = false)
    private LocalTime feedingTime;

    @Column(nullable = false, length = 120)
    private String food;

    @Column(length = 500)
    private String instructions;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public FeedingSchedule() {
    }

    public Long getId() {
        return id;
    }

    public Creature getCreature() {
        return creature;
    }

    public LocalTime getFeedingTime() {
        return feedingTime;
    }

    public String getFood() {
        return food;
    }

    public String getInstructions() {
        return instructions;
    }
}

