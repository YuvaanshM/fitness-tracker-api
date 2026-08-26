package com.yuvaansh.fitness_tracker_api.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Editable subset of the user's profile (no username/password/sex).
 */
public class UpdateProfileRequest {

    @NotNull
    @Positive
    @DecimalMax("300")
    private Double height;

    @NotNull
    @Positive
    @DecimalMax("1000")
    private Double weight;

    @NotBlank
    @Size(max = 50)
    private String activityLevel;

    @NotBlank
    @Size(max = 20)
    private String goal;

    @NotNull
    @DecimalMin("-10.0")
    @DecimalMax("10.0")
    private Double goalWeightChangePerWeek;

    @NotNull
    @Past
    private LocalDate dateOfBirth;

    public UpdateProfileRequest() {
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(String activityLevel) {
        this.activityLevel = activityLevel;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public Double getGoalWeightChangePerWeek() {
        return goalWeightChangePerWeek;
    }

    public void setGoalWeightChangePerWeek(Double goalWeightChangePerWeek) {
        this.goalWeightChangePerWeek = goalWeightChangePerWeek;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
