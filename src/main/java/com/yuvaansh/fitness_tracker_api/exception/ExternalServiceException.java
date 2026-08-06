package com.yuvaansh.fitness_tracker_api.exception;

/**
 * Thrown when an upstream third-party service (e.g. USDA FoodData Central) is
 * unavailable or returns an unexpected response. Mapped to HTTP 502.
 */
public class ExternalServiceException extends RuntimeException {

    public ExternalServiceException(String message) {
        super(message);
    }

    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
