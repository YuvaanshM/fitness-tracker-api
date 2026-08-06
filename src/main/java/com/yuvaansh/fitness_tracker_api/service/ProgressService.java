package com.yuvaansh.fitness_tracker_api.service;

import com.yuvaansh.fitness_tracker_api.dto.BodyWeightEntryResponse;
import com.yuvaansh.fitness_tracker_api.dto.CreateBodyWeightEntryRequest;
import com.yuvaansh.fitness_tracker_api.dto.ExerciseStrengthTrendResponse;
import com.yuvaansh.fitness_tracker_api.dto.NutritionTrendResponse;
import com.yuvaansh.fitness_tracker_api.entity.BodyWeightEntry;
import com.yuvaansh.fitness_tracker_api.entity.User;
import com.yuvaansh.fitness_tracker_api.entity.WorkoutSet;
import com.yuvaansh.fitness_tracker_api.exception.InvalidRequestException;
import com.yuvaansh.fitness_tracker_api.exception.ResourceNotFoundException;
import com.yuvaansh.fitness_tracker_api.exception.UserNotFoundException;
import com.yuvaansh.fitness_tracker_api.repository.BodyWeightEntryRepository;
import com.yuvaansh.fitness_tracker_api.repository.ExerciseRepository;
import com.yuvaansh.fitness_tracker_api.repository.MealRepository;
import com.yuvaansh.fitness_tracker_api.repository.UserRepository;
import com.yuvaansh.fitness_tracker_api.repository.WorkoutSetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ProgressService {

    private static final long MAX_RANGE_DAYS = 366;
    private static final BigDecimal THIRTY = BigDecimal.valueOf(30);

    private final BodyWeightEntryRepository bodyWeightEntryRepository;
    private final MealRepository mealRepository;
    private final WorkoutSetRepository workoutSetRepository;
    private final ExerciseRepository exerciseRepository;
    private final UserRepository userRepository;

    public ProgressService(
            BodyWeightEntryRepository bodyWeightEntryRepository,
            MealRepository mealRepository,
            WorkoutSetRepository workoutSetRepository,
            ExerciseRepository exerciseRepository,
            UserRepository userRepository) {
        this.bodyWeightEntryRepository = bodyWeightEntryRepository;
        this.mealRepository = mealRepository;
        this.workoutSetRepository = workoutSetRepository;
        this.exerciseRepository = exerciseRepository;
        this.userRepository = userRepository;
    }

    public BodyWeightEntryResponse recordWeight(
            Principal principal,
            CreateBodyWeightEntryRequest request) {
        User user = resolveUser(principal);
        BodyWeightEntry entry = bodyWeightEntryRepository
                .findByUserIdAndEntryDate(user.getId(), request.getEntryDate())
                .orElseGet(BodyWeightEntry::new);
        entry.setUser(user);
        entry.setEntryDate(request.getEntryDate());
        entry.setWeightLbs(request.getWeightLbs().setScale(2, RoundingMode.HALF_UP));
        return BodyWeightEntryResponse.fromEntity(bodyWeightEntryRepository.save(entry));
    }

    @Transactional(readOnly = true)
    public List<BodyWeightEntryResponse> getWeightTrend(
            Principal principal,
            LocalDate from,
            LocalDate to) {
        validateRange(from, to);
        User user = resolveUser(principal);
        return bodyWeightEntryRepository
                .findByUserIdAndEntryDateBetweenOrderByEntryDateAsc(user.getId(), from, to)
                .stream()
                .map(BodyWeightEntryResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NutritionTrendResponse> getNutritionTrend(
            Principal principal,
            LocalDate from,
            LocalDate to) {
        validateRange(from, to);
        User user = resolveUser(principal);
        return mealRepository.summarizeTrendByUserIdAndDateRange(user.getId(), from, to)
                .stream()
                .map(row -> new NutritionTrendResponse(
                        row.getDate(),
                        valueOrZero(row.getCalories()),
                        valueOrZero(row.getProtein()),
                        valueOrZero(row.getCarbs()),
                        valueOrZero(row.getFats())))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ExerciseStrengthTrendResponse> getExerciseStrengthTrend(
            Principal principal,
            Long exerciseId,
            LocalDate from,
            LocalDate to) {
        validateRange(from, to);
        User user = resolveUser(principal);
        exerciseRepository.findAccessibleById(exerciseId, user.getId())
                .orElseThrow(ResourceNotFoundException::new);

        List<WorkoutSet> sets = workoutSetRepository.findCompletedForExerciseTrend(
                user.getId(),
                exerciseId,
                from.atStartOfDay(),
                to.atTime(LocalTime.MAX));

        Map<Long, List<WorkoutSet>> bySession = new LinkedHashMap<>();
        for (WorkoutSet set : sets) {
            bySession.computeIfAbsent(set.getSession().getId(), ignored -> new ArrayList<>())
                    .add(set);
        }

        return bySession.values().stream()
                .map(this::toStrengthPoint)
                .toList();
    }

    private ExerciseStrengthTrendResponse toStrengthPoint(List<WorkoutSet> sets) {
        WorkoutSet first = sets.get(0);
        BigDecimal topWeight = BigDecimal.ZERO;
        BigDecimal totalVolume = BigDecimal.ZERO;
        BigDecimal estimatedOneRepMax = BigDecimal.ZERO;

        for (WorkoutSet set : sets) {
            BigDecimal weight = set.getWeightLbs();
            BigDecimal reps = BigDecimal.valueOf(set.getReps());
            topWeight = topWeight.max(weight);
            totalVolume = totalVolume.add(weight.multiply(reps));
            BigDecimal setEstimate = weight.multiply(BigDecimal.ONE.add(reps.divide(
                    THIRTY,
                    8,
                    RoundingMode.HALF_UP)));
            estimatedOneRepMax = estimatedOneRepMax.max(setEstimate);
        }

        return new ExerciseStrengthTrendResponse(
                first.getSession().getId(),
                first.getSession().getStartedAt(),
                topWeight.setScale(2, RoundingMode.HALF_UP),
                totalVolume.setScale(2, RoundingMode.HALF_UP),
                estimatedOneRepMax.setScale(2, RoundingMode.HALF_UP));
    }

    private void validateRange(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new InvalidRequestException("Both from and to dates are required");
        }
        if (from.isAfter(to)) {
            throw new InvalidRequestException("from must be on or before to");
        }
        if (ChronoUnit.DAYS.between(from, to) > MAX_RANGE_DAYS) {
            throw new InvalidRequestException("Date range cannot exceed 366 days");
        }
    }

    private long valueOrZero(Long value) {
        return value == null ? 0L : value;
    }

    private BigDecimal valueOrZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private User resolveUser(Principal principal) {
        return userRepository.findByUsername(principal.getName())
                .orElseThrow(UserNotFoundException::new);
    }
}
