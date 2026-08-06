package com.yuvaansh.fitness_tracker_api.service;

import com.yuvaansh.fitness_tracker_api.dto.MetricsResponse;
import com.yuvaansh.fitness_tracker_api.entity.User;
import com.yuvaansh.fitness_tracker_api.exception.MetricsUnavailableException;
import com.yuvaansh.fitness_tracker_api.exception.UserNotFoundException;
import com.yuvaansh.fitness_tracker_api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MetricsServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Principal principal;

    private MetricsService metricsService;

    @BeforeEach
    void setUp() {
        metricsService = new MetricsService(userRepository);
    }

    private User user(String sex, double heightCm, double weightLbs, String activityLevel,
                      double goalPerWeek, int age) {
        return new User("alice", "hash", sex, heightCm, weightLbs, activityLevel, "MAINTAIN",
                goalPerWeek, LocalDate.now().minusYears(age));
    }

    @Test
    void computeMetrics_matchesMifflinStJeorForMale() {
        // 200 lbs -> 90.719 kg, 180 cm, age 30, sedentary (x1.2)
        // BMR = 10*90.719 + 6.25*180 - 5*30 + 5 = ~1887
        MetricsResponse metrics = metricsService.computeMetrics(
                user("M", 180.0, 200.0, "SEDENTARY", 0.0, 30));

        assertThat(metrics.getAge()).isEqualTo(30);
        assertThat(metrics.getActivityFactor()).isEqualTo(1.2);
        assertThat(metrics.getBmr()).isBetween(1886L, 1888L);
        assertThat(metrics.getTdee()).isBetween(2264L, 2266L);
        // maintain goal -> recommended equals TDEE
        assertThat(metrics.getRecommendedCalories()).isEqualTo(metrics.getTdee());
        assertThat(metrics.getProteinTargetGrams()).isEqualTo(200L);
    }

    @Test
    void computeMetrics_femaleBmrIsLowerByConstant() {
        MetricsResponse male = metricsService.computeMetrics(
                user("M", 175.0, 180.0, "MODERATELY_ACTIVE", 0.0, 28));
        MetricsResponse female = metricsService.computeMetrics(
                user("F", 175.0, 180.0, "MODERATELY_ACTIVE", 0.0, 28));

        // Mifflin-St Jeor differs only by +5 (male) vs -161 (female) => 166 kcal.
        assertThat(male.getBmr() - female.getBmr()).isEqualTo(166L);
    }

    @Test
    void computeMetrics_appliesActivityFactor() {
        MetricsResponse metrics = metricsService.computeMetrics(
                user("M", 180.0, 200.0, "VERY_ACTIVE", 0.0, 30));

        assertThat(metrics.getActivityFactor()).isEqualTo(1.725);
        assertThat(metrics.getTdee()).isEqualTo(Math.round(metrics.getBmr() * 1.725));
    }

    @Test
    void computeMetrics_cuttingLowersRecommendedByAboutFiveHundred() {
        MetricsResponse maintain = metricsService.computeMetrics(
                user("M", 180.0, 200.0, "SEDENTARY", 0.0, 30));
        MetricsResponse cut = metricsService.computeMetrics(
                user("M", 180.0, 200.0, "SEDENTARY", -1.0, 30));

        // -1 lb/week => -3500/7 = -500 kcal/day
        assertThat(maintain.getRecommendedCalories() - cut.getRecommendedCalories())
                .isBetween(499L, 501L);
    }

    @Test
    void computeMetrics_throwsWhenDateOfBirthMissing() {
        User noDob = new User("alice", "hash", "M", 180.0, 200.0,
                "SEDENTARY", "MAINTAIN", 0.0);

        assertThatThrownBy(() -> metricsService.computeMetrics(noDob))
                .isInstanceOf(MetricsUnavailableException.class);
    }

    @Test
    void computeMetrics_throwsWhenActivityLevelUnknown() {
        assertThatThrownBy(() -> metricsService.computeMetrics(
                user("M", 180.0, 200.0, "SUPER_DUPER_ACTIVE", 0.0, 30)))
                .isInstanceOf(MetricsUnavailableException.class);
    }

    @Test
    void getMetrics_resolvesAuthenticatedUser() {
        when(principal.getName()).thenReturn("alice");
        when(userRepository.findByUsername("alice"))
                .thenReturn(Optional.of(user("M", 180.0, 200.0, "SEDENTARY", 0.0, 30)));

        MetricsResponse metrics = metricsService.getMetrics(principal);

        assertThat(metrics.getBmr()).isBetween(1886L, 1888L);
    }

    @Test
    void getMetrics_throwsWhenUserMissing() {
        when(principal.getName()).thenReturn("ghost");
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> metricsService.getMetrics(principal))
                .isInstanceOf(UserNotFoundException.class);
    }
}
