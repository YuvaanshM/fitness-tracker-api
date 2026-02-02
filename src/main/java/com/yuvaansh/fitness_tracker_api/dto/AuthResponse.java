package com.yuvaansh.fitness_tracker_api.dto;

/**
 * AuthResponse - Data Transfer Object for authentication responses
 * 
 * This is what we send back to the frontend after login/register
 * We only send safe information (not the password!)
 */
public class AuthResponse {
    
    private String message;
    private Long userId;
    private String username;
    
    public AuthResponse() {
    }
    
    public AuthResponse(String message, Long userId, String username) {
        this.message = message;
        this.userId = userId;
        this.username = username;
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
}
