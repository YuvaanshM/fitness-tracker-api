package com.yuvaansh.fitness_tracker_api.controller;

import com.yuvaansh.fitness_tracker_api.dto.MetricsResponse;
import com.yuvaansh.fitness_tracker_api.dto.UserProfileResponse;
import com.yuvaansh.fitness_tracker_api.repository.UserRepository;
import com.yuvaansh.fitness_tracker_api.service.MetricsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserRepository userRepository;
    private final MetricsService metricsService;

    public UserController(UserRepository userRepository, MetricsService metricsService) {
        this.userRepository = userRepository;
        this.metricsService = metricsService;
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

    /**
     * BMR/TDEE and calorie/protein targets derived from the authenticated user's profile.
     */
    @GetMapping("/metrics")
    public ResponseEntity<MetricsResponse> metrics(Principal principal) {
        return ResponseEntity.ok(metricsService.getMetrics(principal));
    }
}
