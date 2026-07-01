package com.yuvaansh.fitness_tracker_api.dto;

/**
 * Minimal error body shape ({@code {"message": "..."}}) shared by non-auth
 * endpoints. Keeps error messages generic per rule.md.
 */
public class ErrorResponse {

    private String message;

    public ErrorResponse() {
    }

    public ErrorResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
