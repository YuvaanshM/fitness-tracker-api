package com.yuvaansh.fitness_tracker_api.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public class LogSetRequest {

    @NotNull
    private Long exerciseId;

    @NotNull
    @Positive
    @Max(1000)
    private Integer setNumber;

    @NotNull
    @PositiveOrZero
    @Max(1000)
    private Integer reps;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @DecimalMax(value = "10000.0")
    private BigDecimal weightLbs;

    @PositiveOrZero
    @Max(7200)
    private Integer restSeconds;

    private Boolean completed;

    public LogSetRequest() {
    }

    public Long getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(Long exerciseId) {
        this.exerciseId = exerciseId;
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
