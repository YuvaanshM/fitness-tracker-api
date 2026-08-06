package com.yuvaansh.fitness_tracker_api.repository;

import com.yuvaansh.fitness_tracker_api.entity.WorkoutSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WorkoutSetRepository extends JpaRepository<WorkoutSet, Long> {

    @Query("""
            SELECT ws
            FROM WorkoutSet ws
            JOIN FETCH ws.session session
            JOIN FETCH ws.exercise exercise
            WHERE session.user.id = :userId
              AND exercise.id = :exerciseId
              AND session.startedAt BETWEEN :from AND :to
              AND ws.completed = true
            ORDER BY session.startedAt ASC, ws.setNumber ASC
            """)
    List<WorkoutSet> findCompletedForExerciseTrend(
            @Param("userId") Long userId,
            @Param("exerciseId") Long exerciseId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);
}
