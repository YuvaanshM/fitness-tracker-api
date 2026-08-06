package com.yuvaansh.fitness_tracker_api.service;

import com.yuvaansh.fitness_tracker_api.client.AiClient;
import com.yuvaansh.fitness_tracker_api.dto.AiTextResponse;
import com.yuvaansh.fitness_tracker_api.dto.ParseMealRequest;
import com.yuvaansh.fitness_tracker_api.dto.ParsedMealResponse;
import com.yuvaansh.fitness_tracker_api.entity.User;
import com.yuvaansh.fitness_tracker_api.exception.ExternalServiceException;
import com.yuvaansh.fitness_tracker_api.exception.InvalidRequestException;
import com.yuvaansh.fitness_tracker_api.exception.UserNotFoundException;
import com.yuvaansh.fitness_tracker_api.repository.MealRepository;
import com.yuvaansh.fitness_tracker_api.repository.RoutineRepository;
import com.yuvaansh.fitness_tracker_api.repository.UserRepository;
import com.yuvaansh.fitness_tracker_api.repository.WorkoutSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiServiceTest {

    @Mock private AiClient aiClient;
    @Mock private UserRepository userRepository;
    @Mock private MealRepository mealRepository;
    @Mock private WorkoutSessionRepository workoutSessionRepository;
    @Mock private RoutineRepository routineRepository;
    @Mock private MetricsService metricsService;
    @Mock private Principal principal;

    private AiService aiService;
    private User user;
    private final Clock clock = Clock.fixed(Instant.parse("2026-07-15T12:00:00Z"), ZoneOffset.UTC);

    @BeforeEach
    void setUp() {
        aiService = new AiService(
                aiClient,
                userRepository,
                mealRepository,
                workoutSessionRepository,
                routineRepository,
                metricsService,
                clock);
        user = new User("alice", "hash", "F", 165.0, 140.0, "MODERATELY_ACTIVE", "LOSE", -1.0,
                LocalDate.of(1996, 5, 15));
        user.setId(1L);
    }

    @Test
    void nutritionInsights_usesAuthenticatedUserData() {
        when(principal.getName()).thenReturn("alice");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(mealRepository.summarizeTrendByUserIdAndDateRange(eq(1L), any(), any()))
                .thenReturn(List.of());
        when(aiClient.generate(anyString(), anyString())).thenReturn("Eat more protein.");

        AiTextResponse response = aiService.nutritionInsights(principal);

        assertThat(response.content()).isEqualTo("Eat more protein.");
        verify(mealRepository).summarizeTrendByUserIdAndDateRange(
                1L, LocalDate.of(2026, 7, 9), LocalDate.of(2026, 7, 15));
    }

    @Test
    void parseMeal_parsesJsonPayload() {
        when(principal.getName()).thenReturn("alice");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(aiClient.generate(anyString(), anyString())).thenReturn("""
                {"name":"Eggs and toast","calories":420,"protein":28,"carbs":30,"fats":18,"sugar":4,"fiber":3}
                """);

        ParseMealRequest request = new ParseMealRequest();
        request.setText("2 eggs and toast");

        ParsedMealResponse parsed = aiService.parseMeal(principal, request);

        assertThat(parsed.name()).isEqualTo("Eggs and toast");
        assertThat(parsed.calories()).isEqualTo(420);
        assertThat(parsed.protein()).isEqualByComparingTo("28.00");
        assertThat(parsed.requiresConfirmation()).isTrue();
    }

    @Test
    void parseMeal_rejectsInvalidJsonFromProvider() {
        when(principal.getName()).thenReturn("alice");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(aiClient.generate(anyString(), anyString())).thenReturn("not-json");

        ParseMealRequest request = new ParseMealRequest();
        request.setText("pizza");

        assertThatThrownBy(() -> aiService.parseMeal(principal, request))
                .isInstanceOf(ExternalServiceException.class);
    }

    @Test
    void workoutSuggestion_throwsWhenUserMissing() {
        when(principal.getName()).thenReturn("ghost");
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> aiService.workoutSuggestion(principal))
                .isInstanceOf(UserNotFoundException.class);
        verify(aiClient, never()).generate(anyString(), anyString());
    }

    @Test
    void nutritionInsights_rateLimitsPerUser() {
        when(principal.getName()).thenReturn("alice");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(mealRepository.summarizeTrendByUserIdAndDateRange(eq(1L), any(), any()))
                .thenReturn(List.of());
        when(aiClient.generate(anyString(), anyString())).thenReturn("ok");

        for (int i = 0; i < 10; i++) {
            aiService.nutritionInsights(principal);
        }

        assertThatThrownBy(() -> aiService.nutritionInsights(principal))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("Too many AI requests");
    }
}
