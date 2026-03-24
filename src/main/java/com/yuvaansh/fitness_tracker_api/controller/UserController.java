package com.yuvaansh.fitness_tracker_api.controller;

import com.yuvaansh.fitness_tracker_api.dto.UserProfileResponse;
import com.yuvaansh.fitness_tracker_api.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Example protected route: requires a valid JWT (see JwtAuthenticationFilter).
     */
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> me(Principal principal) {
        return userRepository.findByUsername(principal.getName())
                .map(u -> ResponseEntity.ok(UserProfileResponse.fromEntity(u)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
