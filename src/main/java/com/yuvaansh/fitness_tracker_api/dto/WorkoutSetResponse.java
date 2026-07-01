package com.yuvaansh.fitness_tracker_api.dto;

import com.yuvaansh.fitness_tracker_api.entity.WorkoutSet;

import java.math.BigDecimal;

public class WorkoutSetResponse {

    private Long id;
    private Long exerciseId;
    private String exerciseName;
    private Integer setNumber;
    private Integer reps;
    private BigDecimal weightLbs;
    private Integer restSeconds;
    private Boolean completed;

    public WorkoutSetResponse() {
    }

    public static WorkoutSetResponse fromEntity(WorkoutSet set) {
        WorkoutSetResponse response = new WorkoutSetResponse();
        response.setId(set.getId());
        response.setExerciseId(set.getExercise().getId());
        response.setExerciseName(set.getExercise().getName());
        response.setSetNumber(set.getSetNumber());
        response.setReps(set.getReps());
        response.setWeightLbs(set.getWeightLbs());
        response.setRestSeconds(set.getRestSeconds());
        response.setCompleted(set.getCompleted());
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
