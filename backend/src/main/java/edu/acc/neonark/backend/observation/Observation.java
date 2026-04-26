package edu.acc.neonark.backend.observation;

import edu.acc.neonark.backend.admin.AppUser;
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

@Entity
@Table(name = "observations")
public class Observation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creature_id", nullable = false)
    private Creature creature;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private AppUser author;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String note;

    @Column(name = "observed_at", nullable = false)
    private LocalDateTime observedAt;

    public Observation() {
    }

    public Long getId() {
        return id;
    }

    public AppUser getAuthor() {
        return author;
    }

    public String getNote() {
        return note;
    }

    public LocalDateTime getObservedAt() {
        return observedAt;
    }
}

