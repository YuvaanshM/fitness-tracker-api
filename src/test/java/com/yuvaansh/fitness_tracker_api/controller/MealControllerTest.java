package com.yuvaansh.fitness_tracker_api.controller;

import com.yuvaansh.fitness_tracker_api.dto.AddMealFromFoodRequest;
import com.yuvaansh.fitness_tracker_api.dto.CreateMealRequest;
import com.yuvaansh.fitness_tracker_api.dto.DailyNutritionSummaryResponse;
import com.yuvaansh.fitness_tracker_api.dto.MealResponse;
import com.yuvaansh.fitness_tracker_api.exception.GlobalExceptionHandler;
import com.yuvaansh.fitness_tracker_api.service.MealService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MealControllerTest {

    @Mock
    private MealService mealService;

    @InjectMocks
    private MealController mealController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(mealController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void createMeal_returnsCreated() throws Exception {
        when(mealService.createMeal(any(Principal.class), any(CreateMealRequest.class)))
                .thenReturn(buildResponse());

        mockMvc.perform(post("/api/meals")
                        .principal(() -> "alice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validMealJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Chicken Bowl"))
                .andExpect(jsonPath("$.calories").value(650));
    }

    @Test
    void createMeal_withInvalidBody_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/meals")
                        .principal(() -> "alice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void createMealFromFood_returnsCreated() throws Exception {
        when(mealService.createMealFromFood(any(Principal.class), any(AddMealFromFoodRequest.class)))
                .thenReturn(buildResponse());

        mockMvc.perform(post("/api/meals/from-food")
                        .principal(() -> "alice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"fdcId": 173944, "servings": 1.5, "mealDate": "2026-06-28"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Chicken Bowl"));
    }

    @Test
    void createMealFromFood_withInvalidBody_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/meals/from-food")
                        .principal(() -> "alice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"servings\": -1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getMealsByDate_returnsMealsForDate() throws Exception {
        when(mealService.getMealsByDate(any(Principal.class), eq(LocalDate.of(2026, 6, 28))))
                .thenReturn(List.of(buildResponse()));

        mockMvc.perform(get("/api/meals")
                        .principal(() -> "alice")
                        .param("date", "2026-06-28"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Chicken Bowl"))
                .andExpect(jsonPath("$[0].mealDate").value("2026-06-28"));
    }

    @Test
    void getDailySummary_returnsTotalsForDate() throws Exception {
        when(mealService.getDailySummary(any(Principal.class), eq(LocalDate.of(2026, 6, 28))))
                .thenReturn(buildSummaryResponse());

        mockMvc.perform(get("/api/meals/summary")
                        .principal(() -> "alice")
                        .param("date", "2026-06-28"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value("2026-06-28"))
                .andExpect(jsonPath("$.totalCalories").value(1250))
                .andExpect(jsonPath("$.totalProtein").value(90.0))
                .andExpect(jsonPath("$.totalCarbs").value(120.0))
                .andExpect(jsonPath("$.totalFats").value(45.0))
                .andExpect(jsonPath("$.totalSugar").value(20.0))
                .andExpect(jsonPath("$.totalFiber").value(15.0));
    }

    private String validMealJson() {
        return """
                {
                  "name": "Chicken Bowl",
                  "mealDate": "2026-06-28",
                  "calories": 650,
                  "protein": 45.00,
                  "carbs": 55.00,
                  "fats": 20.00
                }
                """;
    }

    private MealResponse buildResponse() {
        MealResponse response = new MealResponse();
        response.setId(1L);
        response.setName("Chicken Bowl");
        response.setMealDate(LocalDate.of(2026, 6, 28));
        response.setCalories(650);
        response.setProtein(new BigDecimal("45.00"));
        response.setCarbs(new BigDecimal("55.00"));
        response.setFats(new BigDecimal("20.00"));
        response.setCreatedAt(LocalDateTime.of(2026, 6, 28, 12, 0));
        response.setUpdatedAt(LocalDateTime.of(2026, 6, 28, 12, 0));
        return response;
    }

    private DailyNutritionSummaryResponse buildSummaryResponse() {
        return new DailyNutritionSummaryResponse(
                LocalDate.of(2026, 6, 28),
                1250L,
                new BigDecimal("90.00"),
                new BigDecimal("120.00"),
                new BigDecimal("45.00"),
                new BigDecimal("20.00"),
                new BigDecimal("15.00")
        );
    }
}
