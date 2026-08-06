package com.yuvaansh.fitness_tracker_api.controller;

import com.yuvaansh.fitness_tracker_api.dto.AddMealFromFoodRequest;
import com.yuvaansh.fitness_tracker_api.dto.CreateMealRequest;
import com.yuvaansh.fitness_tracker_api.dto.DailyNutritionSummaryResponse;
import com.yuvaansh.fitness_tracker_api.dto.MealResponse;
import com.yuvaansh.fitness_tracker_api.service.MealService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/meals")
public class MealController {

    private final MealService mealService;

    public MealController(MealService mealService) {
        this.mealService = mealService;
    }

    @PostMapping
    public ResponseEntity<MealResponse> createMeal(
            Principal principal,
            @Valid @RequestBody CreateMealRequest request) {
        MealResponse response = mealService.createMeal(principal, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/from-food")
    public ResponseEntity<MealResponse> createMealFromFood(
            Principal principal,
            @Valid @RequestBody AddMealFromFoodRequest request) {
        MealResponse response = mealService.createMealFromFood(principal, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/summary")
    public ResponseEntity<DailyNutritionSummaryResponse> getDailySummary(
            Principal principal,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        DailyNutritionSummaryResponse summary = mealService.getDailySummary(principal, date);
        return ResponseEntity.ok(summary);
    }

    @GetMapping
    public ResponseEntity<List<MealResponse>> getMealsByDate(
            Principal principal,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<MealResponse> meals = mealService.getMealsByDate(principal, date);
        return ResponseEntity.ok(meals);
    }
}
