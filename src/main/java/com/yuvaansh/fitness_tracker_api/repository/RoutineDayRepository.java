package com.yuvaansh.fitness_tracker_api.repository;

import com.yuvaansh.fitness_tracker_api.entity.RoutineDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoutineDayRepository extends JpaRepository<RoutineDay, Long> {

    /**
     * A day scoped both to its routine and to the owning user, so callers cannot
     * reach another user's day by guessing ids.
     */
    Optional<RoutineDay> findByIdAndRoutineIdAndRoutineUserId(Long id, Long routineId, Long userId);

    Optional<RoutineDay> findByIdAndRoutineUserId(Long id, Long userId);
}
