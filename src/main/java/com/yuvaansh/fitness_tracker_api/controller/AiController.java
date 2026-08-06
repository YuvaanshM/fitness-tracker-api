package com.yuvaansh.fitness_tracker_api.controller;

import com.yuvaansh.fitness_tracker_api.dto.AiTextResponse;
import com.yuvaansh.fitness_tracker_api.dto.ParseMealRequest;
import com.yuvaansh.fitness_tracker_api.dto.ParsedMealResponse;
import com.yuvaansh.fitness_tracker_api.service.AiService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * AI coaching endpoints. All routes require authentication; the provider API
 * key stays on the server.
 */
@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/nutrition-insights")
    public ResponseEntity<AiTextResponse> nutritionInsights(Principal principal) {
        return ResponseEntity.ok(aiService.nutritionInsights(principal));
    }

    @PostMapping("/parse-meal")
    public ResponseEntity<ParsedMealResponse> parseMeal(
            Principal principal,
            @Valid @RequestBody ParseMealRequest request) {
        return ResponseEntity.ok(aiService.parseMeal(principal, request));
    }

    @PostMapping("/workout-suggestion")
    public ResponseEntity<AiTextResponse> workoutSuggestion(Principal principal) {
        return ResponseEntity.ok(aiService.workoutSuggestion(principal));
    }
}
