package com.yuvaansh.fitness_tracker_api.service;

import com.yuvaansh.fitness_tracker_api.dto.AddMealFromFoodRequest;
import com.yuvaansh.fitness_tracker_api.dto.CreateMealRequest;
import com.yuvaansh.fitness_tracker_api.dto.DailyNutritionSummaryResponse;
import com.yuvaansh.fitness_tracker_api.dto.FoodDetailResponse;
import com.yuvaansh.fitness_tracker_api.dto.MealResponse;
import com.yuvaansh.fitness_tracker_api.entity.Meal;
import com.yuvaansh.fitness_tracker_api.entity.User;
import com.yuvaansh.fitness_tracker_api.exception.UserNotFoundException;
import com.yuvaansh.fitness_tracker_api.repository.DailyNutritionSummaryProjection;
import com.yuvaansh.fitness_tracker_api.repository.MealRepository;
import com.yuvaansh.fitness_tracker_api.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Service
public class MealService {

    private final MealRepository mealRepository;
    private final UserRepository userRepository;
    private final FoodService foodService;

    public MealService(MealRepository mealRepository, UserRepository userRepository, FoodService foodService) {
        this.mealRepository = mealRepository;
        this.userRepository = userRepository;
        this.foodService = foodService;
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

    /**
     * Logs a meal by fetching a USDA food's per-100 g macros and scaling them by
     * {@code servings} (e.g. 1.5 = 150 g). Micronutrients are copied when present.
     */
    public MealResponse createMealFromFood(Principal principal, AddMealFromFoodRequest request) {
        User user = resolveUser(principal);
        FoodDetailResponse food = foodService.getFood(request.getFdcId());
        BigDecimal servings = request.getServings();

        Meal meal = new Meal();
        meal.setUser(user);
        meal.setName(resolveName(request.getName(), food.getDescription()));
        meal.setMealDate(request.getMealDate());
        meal.setCalories(scaleCalories(food.getCalories(), servings));
        meal.setProtein(scaleRequired(food.getProtein(), servings));
        meal.setCarbs(scaleRequired(food.getCarbs(), servings));
        meal.setFats(scaleRequired(food.getFats(), servings));
        meal.setSugar(scale(food.getSugar(), servings));
        meal.setFiber(scale(food.getFiber(), servings));
        meal.setSodiumMg(scale(food.getSodiumMg(), servings));
        meal.setPotassiumMg(scale(food.getPotassiumMg(), servings));
        meal.setCholesterolMg(scale(food.getCholesterolMg(), servings));
        meal.setCalciumMg(scale(food.getCalciumMg(), servings));
        meal.setIronMg(scale(food.getIronMg(), servings));
        meal.setVitaminAMcg(scale(food.getVitaminAMcg(), servings));
        meal.setVitaminCMg(scale(food.getVitaminCMg(), servings));
        meal.setVitaminDMcg(scale(food.getVitaminDMcg(), servings));

        Meal saved = mealRepository.save(meal);
        return MealResponse.fromEntity(saved);
    }

    private String resolveName(String override, String foodDescription) {
        if (StringUtils.hasText(override)) {
            return override.trim();
        }
        String name = StringUtils.hasText(foodDescription) ? foodDescription.trim() : "Food";
        return name.length() > 200 ? name.substring(0, 200) : name;
    }

    private Integer scaleCalories(Integer per100g, BigDecimal servings) {
        if (per100g == null) {
            return 0;
        }
        return BigDecimal.valueOf(per100g).multiply(servings).setScale(0, RoundingMode.HALF_UP).intValue();
    }

    private BigDecimal scale(BigDecimal per100g, BigDecimal servings) {
        if (per100g == null) {
            return null;
        }
        return per100g.multiply(servings).setScale(2, RoundingMode.HALF_UP);
    }

    // For macros the Meal entity requires (protein/carbs/fats): default missing to zero.
    private BigDecimal scaleRequired(BigDecimal per100g, BigDecimal servings) {
        BigDecimal scaled = scale(per100g, servings);
        return scaled == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : scaled;
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
