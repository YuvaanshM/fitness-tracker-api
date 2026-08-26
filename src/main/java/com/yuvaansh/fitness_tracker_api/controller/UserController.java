package com.yuvaansh.fitness_tracker_api.controller;

import com.yuvaansh.fitness_tracker_api.dto.MetricsResponse;
import com.yuvaansh.fitness_tracker_api.dto.UpdateProfileRequest;
import com.yuvaansh.fitness_tracker_api.dto.UserProfileResponse;
import com.yuvaansh.fitness_tracker_api.entity.User;
import com.yuvaansh.fitness_tracker_api.exception.UserNotFoundException;
import com.yuvaansh.fitness_tracker_api.repository.UserRepository;
import com.yuvaansh.fitness_tracker_api.service.MetricsService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    /**
     * Updates the authenticated user's editable profile fields (not username/password/sex).
     */
    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateMe(
            Principal principal,
            @Valid @RequestBody UpdateProfileRequest request) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(UserNotFoundException::new);

        user.setHeight(request.getHeight());
        user.setWeight(request.getWeight());
        user.setActivityLevel(request.getActivityLevel());
        user.setGoal(request.getGoal());
        user.setGoalWeightChangePerWeek(request.getGoalWeightChangePerWeek());
        user.setDateOfBirth(request.getDateOfBirth());

        User saved = userRepository.save(user);
        return ResponseEntity.ok(UserProfileResponse.fromEntity(saved));
    }
}
