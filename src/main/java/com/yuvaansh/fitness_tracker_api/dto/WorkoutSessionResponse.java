package com.yuvaansh.fitness_tracker_api.dto;

import com.yuvaansh.fitness_tracker_api.entity.WorkoutSession;

import java.time.LocalDateTime;
import java.util.List;

public class WorkoutSessionResponse {

    private Long id;
    private Long routineDayId;
    private String routineDayName;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private String notes;
    private List<WorkoutSetResponse> sets;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public WorkoutSessionResponse() {
    }

    public static WorkoutSessionResponse fromEntity(WorkoutSession session) {
        WorkoutSessionResponse response = new WorkoutSessionResponse();
        response.setId(session.getId());
        if (session.getRoutineDay() != null) {
            response.setRoutineDayId(session.getRoutineDay().getId());
            response.setRoutineDayName(session.getRoutineDay().getName());
        }
        response.setStartedAt(session.getStartedAt());
        response.setEndedAt(session.getEndedAt());
        response.setNotes(session.getNotes());
        response.setSets(session.getSets().stream()
                .map(WorkoutSetResponse::fromEntity)
                .toList());
        response.setCreatedAt(session.getCreatedAt());
        response.setUpdatedAt(session.getUpdatedAt());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoutineDayId() {
        return routineDayId;
    }

    public void setRoutineDayId(Long routineDayId) {
        this.routineDayId = routineDayId;
    }

    public String getRoutineDayName() {
        return routineDayName;
    }

    public void setRoutineDayName(String routineDayName) {
        this.routineDayName = routineDayName;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(LocalDateTime endedAt) {
        this.endedAt = endedAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<WorkoutSetResponse> getSets() {
        return sets;
    }

    public void setSets(List<WorkoutSetResponse> sets) {
        this.sets = sets;
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
