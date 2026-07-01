package com.yuvaansh.fitness_tracker_api.dto;

import com.yuvaansh.fitness_tracker_api.entity.Meal;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class MealResponse {

    private Long id;
    private String name;
    private LocalDate mealDate;
    private Integer calories;
    private BigDecimal protein;
    private BigDecimal carbs;
    private BigDecimal fats;
    private BigDecimal sugar;
    private BigDecimal fiber;
    private BigDecimal sodiumMg;
    private BigDecimal potassiumMg;
    private BigDecimal cholesterolMg;
    private BigDecimal calciumMg;
    private BigDecimal ironMg;
    private BigDecimal vitaminAMcg;
    private BigDecimal vitaminCMg;
    private BigDecimal vitaminDMcg;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public MealResponse() {
    }

    public static MealResponse fromEntity(Meal meal) {
        MealResponse response = new MealResponse();
        response.setId(meal.getId());
        response.setName(meal.getName());
        response.setMealDate(meal.getMealDate());
        response.setCalories(meal.getCalories());
        response.setProtein(meal.getProtein());
        response.setCarbs(meal.getCarbs());
        response.setFats(meal.getFats());
        response.setSugar(meal.getSugar());
        response.setFiber(meal.getFiber());
        response.setSodiumMg(meal.getSodiumMg());
        response.setPotassiumMg(meal.getPotassiumMg());
        response.setCholesterolMg(meal.getCholesterolMg());
        response.setCalciumMg(meal.getCalciumMg());
        response.setIronMg(meal.getIronMg());
        response.setVitaminAMcg(meal.getVitaminAMcg());
        response.setVitaminCMg(meal.getVitaminCMg());
        response.setVitaminDMcg(meal.getVitaminDMcg());
        response.setCreatedAt(meal.getCreatedAt());
        response.setUpdatedAt(meal.getUpdatedAt());
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

    public LocalDate getMealDate() {
        return mealDate;
    }

    public void setMealDate(LocalDate mealDate) {
        this.mealDate = mealDate;
    }

    public Integer getCalories() {
        return calories;
    }

    public void setCalories(Integer calories) {
        this.calories = calories;
    }

    public BigDecimal getProtein() {
        return protein;
    }

    public void setProtein(BigDecimal protein) {
        this.protein = protein;
    }

    public BigDecimal getCarbs() {
        return carbs;
    }

    public void setCarbs(BigDecimal carbs) {
        this.carbs = carbs;
    }

    public BigDecimal getFats() {
        return fats;
    }

    public void setFats(BigDecimal fats) {
        this.fats = fats;
    }

    public BigDecimal getSugar() {
        return sugar;
    }

    public void setSugar(BigDecimal sugar) {
        this.sugar = sugar;
    }

    public BigDecimal getFiber() {
        return fiber;
    }

    public void setFiber(BigDecimal fiber) {
        this.fiber = fiber;
    }

    public BigDecimal getSodiumMg() {
        return sodiumMg;
    }

    public void setSodiumMg(BigDecimal sodiumMg) {
        this.sodiumMg = sodiumMg;
    }

    public BigDecimal getPotassiumMg() {
        return potassiumMg;
    }

    public void setPotassiumMg(BigDecimal potassiumMg) {
        this.potassiumMg = potassiumMg;
    }

    public BigDecimal getCholesterolMg() {
        return cholesterolMg;
    }

    public void setCholesterolMg(BigDecimal cholesterolMg) {
        this.cholesterolMg = cholesterolMg;
    }

    public BigDecimal getCalciumMg() {
        return calciumMg;
    }

    public void setCalciumMg(BigDecimal calciumMg) {
        this.calciumMg = calciumMg;
    }

    public BigDecimal getIronMg() {
        return ironMg;
    }

    public void setIronMg(BigDecimal ironMg) {
        this.ironMg = ironMg;
    }

    public BigDecimal getVitaminAMcg() {
        return vitaminAMcg;
    }

    public void setVitaminAMcg(BigDecimal vitaminAMcg) {
        this.vitaminAMcg = vitaminAMcg;
    }

    public BigDecimal getVitaminCMg() {
        return vitaminCMg;
    }

    public void setVitaminCMg(BigDecimal vitaminCMg) {
        this.vitaminCMg = vitaminCMg;
    }

    public BigDecimal getVitaminDMcg() {
        return vitaminDMcg;
    }

    public void setVitaminDMcg(BigDecimal vitaminDMcg) {
        this.vitaminDMcg = vitaminDMcg;
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
