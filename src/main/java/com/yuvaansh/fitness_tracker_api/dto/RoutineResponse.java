package com.yuvaansh.fitness_tracker_api.dto;

import com.yuvaansh.fitness_tracker_api.entity.Routine;

import java.time.LocalDateTime;
import java.util.List;

public class RoutineResponse {

    private Long id;
    private String name;
    private String description;
    private List<RoutineDayResponse> days;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RoutineResponse() {
    }

    public static RoutineResponse fromEntity(Routine routine) {
        RoutineResponse response = new RoutineResponse();
        response.setId(routine.getId());
        response.setName(routine.getName());
        response.setDescription(routine.getDescription());
        response.setDays(routine.getDays().stream()
                .map(RoutineDayResponse::fromEntity)
                .toList());
        response.setCreatedAt(routine.getCreatedAt());
        response.setUpdatedAt(routine.getUpdatedAt());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<RoutineDayResponse> getDays() {
        return days;
    }

    public void setDays(List<RoutineDayResponse> days) {
        this.days = days;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
