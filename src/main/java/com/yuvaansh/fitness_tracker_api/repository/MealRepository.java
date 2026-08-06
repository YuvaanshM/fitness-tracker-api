package com.yuvaansh.fitness_tracker_api.repository;

import com.yuvaansh.fitness_tracker_api.entity.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {

    List<Meal> findByUserIdAndMealDateOrderByCreatedAtDesc(Long userId, LocalDate mealDate);

    @Query("""
            SELECT
                SUM(m.calories) AS totalCalories,
                SUM(m.protein) AS totalProtein,
                SUM(m.carbs) AS totalCarbs,
                SUM(m.fats) AS totalFats,
                SUM(m.sugar) AS totalSugar,
                SUM(m.fiber) AS totalFiber
            FROM Meal m
            WHERE m.user.id = :userId
              AND m.mealDate = :mealDate
            """)
    DailyNutritionSummaryProjection summarizeByUserIdAndMealDate(
            @Param("userId") Long userId,
            @Param("mealDate") LocalDate mealDate);

    @Query("""
            SELECT
                m.mealDate AS date,
                SUM(m.calories) AS calories,
                SUM(m.protein) AS protein,
                SUM(m.carbs) AS carbs,
                SUM(m.fats) AS fats
            FROM Meal m
            WHERE m.user.id = :userId
              AND m.mealDate BETWEEN :from AND :to
            GROUP BY m.mealDate
            ORDER BY m.mealDate ASC
            """)
    List<NutritionTrendProjection> summarizeTrendByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to);
}
