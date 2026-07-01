package com.yuvaansh.fitness_tracker_api.controller;

import com.yuvaansh.fitness_tracker_api.dto.FinishWorkoutRequest;
import com.yuvaansh.fitness_tracker_api.dto.LogSetRequest;
import com.yuvaansh.fitness_tracker_api.dto.StartWorkoutRequest;
import com.yuvaansh.fitness_tracker_api.dto.WorkoutSessionResponse;
import com.yuvaansh.fitness_tracker_api.service.WorkoutService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/workouts")
public class WorkoutController {

    private final WorkoutService workoutService;

    public WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    @PostMapping
    public ResponseEntity<WorkoutSessionResponse> startWorkout(
            Principal principal,
            @Valid @RequestBody StartWorkoutRequest request) {
        WorkoutSessionResponse response = workoutService.startWorkout(principal, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{sessionId}/sets")
    public ResponseEntity<WorkoutSessionResponse> logSet(
            Principal principal,
            @PathVariable Long sessionId,
            @Valid @RequestBody LogSetRequest request) {
        WorkoutSessionResponse response = workoutService.logSet(principal, sessionId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{sessionId}")
    public ResponseEntity<WorkoutSessionResponse> finishWorkout(
            Principal principal,
            @PathVariable Long sessionId,
            @Valid @RequestBody FinishWorkoutRequest request) {
        return ResponseEntity.ok(workoutService.finishWorkout(principal, sessionId, request));
    }

    @GetMapping
    public ResponseEntity<List<WorkoutSessionResponse>> listWorkouts(
            Principal principal,
            @RequestParam(value = "from", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(value = "to", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(workoutService.listWorkouts(principal, from, to));
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<WorkoutSessionResponse> getWorkout(
            Principal principal,
            @PathVariable Long sessionId) {
        return ResponseEntity.ok(workoutService.getWorkout(principal, sessionId));
    }
}
