package com.yuvaansh.fitness_tracker_api.repository;

import java.math.BigDecimal;

public interface DailyNutritionSummaryProjection {

    Long getTotalCalories();

    BigDecimal getTotalProtein();

    BigDecimal getTotalCarbs();

    BigDecimal getTotalFats();

    BigDecimal getTotalSugar();

    BigDecimal getTotalFiber();
}
