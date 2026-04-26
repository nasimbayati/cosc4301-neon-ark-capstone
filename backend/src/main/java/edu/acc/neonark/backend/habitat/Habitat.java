package edu.acc.neonark.backend.habitat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "habitats")
public class Habitat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String name;

    @Column(length = 120)
    private String zone;

    @Column(nullable = false)
    private Integer capacity;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Habitat() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getZone() {
        return zone;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

