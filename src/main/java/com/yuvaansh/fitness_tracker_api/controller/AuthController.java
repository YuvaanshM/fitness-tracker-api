package com.yuvaansh.fitness_tracker_api.controller;

import com.yuvaansh.fitness_tracker_api.dto.AuthResponse;
import com.yuvaansh.fitness_tracker_api.dto.LoginRequest;
import com.yuvaansh.fitness_tracker_api.dto.RegisterRequest;
import com.yuvaansh.fitness_tracker_api.entity.User;
import com.yuvaansh.fitness_tracker_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController - Handles authentication endpoints (register and login)
 * 
 * @RestController = This class handles HTTP requests
 * @RequestMapping("/api/auth") = All endpoints start with /api/auth
 * 
 * This controller receives HTTP requests and sends HTTP responses
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    /**
     * @Autowired tells Spring: "Inject the UserRepository here"
     * Spring automatically creates a UserRepository and gives it to us
     * We don't write: UserRepository repo = new UserRepository();
     */
    @Autowired
    private UserRepository userRepository;
    
    /**
     * POST /api/auth/register
     * 
     * This endpoint receives registration data and creates a new user
     * 
     * @RequestBody = Spring converts the JSON from the request into a RegisterRequest object
     * @PostMapping = This method handles POST requests
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        
        // Step 1: Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            // Username taken - return error
            AuthResponse response = new AuthResponse(
                "Username already exists", 
                null, 
                null
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        // Step 2: Create a new User entity from the request
        User newUser = new User(
            request.getUsername(),
            request.getPassword(),  // TODO: Hash password before storing (we'll add this later)
            request.getSex(),
            request.getHeight(),
            request.getWeight(),
            request.getActivityLevel(),
            request.getGoal(),
            request.getGoalWeightChangePerWeek()
        );
        
        // Step 3: Save the user to database
        User savedUser = userRepository.save(newUser);
        
        // Step 4: Return success response
        AuthResponse response = new AuthResponse(
            "User registered successfully",
            savedUser.getId(),
            savedUser.getUsername()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * POST /api/auth/login
     * 
     * This endpoint receives login credentials and verifies them
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        
        // Step 1: Find user by username
        // Remember: findByUsername returns Optional<User>
        var userOpt = userRepository.findByUsername(request.getUsername());
        
        // Step 2: Check if user exists
        if (userOpt.isEmpty()) {
            // User not found - return error
            AuthResponse response = new AuthResponse(
                "Invalid username or password",
                null,
                null
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        
        // Step 3: Extract the User from Optional
        User user = userOpt.get();
        
        // Step 4: Check password
        // TODO: Compare hashed password (we'll add password hashing later)
        if (!user.getPassword().equals(request.getPassword())) {
            // Password doesn't match
            AuthResponse response = new AuthResponse(
                "Invalid username or password",
                null,
                null
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        
        // Step 5: Login successful!
        AuthResponse response = new AuthResponse(
            "Login successful",
            user.getId(),
            user.getUsername()
        );
        
        return ResponseEntity.ok(response);
    }
}
