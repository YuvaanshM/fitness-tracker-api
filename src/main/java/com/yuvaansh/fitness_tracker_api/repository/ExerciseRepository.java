package com.yuvaansh.fitness_tracker_api.repository;

import com.yuvaansh.fitness_tracker_api.entity.Exercise;
import com.yuvaansh.fitness_tracker_api.enums.MuscleGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    /**
     * Global (user IS NULL) exercises plus the given user's custom exercises,
     * optionally filtered by a name fragment and/or muscle group.
     */
    @Query("""
            SELECT e FROM Exercise e
            WHERE (e.user IS NULL OR e.user.id = :userId)
              AND (:query IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :query, '%')))
              AND (:muscle IS NULL OR e.primaryMuscle = :muscle)
            ORDER BY e.name ASC
            """)
    List<Exercise> searchAccessible(
            @Param("userId") Long userId,
            @Param("query") String query,
            @Param("muscle") MuscleGroup muscle);

    /**
     * An exercise the user is allowed to reference: either global or their own.
     */
    @Query("""
            SELECT e FROM Exercise e
            WHERE e.id = :id
              AND (e.user IS NULL OR e.user.id = :userId)
            """)
    Optional<Exercise> findAccessibleById(@Param("id") Long id, @Param("userId") Long userId);

    boolean existsByUserIsNullAndNameIgnoreCase(String name);
}
