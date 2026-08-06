package com.yuvaansh.fitness_tracker_api.exception;

/**
 * Indicates a syntactically valid request whose values violate a business rule,
 * such as an inverted or excessively large date range.
 */
public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException(String message) {
        super(message);
    }
}
