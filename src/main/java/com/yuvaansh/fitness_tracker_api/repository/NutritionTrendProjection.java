package com.yuvaansh.fitness_tracker_api.repository;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface NutritionTrendProjection {

    LocalDate getDate();

    Long getCalories();

    BigDecimal getProtein();

    BigDecimal getCarbs();

    BigDecimal getFats();
}
