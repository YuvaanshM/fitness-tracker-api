package com.yuvaansh.fitness_tracker_api.repository;

import com.yuvaansh.fitness_tracker_api.entity.Routine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoutineRepository extends JpaRepository<Routine, Long> {

    List<Routine> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Routine> findByIdAndUserId(Long id, Long userId);
}
