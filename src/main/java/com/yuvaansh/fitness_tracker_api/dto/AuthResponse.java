package com.yuvaansh.fitness_tracker_api.dto;

/**
 * Response after register/login. Includes a JWT when successful.
 * Never includes the password.
 */
public class AuthResponse {

    private String message;
    private Long userId;
    private String username;
    /** Bearer token; null on error responses */
    private String token;

    public AuthResponse() {
    }

    public AuthResponse(String message, Long userId, String username) {
        this.message = message;
        this.userId = userId;
        this.username = username;
    }

    public AuthResponse(String message, Long userId, String username, String token) {
        this.message = message;
        this.userId = userId;
        this.username = username;
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
