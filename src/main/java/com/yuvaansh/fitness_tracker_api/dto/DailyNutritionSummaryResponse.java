package com.yuvaansh.fitness_tracker_api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DailyNutritionSummaryResponse {

    private LocalDate date;
    private Long totalCalories;
    private BigDecimal totalProtein;
    private BigDecimal totalCarbs;
    private BigDecimal totalFats;
    private BigDecimal totalSugar;
    private BigDecimal totalFiber;

    public DailyNutritionSummaryResponse() {
    }

    public DailyNutritionSummaryResponse(
            LocalDate date,
            Long totalCalories,
            BigDecimal totalProtein,
            BigDecimal totalCarbs,
            BigDecimal totalFats,
            BigDecimal totalSugar,
            BigDecimal totalFiber) {
        this.date = date;
        this.totalCalories = totalCalories;
        this.totalProtein = totalProtein;
        this.totalCarbs = totalCarbs;
        this.totalFats = totalFats;
        this.totalSugar = totalSugar;
        this.totalFiber = totalFiber;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Long getTotalCalories() {
        return totalCalories;
    }

    public void setTotalCalories(Long totalCalories) {
        this.totalCalories = totalCalories;
    }

    public BigDecimal getTotalProtein() {
        return totalProtein;
    }

    public void setTotalProtein(BigDecimal totalProtein) {
        this.totalProtein = totalProtein;
    }

    public BigDecimal getTotalCarbs() {
        return totalCarbs;
    }

    public void setTotalCarbs(BigDecimal totalCarbs) {
        this.totalCarbs = totalCarbs;
    }

    public BigDecimal getTotalFats() {
        return totalFats;
    }

    public void setTotalFats(BigDecimal totalFats) {
        this.totalFats = totalFats;
    }

    public BigDecimal getTotalSugar() {
        return totalSugar;
    }

    public void setTotalSugar(BigDecimal totalSugar) {
        this.totalSugar = totalSugar;
    }

    public BigDecimal getTotalFiber() {
        return totalFiber;
    }

    public void setTotalFiber(BigDecimal totalFiber) {
        this.totalFiber = totalFiber;
    }
}
