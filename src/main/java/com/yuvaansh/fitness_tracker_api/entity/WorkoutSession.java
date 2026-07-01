package com.yuvaansh.fitness_tracker_api.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * A logged workout session for a user. Optionally started from a {@link RoutineDay}
 * (e.g. today's "Push" day). Holds the sets that were actually performed.
 */
@Entity
@Table(
        name = "workout_sessions",
        indexes = @Index(
                name = "idx_workout_sessions_user_id_started_at",
                columnList = "user_id, started_at"
        )
)
public class WorkoutSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Optional link to the routine day this session was based on. Nullable so
     * users can log an ad-hoc workout with no routine.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_day_id")
    private RoutineDay routineDay;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Column(length = 500)
    private String notes;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("setNumber ASC")
    private List<WorkoutSet> sets = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public WorkoutSession() {
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void addSet(WorkoutSet set) {
        set.setSession(this);
        this.sets.add(set);
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

    public RoutineDay getRoutineDay() {
        return routineDay;
    }

    public void setRoutineDay(RoutineDay routineDay) {
        this.routineDay = routineDay;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(LocalDateTime endedAt) {
        this.endedAt = endedAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<WorkoutSet> getSets() {
        return sets;
    }

    public void setSets(List<WorkoutSet> sets) {
        this.sets = sets;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
