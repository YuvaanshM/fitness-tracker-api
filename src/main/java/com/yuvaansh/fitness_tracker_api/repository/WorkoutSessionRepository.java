package com.yuvaansh.fitness_tracker_api.repository;

import com.yuvaansh.fitness_tracker_api.entity.WorkoutSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, Long> {

    List<WorkoutSession> findByUserIdOrderByStartedAtDesc(Long userId);

    List<WorkoutSession> findByUserIdAndStartedAtBetweenOrderByStartedAtDesc(
            Long userId, LocalDateTime from, LocalDateTime to);

    Optional<WorkoutSession> findByIdAndUserId(Long id, Long userId);
}
