package com.yuvaansh.fitness_tracker_api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CreateMealRequest {

    @NotBlank
    @Size(max = 200)
    private String name;

    @NotNull
    private LocalDate mealDate;

    @NotNull
    @Positive
    @Max(100000)
    private Integer calories;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @DecimalMax(value = "10000.0")
    private BigDecimal protein;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @DecimalMax(value = "10000.0")
    private BigDecimal carbs;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @DecimalMax(value = "10000.0")
    private BigDecimal fats;

    @DecimalMin(value = "0.0", inclusive = true)
    @DecimalMax(value = "10000.0")
    private BigDecimal sugar;

    @DecimalMin(value = "0.0", inclusive = true)
    @DecimalMax(value = "10000.0")
    private BigDecimal fiber;

    @DecimalMin(value = "0.0", inclusive = true)
    @DecimalMax(value = "1000000.0")
    private BigDecimal sodiumMg;

    @DecimalMin(value = "0.0", inclusive = true)
    @DecimalMax(value = "1000000.0")
    private BigDecimal potassiumMg;

    @DecimalMin(value = "0.0", inclusive = true)
    @DecimalMax(value = "1000000.0")
    private BigDecimal cholesterolMg;

    @DecimalMin(value = "0.0", inclusive = true)
    @DecimalMax(value = "1000000.0")
    private BigDecimal calciumMg;

    @DecimalMin(value = "0.0", inclusive = true)
    @DecimalMax(value = "1000000.0")
    private BigDecimal ironMg;

    @DecimalMin(value = "0.0", inclusive = true)
    @DecimalMax(value = "1000000.0")
    private BigDecimal vitaminAMcg;

    @DecimalMin(value = "0.0", inclusive = true)
    @DecimalMax(value = "1000000.0")
    private BigDecimal vitaminCMg;

    @DecimalMin(value = "0.0", inclusive = true)
    @DecimalMax(value = "1000000.0")
    private BigDecimal vitaminDMcg;

    public CreateMealRequest() {
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
}
