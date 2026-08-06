package com.yuvaansh.fitness_tracker_api.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Log a meal from a USDA food. {@code servings} scales the food's per-100 g
 * macros (e.g. 1.5 = 150 g). {@code name} optionally overrides the food description.
 */
public class AddMealFromFoodRequest {

    @NotNull
    private Long fdcId;

    @NotNull
    @DecimalMin(value = "0.01")
    @DecimalMax(value = "1000.0")
    private BigDecimal servings;

    @NotNull
    private LocalDate mealDate;

    @Size(max = 200)
    private String name;

    public AddMealFromFoodRequest() {
    }

    public Long getFdcId() {
        return fdcId;
    }

    public void setFdcId(Long fdcId) {
        this.fdcId = fdcId;
    }

    public BigDecimal getServings() {
        return servings;
    }

    public void setServings(BigDecimal servings) {
        this.servings = servings;
    }

    public LocalDate getMealDate() {
        return mealDate;
    }

    public void setMealDate(LocalDate mealDate) {
        this.mealDate = mealDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
