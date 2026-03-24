package com.yuvaansh.fitness_tracker_api.dto;

import com.yuvaansh.fitness_tracker_api.entity.User;

/**
 * Safe subset of user fields for authenticated clients (no password).
 */
public class UserProfileResponse {

    private Long id;
    private String username;
    private String sex;
    private Double height;
    private Double weight;
    private String activityLevel;
    private String goal;
    private Double goalWeightChangePerWeek;

    public UserProfileResponse() {
    }

    public static UserProfileResponse fromEntity(User user) {
        UserProfileResponse r = new UserProfileResponse();
        r.setId(user.getId());
        r.setUsername(user.getUsername());
        r.setSex(user.getSex());
        r.setHeight(user.getHeight());
        r.setWeight(user.getWeight());
        r.setActivityLevel(user.getActivityLevel());
        r.setGoal(user.getGoal());
        r.setGoalWeightChangePerWeek(user.getGoalWeightChangePerWeek());
        return r;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
