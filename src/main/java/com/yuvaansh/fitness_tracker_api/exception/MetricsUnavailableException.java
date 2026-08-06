package com.yuvaansh.fitness_tracker_api.exception;

/**
 * Thrown when BMR/TDEE metrics cannot be computed for a user, e.g. their profile
 * is missing a date of birth or has an unrecognized activity level.
 */
public class MetricsUnavailableException extends RuntimeException {

    public MetricsUnavailableException(String message) {
        super(message);
    }
}
