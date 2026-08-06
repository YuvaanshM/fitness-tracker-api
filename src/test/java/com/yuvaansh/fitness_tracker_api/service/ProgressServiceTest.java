package com.yuvaansh.fitness_tracker_api.service;

import com.yuvaansh.fitness_tracker_api.dto.BodyWeightEntryResponse;
import com.yuvaansh.fitness_tracker_api.dto.CreateBodyWeightEntryRequest;
import com.yuvaansh.fitness_tracker_api.dto.ExerciseStrengthTrendResponse;
import com.yuvaansh.fitness_tracker_api.dto.NutritionTrendResponse;
import com.yuvaansh.fitness_tracker_api.entity.BodyWeightEntry;
import com.yuvaansh.fitness_tracker_api.entity.Exercise;
import com.yuvaansh.fitness_tracker_api.entity.User;
import com.yuvaansh.fitness_tracker_api.entity.WorkoutSession;
import com.yuvaansh.fitness_tracker_api.entity.WorkoutSet;
import com.yuvaansh.fitness_tracker_api.enums.Equipment;
import com.yuvaansh.fitness_tracker_api.enums.MuscleGroup;
import com.yuvaansh.fitness_tracker_api.exception.InvalidRequestException;
import com.yuvaansh.fitness_tracker_api.exception.ResourceNotFoundException;
import com.yuvaansh.fitness_tracker_api.repository.BodyWeightEntryRepository;
import com.yuvaansh.fitness_tracker_api.repository.ExerciseRepository;
import com.yuvaansh.fitness_tracker_api.repository.MealRepository;
import com.yuvaansh.fitness_tracker_api.repository.NutritionTrendProjection;
import com.yuvaansh.fitness_tracker_api.repository.UserRepository;
import com.yuvaansh.fitness_tracker_api.repository.WorkoutSetRepository;
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
class ProgressServiceTest {

    @Mock private BodyWeightEntryRepository bodyWeightEntryRepository;
    @Mock private MealRepository mealRepository;
    @Mock private WorkoutSetRepository workoutSetRepository;
    @Mock private ExerciseRepository exerciseRepository;
    @Mock private UserRepository userRepository;
    @Mock private Principal principal;

    @InjectMocks
    private ProgressService progressService;

    private User user;
    private LocalDate from;
    private LocalDate to;

    @BeforeEach
    void setUp() {
        user = new User("alice", "hash", "F", 165.0, 140.0, "MODERATELY_ACTIVE", "LOSE", -1.0);
        user.setId(1L);
        from = LocalDate.of(2026, 7, 1);
        to = LocalDate.of(2026, 7, 7);
    }

    private void stubAuthenticatedUser() {
        when(principal.getName()).thenReturn("alice");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
    }

    @Test
    void recordWeight_upsertsForAuthenticatedUser() {
        stubAuthenticatedUser();
        CreateBodyWeightEntryRequest request = new CreateBodyWeightEntryRequest();
        request.setEntryDate(LocalDate.of(2026, 7, 2));
        request.setWeightLbs(new BigDecimal("141.5"));

        when(bodyWeightEntryRepository.findByUserIdAndEntryDate(1L, request.getEntryDate()))
                .thenReturn(Optional.empty());
        when(bodyWeightEntryRepository.save(any(BodyWeightEntry.class))).thenAnswer(inv -> {
            BodyWeightEntry entry = inv.getArgument(0);
            entry.setId(10L);
            entry.setCreatedAt(LocalDateTime.now());
            entry.setUpdatedAt(LocalDateTime.now());
            return entry;
        });

        BodyWeightEntryResponse response = progressService.recordWeight(principal, request);

        ArgumentCaptor<BodyWeightEntry> captor = ArgumentCaptor.forClass(BodyWeightEntry.class);
        verify(bodyWeightEntryRepository).save(captor.capture());
        assertThat(captor.getValue().getUser()).isEqualTo(user);
        assertThat(captor.getValue().getWeightLbs()).isEqualByComparingTo("141.50");
        assertThat(response.getId()).isEqualTo(10L);
    }

    @Test
    void getWeightTrend_rejectsInvertedRange() {
        assertThatThrownBy(() -> progressService.getWeightTrend(principal, to, from))
                .isInstanceOf(InvalidRequestException.class);
    }

    @Test
    void getNutritionTrend_mapsDailyTotals() {
        stubAuthenticatedUser();
        when(mealRepository.summarizeTrendByUserIdAndDateRange(1L, from, to))
                .thenReturn(List.of(trend(from, 2000L, "150.00", "200.00", "60.00")));

        List<NutritionTrendResponse> results = progressService.getNutritionTrend(principal, from, to);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).date()).isEqualTo(from);
        assertThat(results.get(0).calories()).isEqualTo(2000L);
        assertThat(results.get(0).protein()).isEqualByComparingTo("150.00");
    }

    @Test
    void getExerciseStrengthTrend_throwsWhenExerciseInaccessible() {
        stubAuthenticatedUser();
        when(exerciseRepository.findAccessibleById(99L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                progressService.getExerciseStrengthTrend(principal, 99L, from, to))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getExerciseStrengthTrend_aggregatesTopWeightVolumeAndOneRm() {
        stubAuthenticatedUser();
        Exercise exercise = new Exercise(null, "Bench Press", MuscleGroup.CHEST, Equipment.BARBELL);
        exercise.setId(5L);
        when(exerciseRepository.findAccessibleById(5L, 1L)).thenReturn(Optional.of(exercise));

        WorkoutSession session = new WorkoutSession();
        session.setId(20L);
        session.setStartedAt(LocalDateTime.of(2026, 7, 3, 10, 0));

        WorkoutSet set1 = new WorkoutSet();
        set1.setSession(session);
        set1.setExercise(exercise);
        set1.setReps(5);
        set1.setWeightLbs(new BigDecimal("135.00"));
        set1.setCompleted(true);

        WorkoutSet set2 = new WorkoutSet();
        set2.setSession(session);
        set2.setExercise(exercise);
        set2.setReps(3);
        set2.setWeightLbs(new BigDecimal("145.00"));
        set2.setCompleted(true);

        when(workoutSetRepository.findCompletedForExerciseTrend(
                eq(1L), eq(5L), any(), any()))
                .thenReturn(List.of(set1, set2));

        List<ExerciseStrengthTrendResponse> results =
                progressService.getExerciseStrengthTrend(principal, 5L, from, to);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).topWeightLbs()).isEqualByComparingTo("145.00");
        // volume = 135*5 + 145*3 = 1110
        assertThat(results.get(0).totalVolumeLbs()).isEqualByComparingTo("1110.00");
        assertThat(results.get(0).estimatedOneRepMaxLbs()).isGreaterThan(new BigDecimal("145.00"));
    }

    private NutritionTrendProjection trend(
            LocalDate date, Long calories, String protein, String carbs, String fats) {
        return new NutritionTrendProjection() {
            @Override public LocalDate getDate() { return date; }
            @Override public Long getCalories() { return calories; }
            @Override public BigDecimal getProtein() { return new BigDecimal(protein); }
            @Override public BigDecimal getCarbs() { return new BigDecimal(carbs); }
            @Override public BigDecimal getFats() { return new BigDecimal(fats); }
        };
    }
}
