package com.yuvaansh.fitness_tracker_api.enums;

/**
 * Activity levels and their TDEE multipliers for the Mifflin-St Jeor equation.
 * Stored on the user as a string; {@link #fromString(String)} provides a
 * whitelist parse so unknown values are rejected rather than silently defaulted.
 */
public enum ActivityLevel {

    SEDENTARY(1.2),
    LIGHTLY_ACTIVE(1.375),
    MODERATELY_ACTIVE(1.55),
    VERY_ACTIVE(1.725),
    EXTRA_ACTIVE(1.9);

    private final double factor;

    ActivityLevel(double factor) {
        this.factor = factor;
    }

    public double getFactor() {
        return factor;
    }

    /**
     * Case-insensitive whitelist lookup. Returns null when the value is null or
     * not a recognized activity level, letting callers decide how to handle it.
     */
    public static ActivityLevel fromString(String value) {
        if (value == null) {
            return null;
        }
        try {
            return ActivityLevel.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
