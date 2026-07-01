package com.yuvaansh.fitness_tracker_api.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

/**
 * A single set performed during a {@link WorkoutSession}: which exercise, the set
 * number, reps, weight in pounds, and the actual rest taken afterwards.
 */
@Entity
@Table(
        name = "workout_sets",
        indexes = @Index(name = "idx_workout_sets_session_id", columnList = "session_id")
)
public class WorkoutSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private WorkoutSession session;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @Column(name = "set_number", nullable = false)
    private Integer setNumber;

    @Column(nullable = false)
    private Integer reps;

    @Column(name = "weight_lbs", nullable = false, precision = 8, scale = 2)
    private BigDecimal weightLbs;

    /**
     * Actual rest taken after this set, in seconds. Optional.
     */
    @Column(name = "rest_seconds")
    private Integer restSeconds;

    @Column(nullable = false)
    private Boolean completed = true;

    public WorkoutSet() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WorkoutSession getSession() {
        return session;
    }

    public void setSession(WorkoutSession session) {
        this.session = session;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public Integer getSetNumber() {
        return setNumber;
    }

    public void setSetNumber(Integer setNumber) {
        this.setNumber = setNumber;
    }

    public Integer getReps() {
        return reps;
    }

    public void setReps(Integer reps) {
        this.reps = reps;
    }

    public BigDecimal getWeightLbs() {
        return weightLbs;
    }

    public void setWeightLbs(BigDecimal weightLbs) {
        this.weightLbs = weightLbs;
    }

    public Integer getRestSeconds() {
        return restSeconds;
    }

    public void setRestSeconds(Integer restSeconds) {
        this.restSeconds = restSeconds;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
}
