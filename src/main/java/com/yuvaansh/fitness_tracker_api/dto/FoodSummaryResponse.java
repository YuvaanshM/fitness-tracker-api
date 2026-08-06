package com.yuvaansh.fitness_tracker_api.dto;

/**
 * Lightweight food search hit from USDA FoodData Central. Macros are per 100 g.
 */
public class FoodSummaryResponse {

    private long fdcId;
    private String description;
    private String brandName;
    private Integer caloriesPer100g;

    public FoodSummaryResponse() {
    }

    public FoodSummaryResponse(long fdcId, String description, String brandName, Integer caloriesPer100g) {
        this.fdcId = fdcId;
        this.description = description;
        this.brandName = brandName;
        this.caloriesPer100g = caloriesPer100g;
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

    public Integer getCaloriesPer100g() {
        return caloriesPer100g;
    }

    public void setCaloriesPer100g(Integer caloriesPer100g) {
        this.caloriesPer100g = caloriesPer100g;
    }
}
