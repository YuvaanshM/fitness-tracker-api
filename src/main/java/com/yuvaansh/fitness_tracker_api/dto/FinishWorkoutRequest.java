package com.yuvaansh.fitness_tracker_api.dto;

import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class FinishWorkoutRequest {

    /**
     * Optional explicit end time. Defaults to now when omitted.
     */
    private LocalDateTime endedAt;

    @Size(max = 500)
    private String notes;

    public FinishWorkoutRequest() {
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
}
