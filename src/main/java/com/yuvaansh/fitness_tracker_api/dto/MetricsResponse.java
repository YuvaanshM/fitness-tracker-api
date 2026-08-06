package com.yuvaansh.fitness_tracker_api.dto;

/**
 * Computed body-energy metrics for the authenticated user.
 *
 * <ul>
 *   <li>{@code bmr} - Basal Metabolic Rate (kcal/day) via Mifflin-St Jeor.</li>
 *   <li>{@code tdee} - Total Daily Energy Expenditure = BMR x activity factor.</li>
 *   <li>{@code recommendedCalories} - TDEE adjusted for the user's weekly weight goal.</li>
 *   <li>{@code proteinTargetGrams} - suggested daily protein (approx. 1 g per lb bodyweight).</li>
 * </ul>
 */
public class MetricsResponse {

    private int age;
    private double activityFactor;
    private long bmr;
    private long tdee;
    private long recommendedCalories;
    private long proteinTargetGrams;

    public MetricsResponse() {
    }

    public MetricsResponse(int age, double activityFactor, long bmr, long tdee,
                           long recommendedCalories, long proteinTargetGrams) {
        this.age = age;
        this.activityFactor = activityFactor;
        this.bmr = bmr;
        this.tdee = tdee;
        this.recommendedCalories = recommendedCalories;
        this.proteinTargetGrams = proteinTargetGrams;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getActivityFactor() {
        return activityFactor;
    }

    public void setActivityFactor(double activityFactor) {
        this.activityFactor = activityFactor;
    }

    public long getBmr() {
        return bmr;
    }

    public void setBmr(long bmr) {
        this.bmr = bmr;
    }

    public long getTdee() {
        return tdee;
    }

    public void setTdee(long tdee) {
        this.tdee = tdee;
    }

    public long getRecommendedCalories() {
        return recommendedCalories;
    }

    public void setRecommendedCalories(long recommendedCalories) {
        this.recommendedCalories = recommendedCalories;
    }

    public long getProteinTargetGrams() {
        return proteinTargetGrams;
    }

    public void setProteinTargetGrams(long proteinTargetGrams) {
        this.proteinTargetGrams = proteinTargetGrams;
    }
}
