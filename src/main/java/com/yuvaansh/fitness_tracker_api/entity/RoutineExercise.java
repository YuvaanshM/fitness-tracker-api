package com.yuvaansh.fitness_tracker_api.entity;

import jakarta.persistence.*;

/**
 * A planned exercise inside a {@link RoutineDay}: which exercise, in what order,
 * with target sets/reps and the rest duration the frontend timer should count down.
 */
@Entity
@Table(
        name = "routine_exercises",
        indexes = @Index(name = "idx_routine_exercises_day_id", columnList = "routine_day_id")
)
public class RoutineExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "routine_day_id", nullable = false)
    private RoutineDay routineDay;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @Column(name = "target_sets")
    private Integer targetSets;

    @Column(name = "target_reps")
    private Integer targetReps;

    /**
     * Planned rest between sets, in seconds. Backend side of the rest timer;
     * the frontend runs the actual countdown.
     */
    @Column(name = "rest_seconds")
    private Integer restSeconds;

    @Column(length = 300)
    private String notes;

    public RoutineExercise() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RoutineDay getRoutineDay() {
        return routineDay;
    }

    public void setRoutineDay(RoutineDay routineDay) {
        this.routineDay = routineDay;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public Integer getTargetSets() {
        return targetSets;
    }

    public void setTargetSets(Integer targetSets) {
        this.targetSets = targetSets;
    }

    public Integer getTargetReps() {
        return targetReps;
    }

    public void setTargetReps(Integer targetReps) {
        this.targetReps = targetReps;
    }

    public Integer getRestSeconds() {
        return restSeconds;
    }

    public void setRestSeconds(Integer restSeconds) {
        this.restSeconds = restSeconds;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
