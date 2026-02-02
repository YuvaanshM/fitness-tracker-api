package com.yuvaansh.fitness_tracker_api.dto;

/**
 * LoginRequest - Data Transfer Object for user login
 * 
 * Simple class that holds username and password from the login request
 */
public class LoginRequest {
    
    private String username;
    private String password;
    
    public LoginRequest() {
    }
    
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}
