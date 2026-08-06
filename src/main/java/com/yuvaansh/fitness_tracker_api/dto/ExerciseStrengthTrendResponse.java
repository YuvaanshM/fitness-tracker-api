package com.yuvaansh.fitness_tracker_api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ExerciseStrengthTrendResponse(
        Long workoutSessionId,
        LocalDateTime performedAt,
        BigDecimal topWeightLbs,
        BigDecimal totalVolumeLbs,
        BigDecimal estimatedOneRepMaxLbs
) {
}
