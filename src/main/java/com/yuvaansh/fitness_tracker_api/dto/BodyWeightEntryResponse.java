package com.yuvaansh.fitness_tracker_api.dto;

import com.yuvaansh.fitness_tracker_api.entity.BodyWeightEntry;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class BodyWeightEntryResponse {

    private Long id;
    private LocalDate entryDate;
    private BigDecimal weightLbs;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BodyWeightEntryResponse() {
    }

    public static BodyWeightEntryResponse fromEntity(BodyWeightEntry entry) {
        BodyWeightEntryResponse response = new BodyWeightEntryResponse();
        response.setId(entry.getId());
        response.setEntryDate(entry.getEntryDate());
        response.setWeightLbs(entry.getWeightLbs());
        response.setCreatedAt(entry.getCreatedAt());
        response.setUpdatedAt(entry.getUpdatedAt());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(LocalDate entryDate) {
        this.entryDate = entryDate;
    }

    public BigDecimal getWeightLbs() {
        return weightLbs;
    }

    public void setWeightLbs(BigDecimal weightLbs) {
        this.weightLbs = weightLbs;
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
