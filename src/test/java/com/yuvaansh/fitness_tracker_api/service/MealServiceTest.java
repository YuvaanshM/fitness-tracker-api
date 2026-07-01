package com.yuvaansh.fitness_tracker_api.service;

import com.yuvaansh.fitness_tracker_api.dto.CreateMealRequest;
import com.yuvaansh.fitness_tracker_api.dto.MealResponse;
import com.yuvaansh.fitness_tracker_api.entity.Meal;
import com.yuvaansh.fitness_tracker_api.entity.User;
import com.yuvaansh.fitness_tracker_api.exception.UserNotFoundException;
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
    void getMealsByDate_returnsOnlyAuthenticatedUserMeals() {
        Meal meal = new Meal();
        meal.setId(5L);
        meal.setUser(user);
        meal.setName("Chicken Bowl");
        meal.setMealDate(mealDate);
        meal.setCalories(650);
        meal.setProtein(new BigDecimal("45.00"));
        meal.setCarbs(new BigDecimal("55.00"));
        meal.setFats(new BigDecimal("20.00"));
        meal.setCreatedAt(LocalDateTime.now());
        meal.setUpdatedAt(LocalDateTime.now());

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(mealRepository.findByUserIdAndMealDateOrderByCreatedAtDesc(eq(1L), eq(mealDate)))
                .thenReturn(List.of(meal));

        List<MealResponse> results = mealService.getMealsByDate(principal, mealDate);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Chicken Bowl");
        assertThat(results.get(0).getMealDate()).isEqualTo(mealDate);
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
}
