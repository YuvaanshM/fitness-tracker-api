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
        assertThat(results).extracting(Meal::getId).containsExactly(aliceMeal2.getId(), aliceMeal1.getId());
        assertThat(results).allMatch(meal -> meal.getUser().getId().equals(alice.getId()));
        assertThat(results).allMatch(meal -> meal.getMealDate().equals(targetDate));
    }

    private Meal saveMeal(User user, String name, LocalDate mealDate) {
        Meal meal = new Meal();
        meal.setUser(user);
        meal.setName(name);
        meal.setMealDate(mealDate);
        meal.setCalories(500);
        meal.setProtein(new BigDecimal("30.00"));
        meal.setCarbs(new BigDecimal("40.00"));
        meal.setFats(new BigDecimal("15.00"));
        return mealRepository.save(meal);
    }
}
