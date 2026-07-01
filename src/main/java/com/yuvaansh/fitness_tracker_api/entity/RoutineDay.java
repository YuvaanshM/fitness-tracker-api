package com.yuvaansh.fitness_tracker_api.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * A single day within a {@link Routine} (e.g. "Push"). It holds the planned
 * exercises for that day.
 */
@Entity
@Table(
        name = "routine_days",
        indexes = @Index(name = "idx_routine_days_routine_id", columnList = "routine_id")
)
public class RoutineDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "routine_id", nullable = false)
    private Routine routine;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @OneToMany(mappedBy = "routineDay", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<RoutineExercise> exercises = new ArrayList<>();

    public RoutineDay() {
    }

    public RoutineDay(Routine routine, String name, Integer orderIndex) {
        this.routine = routine;
        this.name = name;
        this.orderIndex = orderIndex;
    }

    public void addExercise(RoutineExercise exercise) {
        exercise.setRoutineDay(this);
        this.exercises.add(exercise);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Routine getRoutine() {
        return routine;
    }

    public void setRoutine(Routine routine) {
        this.routine = routine;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public List<RoutineExercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<RoutineExercise> exercises) {
        this.exercises = exercises;
    }
}
