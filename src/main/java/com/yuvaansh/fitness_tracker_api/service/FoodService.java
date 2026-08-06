package com.yuvaansh.fitness_tracker_api.service;

import com.yuvaansh.fitness_tracker_api.client.UsdaFoodClient;
import com.yuvaansh.fitness_tracker_api.dto.FoodDetailResponse;
import com.yuvaansh.fitness_tracker_api.dto.FoodSummaryResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Food catalog lookups backed by USDA FoodData Central. Results are cached
 * (see {@code spring.cache.*}) to stay within USDA rate limits.
 */
@Service
public class FoodService {

    private final UsdaFoodClient usdaFoodClient;

    public FoodService(UsdaFoodClient usdaFoodClient) {
        this.usdaFoodClient = usdaFoodClient;
    }

    @Cacheable(cacheNames = "foodSearch", key = "#query.trim().toLowerCase() + '|' + #pageSize")
    public List<FoodSummaryResponse> search(String query, int pageSize) {
        if (!StringUtils.hasText(query)) {
            return List.of();
        }
        return usdaFoodClient.searchFoods(query.trim(), pageSize);
    }

    @Cacheable(cacheNames = "foodDetail", key = "#fdcId")
    public FoodDetailResponse getFood(long fdcId) {
        return usdaFoodClient.getFood(fdcId);
    }
}
