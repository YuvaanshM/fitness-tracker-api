package com.yuvaansh.fitness_tracker_api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record NutritionTrendResponse(
        LocalDate date,
        long calories,
        BigDecimal protein,
        BigDecimal carbs,
        BigDecimal fats
) {
}
