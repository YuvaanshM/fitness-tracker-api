package com.yuvaansh.fitness_tracker_api.dto;

import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class StartWorkoutRequest {

    /**
     * Optional routine day to base the session on (e.g. today's "Push" day).
     */
    private Long routineDayId;

    /**
     * Optional explicit start time. Defaults to now when omitted.
     */
    private LocalDateTime startedAt;

    @Size(max = 500)
    private String notes;

    public StartWorkoutRequest() {
    }

    public Long getRoutineDayId() {
        return routineDayId;
    }

    public void setRoutineDayId(Long routineDayId) {
        this.routineDayId = routineDayId;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
