package com.yuvaansh.fitness_tracker_api.service;

import com.yuvaansh.fitness_tracker_api.dto.CreateMealRequest;
import com.yuvaansh.fitness_tracker_api.dto.DailyNutritionSummaryResponse;
import com.yuvaansh.fitness_tracker_api.dto.MealResponse;
import com.yuvaansh.fitness_tracker_api.entity.Meal;
import com.yuvaansh.fitness_tracker_api.entity.User;
import com.yuvaansh.fitness_tracker_api.exception.UserNotFoundException;
import com.yuvaansh.fitness_tracker_api.repository.DailyNutritionSummaryProjection;
import com.yuvaansh.fitness_tracker_api.repository.MealRepository;
import com.yuvaansh.fitness_tracker_api.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Service
public class MealService {

    private final MealRepository mealRepository;
    private final UserRepository userRepository;

    public MealService(MealRepository mealRepository, UserRepository userRepository) {
        this.mealRepository = mealRepository;
        this.userRepository = userRepository;
    }

    public MealResponse createMeal(Principal principal, CreateMealRequest request) {
        User user = resolveUser(principal);
        Meal meal = new Meal();
        meal.setUser(user);
        meal.setName(request.getName().trim());
        meal.setMealDate(request.getMealDate());
        meal.setCalories(request.getCalories());
        meal.setProtein(request.getProtein());
        meal.setCarbs(request.getCarbs());
        meal.setFats(request.getFats());
        meal.setSugar(request.getSugar());
        meal.setFiber(request.getFiber());
        meal.setSodiumMg(request.getSodiumMg());
        meal.setPotassiumMg(request.getPotassiumMg());
        meal.setCholesterolMg(request.getCholesterolMg());
        meal.setCalciumMg(request.getCalciumMg());
        meal.setIronMg(request.getIronMg());
        meal.setVitaminAMcg(request.getVitaminAMcg());
        meal.setVitaminCMg(request.getVitaminCMg());
        meal.setVitaminDMcg(request.getVitaminDMcg());

        Meal saved = mealRepository.save(meal);
        return MealResponse.fromEntity(saved);
    }

    public List<MealResponse> getMealsByDate(Principal principal, LocalDate date) {
        User user = resolveUser(principal);
        return mealRepository.findByUserIdAndMealDateOrderByCreatedAtDesc(user.getId(), date)
                .stream()
                .map(MealResponse::fromEntity)
                .toList();
    }

    public DailyNutritionSummaryResponse getDailySummary(Principal principal, LocalDate date) {
        User user = resolveUser(principal);
        DailyNutritionSummaryProjection summary = mealRepository.summarizeByUserIdAndMealDate(user.getId(), date);

        return new DailyNutritionSummaryResponse(
                date,
                nullToZero(summary == null ? null : summary.getTotalCalories()),
                nullToZero(summary == null ? null : summary.getTotalProtein()),
                nullToZero(summary == null ? null : summary.getTotalCarbs()),
                nullToZero(summary == null ? null : summary.getTotalFats()),
                nullToZero(summary == null ? null : summary.getTotalSugar()),
                nullToZero(summary == null ? null : summary.getTotalFiber())
        );
    }

    private Long nullToZero(Long value) {
        return value == null ? 0L : value;
    }

    private BigDecimal nullToZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private User resolveUser(Principal principal) {
        return userRepository.findByUsername(principal.getName())
                .orElseThrow(UserNotFoundException::new);
    }
}
