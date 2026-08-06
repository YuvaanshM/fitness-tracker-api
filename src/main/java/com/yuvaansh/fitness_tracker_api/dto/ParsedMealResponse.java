package com.yuvaansh.fitness_tracker_api.dto;

import java.math.BigDecimal;

/**
 * AI-estimated meal data. The client must show this estimate to the user for
 * confirmation before sending it to the normal meal logging endpoint.
 */
public record ParsedMealResponse(
        String name,
        int calories,
        BigDecimal protein,
        BigDecimal carbs,
        BigDecimal fats,
        BigDecimal sugar,
        BigDecimal fiber,
        boolean requiresConfirmation
) {
}
