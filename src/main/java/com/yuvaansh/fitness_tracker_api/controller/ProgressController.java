package com.yuvaansh.fitness_tracker_api.controller;

import com.yuvaansh.fitness_tracker_api.dto.BodyWeightEntryResponse;
import com.yuvaansh.fitness_tracker_api.dto.CreateBodyWeightEntryRequest;
import com.yuvaansh.fitness_tracker_api.dto.ExerciseStrengthTrendResponse;
import com.yuvaansh.fitness_tracker_api.dto.NutritionTrendResponse;
import com.yuvaansh.fitness_tracker_api.service.ProgressService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/api/progress")
public class ProgressController {

    private final ProgressService progressService;

    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

    @PostMapping("/weight")
    public ResponseEntity<BodyWeightEntryResponse> recordWeight(
            Principal principal,
            @Valid @RequestBody CreateBodyWeightEntryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(progressService.recordWeight(principal, request));
    }

    @GetMapping("/weight")
    public ResponseEntity<List<BodyWeightEntryResponse>> getWeightTrend(
            Principal principal,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(progressService.getWeightTrend(principal, from, to));
    }

    @GetMapping("/nutrition")
    public ResponseEntity<List<NutritionTrendResponse>> getNutritionTrend(
            Principal principal,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(progressService.getNutritionTrend(principal, from, to));
    }

    @GetMapping("/exercise/{exerciseId}")
    public ResponseEntity<List<ExerciseStrengthTrendResponse>> getExerciseStrengthTrend(
            Principal principal,
            @PathVariable Long exerciseId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(
                progressService.getExerciseStrengthTrend(principal, exerciseId, from, to));
    }
}
