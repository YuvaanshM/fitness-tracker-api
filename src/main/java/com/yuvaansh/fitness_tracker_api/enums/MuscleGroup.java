package com.yuvaansh.fitness_tracker_api.enums;

/**
 * Whitelisted primary muscle groups for an exercise.
 * Stored as a string via {@code @Enumerated(EnumType.STRING)} so the DB stays readable.
 */
public enum MuscleGroup {
    CHEST,
    BACK,
    SHOULDERS,
    BICEPS,
    TRICEPS,
    FOREARMS,
    QUADS,
    HAMSTRINGS,
    GLUTES,
    CALVES,
    CORE,
    FULL_BODY,
    CARDIO,
    OTHER
}
