package com.yuvaansh.fitness_tracker_api.dto;

import java.math.BigDecimal;

/**
 * Full macro/micronutrient breakdown for a single USDA food, expressed per 100 g.
 * Mirrors the fields of {@link com.yuvaansh.fitness_tracker_api.entity.Meal} so a
 * meal can be created directly from a scaled copy of these values.
 */
public class FoodDetailResponse {

    private long fdcId;
    private String description;
    private String brandName;

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

    public FoodDetailResponse() {
    }

    public long getFdcId() {
        return fdcId;
    }

    public void setFdcId(long fdcId) {
        this.fdcId = fdcId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
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
