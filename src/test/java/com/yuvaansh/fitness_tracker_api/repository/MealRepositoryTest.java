package com.yuvaansh.fitness_tracker_api.repository;

import com.yuvaansh.fitness_tracker_api.entity.Meal;
import com.yuvaansh.fitness_tracker_api.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class MealRepositoryTest {

    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private UserRepository userRepository;

    private User alice;
    private User bob;
    private LocalDate targetDate;

    @BeforeEach
    void setUp() {
        targetDate = LocalDate.of(2026, 6, 28);

        alice = userRepository.save(new User(
                "alice", "hash", "F", 165.0, 140.0,
                "MODERATELY_ACTIVE", "LOSE", -1.0
        ));
        bob = userRepository.save(new User(
                "bob", "hash", "M", 180.0, 175.0,
                "LIGHTLY_ACTIVE", "MAINTAIN", 0.0
        ));
    }

    @Test
    void findByUserIdAndMealDateOrderByCreatedAtDesc_returnsOnlyMatchingUserAndDate() {
        Meal aliceMeal1 = saveMeal(alice, "Breakfast", targetDate);
        Meal aliceMeal2 = saveMeal(alice, "Lunch", targetDate);
        saveMeal(alice, "Dinner", targetDate.plusDays(1));
        saveMeal(bob, "Breakfast", targetDate);

        List<Meal> results = mealRepository.findByUserIdAndMealDateOrderByCreatedAtDesc(
                alice.getId(), targetDate
        );

        assertThat(results).hasSize(2);
        assertThat(results.stream().map(meal -> meal.getId()).toList())
                .containsExactly(aliceMeal2.getId(), aliceMeal1.getId());
        assertThat(results).allMatch(meal -> meal.getUser().getId().equals(alice.getId()));
        assertThat(results).allMatch(meal -> meal.getMealDate().equals(targetDate));
    }

    @Test
    void summarizeByUserIdAndMealDate_sumsOnlyMatchingUserAndDate() {
        saveMeal(alice, "Breakfast", targetDate, 500, "30.00", "40.00", "15.00", "8.00", "6.00");
        saveMeal(alice, "Lunch", targetDate, 750, "60.00", "80.00", "30.00", "12.00", "9.00");
        saveMeal(alice, "Dinner", targetDate.plusDays(1), 1000, "70.00", "90.00", "40.00", "20.00", "10.00");
        saveMeal(bob, "Breakfast", targetDate, 900, "80.00", "100.00", "50.00", "30.00", "11.00");

        DailyNutritionSummaryProjection summary = mealRepository.summarizeByUserIdAndMealDate(
                alice.getId(), targetDate
        );

        assertThat(summary.getTotalCalories()).isEqualTo(1250L);
        assertThat(summary.getTotalProtein()).isEqualByComparingTo(new BigDecimal("90.00"));
        assertThat(summary.getTotalCarbs()).isEqualByComparingTo(new BigDecimal("120.00"));
        assertThat(summary.getTotalFats()).isEqualByComparingTo(new BigDecimal("45.00"));
        assertThat(summary.getTotalSugar()).isEqualByComparingTo(new BigDecimal("20.00"));
        assertThat(summary.getTotalFiber()).isEqualByComparingTo(new BigDecimal("15.00"));
    }

    private Meal saveMeal(User user, String name, LocalDate mealDate) {
        return saveMeal(user, name, mealDate, 500, "30.00", "40.00", "15.00", null, null);
    }

    private Meal saveMeal(
            User user,
            String name,
            LocalDate mealDate,
            Integer calories,
            String protein,
            String carbs,
            String fats,
            String sugar,
            String fiber) {
        Meal meal = new Meal();
        meal.setUser(user);
        meal.setName(name);
        meal.setMealDate(mealDate);
        meal.setCalories(calories);
        meal.setProtein(new BigDecimal(protein));
        meal.setCarbs(new BigDecimal(carbs));
        meal.setFats(new BigDecimal(fats));
        meal.setSugar(sugar == null ? null : new BigDecimal(sugar));
        meal.setFiber(fiber == null ? null : new BigDecimal(fiber));
        return mealRepository.save(meal);
    }
}
