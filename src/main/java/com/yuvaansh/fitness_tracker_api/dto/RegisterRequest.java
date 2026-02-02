package com.yuvaansh.fitness_tracker_api.dto;

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
    
    private String username;
    private String password;
    private String sex;  // "M" or "F"
    private Double height;  // in cm
    private Double weight;  // in lbs
    private String activityLevel;  // "SEDENTARY", "LIGHTLY_ACTIVE", etc.
    private String goal;  // "LOSE", "MAINTAIN", or "GAIN"
    private Double goalWeightChangePerWeek;  // in lbs/week
    
    // Default constructor (needed for Spring to convert JSON to object)
    public RegisterRequest() {
    }
    
    // Constructor with all fields
    public RegisterRequest(String username, String password, String sex, Double height,
                          Double weight, String activityLevel, String goal, Double goalWeightChangePerWeek) {
        this.username = username;
        this.password = password;
        this.sex = sex;
        this.height = height;
        this.weight = weight;
        this.activityLevel = activityLevel;
        this.goal = goal;
        this.goalWeightChangePerWeek = goalWeightChangePerWeek;
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
}
