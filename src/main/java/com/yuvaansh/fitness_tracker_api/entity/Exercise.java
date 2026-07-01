package com.yuvaansh.fitness_tracker_api.entity;

import com.yuvaansh.fitness_tracker_api.enums.Equipment;
import com.yuvaansh.fitness_tracker_api.enums.MuscleGroup;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * An exercise in the catalog. A {@code null} user means it is a global, seeded
 * exercise available to everyone; a non-null user means it is a custom exercise
 * owned by that user.
 */
@Entity
@Table(
        name = "exercises",
        indexes = @Index(name = "idx_exercises_user_id", columnList = "user_id")
)
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Owner of a custom exercise. Null for global/seeded exercises.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "primary_muscle", nullable = false, length = 20)
    private MuscleGroup primaryMuscle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Equipment equipment;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Exercise() {
    }

    public Exercise(User user, String name, MuscleGroup primaryMuscle, Equipment equipment) {
        this.user = user;
        this.name = name;
        this.primaryMuscle = primaryMuscle;
        this.equipment = equipment;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MuscleGroup getPrimaryMuscle() {
        return primaryMuscle;
    }

    public void setPrimaryMuscle(MuscleGroup primaryMuscle) {
        this.primaryMuscle = primaryMuscle;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
