package com.yuvaansh.fitness_tracker_api.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CreateBodyWeightEntryRequest {

    @NotNull
    @PastOrPresent
    private LocalDate entryDate;

    @NotNull
    @DecimalMin("50.0")
    @DecimalMax("1500.0")
    private BigDecimal weightLbs;

    public CreateBodyWeightEntryRequest() {
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
}
