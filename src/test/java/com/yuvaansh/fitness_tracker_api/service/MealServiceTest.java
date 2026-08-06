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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MealServiceTest {

    @Mock
    private MealRepository mealRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FoodService foodService;

    @Mock
    private Principal principal;

    @InjectMocks
    private MealService mealService;

    private User user;
    private LocalDate mealDate;

    @BeforeEach
    void setUp() {
        user = new User("alice", "hash", "F", 165.0, 140.0,
                "MODERATELY_ACTIVE", "LOSE", -1.0);
        user.setId(1L);
        mealDate = LocalDate.of(2026, 6, 28);
        when(principal.getName()).thenReturn("alice");
    }

    @Test
    void createMeal_tiesMealToAuthenticatedUser() {
        CreateMealRequest request = buildRequest();
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(mealRepository.save(any(Meal.class))).thenAnswer(invocation -> {
            Meal meal = invocation.getArgument(0);
            meal.setId(10L);
            meal.setCreatedAt(LocalDateTime.now());
            meal.setUpdatedAt(LocalDateTime.now());
            return meal;
        });

        MealResponse response = mealService.createMeal(principal, request);

        ArgumentCaptor<Meal> captor = ArgumentCaptor.forClass(Meal.class);
        verify(mealRepository).save(captor.capture());
        Meal saved = captor.getValue();

        assertThat(saved.getUser()).isEqualTo(user);
        assertThat(saved.getName()).isEqualTo("Chicken Bowl");
        assertThat(saved.getMealDate()).isEqualTo(mealDate);
        assertThat(saved.getCalories()).isEqualTo(650);
        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getName()).isEqualTo("Chicken Bowl");
    }

    @Test
    void createMeal_throwsWhenUserNotFound() {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mealService.createMeal(principal, buildRequest()))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void createMealFromFood_scalesPer100gMacrosByServings() {
        AddMealFromFoodRequest request = new AddMealFromFoodRequest();
        request.setFdcId(173944L);
        request.setServings(new BigDecimal("1.5")); // 150 g
        request.setMealDate(mealDate);

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(foodService.getFood(173944L)).thenReturn(bananaPer100g());
        when(mealRepository.save(any(Meal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mealService.createMealFromFood(principal, request);

        ArgumentCaptor<Meal> captor = ArgumentCaptor.forClass(Meal.class);
        verify(mealRepository).save(captor.capture());
        Meal saved = captor.getValue();

        assertThat(saved.getUser()).isEqualTo(user);
        assertThat(saved.getName()).isEqualTo("Banana, raw");
        assertThat(saved.getMealDate()).isEqualTo(mealDate);
        // 89 kcal * 1.5 = 133.5 -> 134
        assertThat(saved.getCalories()).isEqualTo(134);
        assertThat(saved.getProtein()).isEqualByComparingTo(new BigDecimal("1.64"));  // 1.09 * 1.5 = 1.635
        assertThat(saved.getCarbs()).isEqualByComparingTo(new BigDecimal("34.31"));   // 22.87 * 1.5 = 34.305
        assertThat(saved.getFats()).isEqualByComparingTo(new BigDecimal("0.50"));     // 0.33 * 1.5 = 0.495
    }

    @Test
    void createMealFromFood_usesNameOverrideAndDefaultsMissingRequiredMacros() {
        AddMealFromFoodRequest request = new AddMealFromFoodRequest();
        request.setFdcId(1L);
        request.setServings(new BigDecimal("2"));
        request.setMealDate(mealDate);
        request.setName("My Snack");

        FoodDetailResponse sparse = new FoodDetailResponse();
        sparse.setDescription("Mystery Food");
        sparse.setCalories(null);
        // protein/carbs/fats intentionally null

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(foodService.getFood(1L)).thenReturn(sparse);
        when(mealRepository.save(any(Meal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mealService.createMealFromFood(principal, request);

        ArgumentCaptor<Meal> captor = ArgumentCaptor.forClass(Meal.class);
        verify(mealRepository).save(captor.capture());
        Meal saved = captor.getValue();

        assertThat(saved.getName()).isEqualTo("My Snack");
        assertThat(saved.getCalories()).isZero();
        assertThat(saved.getProtein()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(saved.getCarbs()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(saved.getFats()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void createMealFromFood_throwsWhenUserNotFound() {
        AddMealFromFoodRequest request = new AddMealFromFoodRequest();
        request.setFdcId(1L);
        request.setServings(BigDecimal.ONE);
        request.setMealDate(mealDate);

        when(userRepository.findByUsername("alice")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mealService.createMealFromFood(principal, request))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void getMealsByDate_returnsOnlyAuthenticatedUserMeals() {
        Meal meal = buildMeal("Chicken Bowl", 650, "45.00", "55.00", "20.00", null, null);

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(mealRepository.findByUserIdAndMealDateOrderByCreatedAtDesc(eq(1L), eq(mealDate)))
                .thenReturn(List.of(meal));

        List<MealResponse> results = mealService.getMealsByDate(principal, mealDate);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Chicken Bowl");
        assertThat(results.get(0).getMealDate()).isEqualTo(mealDate);
    }

    @Test
    void getDailySummary_sumsMealsForAuthenticatedUserAndDate() {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(mealRepository.summarizeByUserIdAndMealDate(eq(1L), eq(mealDate)))
                .thenReturn(summaryProjection(1250L, "90.00", "120.00", "45.00", "20.00", "15.00"));

        DailyNutritionSummaryResponse summary = mealService.getDailySummary(principal, mealDate);

        assertThat(summary.getDate()).isEqualTo(mealDate);
        assertThat(summary.getTotalCalories()).isEqualTo(1250L);
        assertThat(summary.getTotalProtein()).isEqualByComparingTo(new BigDecimal("90.00"));
        assertThat(summary.getTotalCarbs()).isEqualByComparingTo(new BigDecimal("120.00"));
        assertThat(summary.getTotalFats()).isEqualByComparingTo(new BigDecimal("45.00"));
        assertThat(summary.getTotalSugar()).isEqualByComparingTo(new BigDecimal("20.00"));
        assertThat(summary.getTotalFiber()).isEqualByComparingTo(new BigDecimal("15.00"));
        verify(mealRepository).summarizeByUserIdAndMealDate(1L, mealDate);
    }

    @Test
    void getDailySummary_treatsMissingOptionalNutritionAsZero() {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(mealRepository.summarizeByUserIdAndMealDate(eq(1L), eq(mealDate)))
                .thenReturn(summaryProjection(650L, "45.00", "55.00", "20.00", null, null));

        DailyNutritionSummaryResponse summary = mealService.getDailySummary(principal, mealDate);

        assertThat(summary.getTotalCalories()).isEqualTo(650L);
        assertThat(summary.getTotalSugar()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(summary.getTotalFiber()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void getDailySummary_returnsZeroTotalsWhenNoMealsExist() {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(mealRepository.summarizeByUserIdAndMealDate(eq(1L), eq(mealDate)))
                .thenReturn(summaryProjection(null, null, null, null, null, null));

        DailyNutritionSummaryResponse summary = mealService.getDailySummary(principal, mealDate);

        assertThat(summary.getDate()).isEqualTo(mealDate);
        assertThat(summary.getTotalCalories()).isZero();
        assertThat(summary.getTotalProtein()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(summary.getTotalCarbs()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(summary.getTotalFats()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(summary.getTotalSugar()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(summary.getTotalFiber()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void getDailySummary_supportsCalorieTotalsLargerThanIntegerMaxValue() {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(mealRepository.summarizeByUserIdAndMealDate(eq(1L), eq(mealDate)))
                .thenReturn(summaryProjection(3_000_000_000L, "45.00", "55.00", "20.00", "0.00", "0.00"));

        DailyNutritionSummaryResponse summary = mealService.getDailySummary(principal, mealDate);

        assertThat(summary.getTotalCalories()).isEqualTo(3_000_000_000L);
    }

    private FoodDetailResponse bananaPer100g() {
        FoodDetailResponse food = new FoodDetailResponse();
        food.setFdcId(173944L);
        food.setDescription("Banana, raw");
        food.setCalories(89);
        food.setProtein(new BigDecimal("1.09"));
        food.setCarbs(new BigDecimal("22.87"));
        food.setFats(new BigDecimal("0.33"));
        food.setSugar(new BigDecimal("12.23"));
        food.setFiber(new BigDecimal("2.60"));
        return food;
    }

    private CreateMealRequest buildRequest() {
        CreateMealRequest request = new CreateMealRequest();
        request.setName("Chicken Bowl");
        request.setMealDate(mealDate);
        request.setCalories(650);
        request.setProtein(new BigDecimal("45.00"));
        request.setCarbs(new BigDecimal("55.00"));
        request.setFats(new BigDecimal("20.00"));
        return request;
    }

    private Meal buildMeal(
            String name,
            Integer calories,
            String protein,
            String carbs,
            String fats,
            String sugar,
            String fiber) {
        Meal meal = new Meal();
        meal.setId(5L);
        meal.setUser(user);
        meal.setName(name);
        meal.setMealDate(mealDate);
        meal.setCalories(calories);
        meal.setProtein(new BigDecimal(protein));
        meal.setCarbs(new BigDecimal(carbs));
        meal.setFats(new BigDecimal(fats));
        meal.setSugar(sugar == null ? null : new BigDecimal(sugar));
        meal.setFiber(fiber == null ? null : new BigDecimal(fiber));
        meal.setCreatedAt(LocalDateTime.now());
        meal.setUpdatedAt(LocalDateTime.now());
        return meal;
    }

    private DailyNutritionSummaryProjection summaryProjection(
            Long totalCalories,
            String totalProtein,
            String totalCarbs,
            String totalFats,
            String totalSugar,
            String totalFiber) {
        return new DailyNutritionSummaryProjection() {
            @Override
            public Long getTotalCalories() {
                return totalCalories;
            }

            @Override
            public BigDecimal getTotalProtein() {
                return toBigDecimal(totalProtein);
            }

            @Override
            public BigDecimal getTotalCarbs() {
                return toBigDecimal(totalCarbs);
            }

            @Override
            public BigDecimal getTotalFats() {
                return toBigDecimal(totalFats);
            }

            @Override
            public BigDecimal getTotalSugar() {
                return toBigDecimal(totalSugar);
            }

            @Override
            public BigDecimal getTotalFiber() {
                return toBigDecimal(totalFiber);
            }
        };
    }

    private BigDecimal toBigDecimal(String value) {
        return value == null ? null : new BigDecimal(value);
    }
}
