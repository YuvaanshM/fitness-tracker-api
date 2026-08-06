package com.yuvaansh.fitness_tracker_api.service;

import com.yuvaansh.fitness_tracker_api.dto.MetricsResponse;
import com.yuvaansh.fitness_tracker_api.entity.User;
import com.yuvaansh.fitness_tracker_api.enums.ActivityLevel;
import com.yuvaansh.fitness_tracker_api.exception.MetricsUnavailableException;
import com.yuvaansh.fitness_tracker_api.exception.UserNotFoundException;
import com.yuvaansh.fitness_tracker_api.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.time.Period;

/**
 * Computes body-energy metrics (BMR, TDEE, calorie/protein targets) for the
 * authenticated user using the Mifflin-St Jeor equation.
 */
@Service
public class MetricsService {

    private static final double LBS_PER_KG = 2.2046;
    private static final double CALORIES_PER_POUND = 3500.0;
    private static final double PROTEIN_GRAMS_PER_LB = 1.0;

    private final UserRepository userRepository;

    public MetricsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public MetricsResponse getMetrics(Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(UserNotFoundException::new);
        return computeMetrics(user);
    }

    /**
     * Package-private so it can be unit tested directly with a constructed user.
     */
    MetricsResponse computeMetrics(User user) {
        if (user.getDateOfBirth() == null) {
            throw new MetricsUnavailableException("Date of birth is required to compute metrics");
        }
        ActivityLevel activityLevel = ActivityLevel.fromString(user.getActivityLevel());
        if (activityLevel == null) {
            throw new MetricsUnavailableException("Unknown activity level: " + user.getActivityLevel());
        }
        if (user.getWeight() == null || user.getHeight() == null || user.getSex() == null) {
            throw new MetricsUnavailableException("Profile is incomplete for metric computation");
        }

        int age = ageInYears(user.getDateOfBirth());
        if (age <= 0) {
            throw new MetricsUnavailableException("Date of birth must be in the past");
        }

        double weightKg = user.getWeight() / LBS_PER_KG;
        double heightCm = user.getHeight();

        // Mifflin-St Jeor: base term is shared, sex only changes the constant.
        double base = (10 * weightKg) + (6.25 * heightCm) - (5 * age);
        double bmr = isMale(user.getSex()) ? base + 5 : base - 161;

        double tdee = bmr * activityLevel.getFactor();

        double weeklyGoal = user.getGoalWeightChangePerWeek() == null
                ? 0.0
                : user.getGoalWeightChangePerWeek();
        double dailyCalorieDelta = (weeklyGoal * CALORIES_PER_POUND) / 7.0;
        double recommended = tdee + dailyCalorieDelta;

        long proteinTarget = Math.round(user.getWeight() * PROTEIN_GRAMS_PER_LB);

        return new MetricsResponse(
                age,
                activityLevel.getFactor(),
                Math.round(bmr),
                Math.round(tdee),
                Math.round(recommended),
                proteinTarget
        );
    }

    private boolean isMale(String sex) {
        return "M".equalsIgnoreCase(sex.trim());
    }

    private int ageInYears(LocalDate dateOfBirth) {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }
}
