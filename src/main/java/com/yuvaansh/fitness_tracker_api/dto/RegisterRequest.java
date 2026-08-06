package com.yuvaansh.fitness_tracker_api.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * RegisterRequest - Data Transfer Object for user registration
 * 
 * This is what the frontend will send when a user wants to register.
 * It's a simple Java class that holds the data from the HTTP request.
 * 
 * Why use a DTO instead of using User entity directly?
 * - Security: We don't want to expose all User fields
 * - Flexibility: Request might have different structure than entity
 * - Validation: We can add validation rules here
 */
public class RegisterRequest {

    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank
    @Size(min = 8, max = 128)
    private String password;

    @NotBlank
    @Pattern(regexp = "^[MF]$", message = "must be M or F")
    private String sex;

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
    
    // Default constructor (needed for Spring to convert JSON to object)
    public RegisterRequest() {
    }
    
    // Constructor with all fields
    public RegisterRequest(String username, String password, String sex, Double height,
                          Double weight, String activityLevel, String goal, Double goalWeightChangePerWeek,
                          LocalDate dateOfBirth) {
        this.username = username;
        this.password = password;
        this.sex = sex;
        this.height = height;
        this.weight = weight;
        this.activityLevel = activityLevel;
        this.goal = goal;
        this.goalWeightChangePerWeek = goalWeightChangePerWeek;
        this.dateOfBirth = dateOfBirth;
    }
    
    // Getters and Setters (Spring needs these to convert JSON)
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getSex() {
        return sex;
    }
    
    public void setSex(String sex) {
        this.sex = sex;
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
