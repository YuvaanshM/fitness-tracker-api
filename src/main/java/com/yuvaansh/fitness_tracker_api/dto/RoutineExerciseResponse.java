package com.yuvaansh.fitness_tracker_api.dto;

import com.yuvaansh.fitness_tracker_api.entity.RoutineExercise;
import com.yuvaansh.fitness_tracker_api.enums.MuscleGroup;

public class RoutineExerciseResponse {

    private Long id;
    private Long exerciseId;
    private String exerciseName;
    private MuscleGroup primaryMuscle;
    private Integer orderIndex;
    private Integer targetSets;
    private Integer targetReps;
    private Integer restSeconds;
    private String notes;

    public RoutineExerciseResponse() {
    }

    public static RoutineExerciseResponse fromEntity(RoutineExercise entity) {
        RoutineExerciseResponse response = new RoutineExerciseResponse();
        response.setId(entity.getId());
        response.setExerciseId(entity.getExercise().getId());
        response.setExerciseName(entity.getExercise().getName());
        response.setPrimaryMuscle(entity.getExercise().getPrimaryMuscle());
        response.setOrderIndex(entity.getOrderIndex());
        response.setTargetSets(entity.getTargetSets());
        response.setTargetReps(entity.getTargetReps());
        response.setRestSeconds(entity.getRestSeconds());
        response.setNotes(entity.getNotes());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(Long exerciseId) {
        this.exerciseId = exerciseId;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public MuscleGroup getPrimaryMuscle() {
        return primaryMuscle;
    }

    public void setPrimaryMuscle(MuscleGroup primaryMuscle) {
        this.primaryMuscle = primaryMuscle;
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
