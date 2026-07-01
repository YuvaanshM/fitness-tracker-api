package com.yuvaansh.fitness_tracker_api.controller;

import com.yuvaansh.fitness_tracker_api.dto.CreateExerciseRequest;
import com.yuvaansh.fitness_tracker_api.dto.ExerciseResponse;
import com.yuvaansh.fitness_tracker_api.enums.MuscleGroup;
import com.yuvaansh.fitness_tracker_api.service.ExerciseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {

    private final ExerciseService exerciseService;

    public ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @GetMapping
    public ResponseEntity<List<ExerciseResponse>> listExercises(
            Principal principal,
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "muscle", required = false) MuscleGroup muscle) {
        return ResponseEntity.ok(exerciseService.listExercises(principal, query, muscle));
    }

    @PostMapping
    public ResponseEntity<ExerciseResponse> createExercise(
            Principal principal,
            @Valid @RequestBody CreateExerciseRequest request) {
        ExerciseResponse response = exerciseService.createExercise(principal, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
