package com.yuvaansh.fitness_tracker_api.controller;

import com.yuvaansh.fitness_tracker_api.dto.FoodDetailResponse;
import com.yuvaansh.fitness_tracker_api.dto.FoodSummaryResponse;
import com.yuvaansh.fitness_tracker_api.service.FoodService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Read-only food catalog endpoints backed by USDA FoodData Central.
 * All routes require authentication (see SecurityConfig).
 */
@RestController
@RequestMapping("/api/foods")
public class FoodController {

    private static final int DEFAULT_PAGE_SIZE = 25;
    private static final int MAX_PAGE_SIZE = 50;

    private final FoodService foodService;

    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<FoodSummaryResponse>> search(
            @RequestParam("query") String query,
            @RequestParam(value = "pageSize", defaultValue = "25") int pageSize) {
        int clamped = pageSize <= 0 ? DEFAULT_PAGE_SIZE : Math.min(pageSize, MAX_PAGE_SIZE);
        return ResponseEntity.ok(foodService.search(query, clamped));
    }

    @GetMapping("/{fdcId}")
    public ResponseEntity<FoodDetailResponse> getFood(@PathVariable("fdcId") long fdcId) {
        return ResponseEntity.ok(foodService.getFood(fdcId));
    }
}
