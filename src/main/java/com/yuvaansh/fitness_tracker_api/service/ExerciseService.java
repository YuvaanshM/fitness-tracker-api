package com.yuvaansh.fitness_tracker_api.service;

import com.yuvaansh.fitness_tracker_api.dto.CreateExerciseRequest;
import com.yuvaansh.fitness_tracker_api.dto.ExerciseResponse;
import com.yuvaansh.fitness_tracker_api.entity.Exercise;
import com.yuvaansh.fitness_tracker_api.entity.User;
import com.yuvaansh.fitness_tracker_api.enums.MuscleGroup;
import com.yuvaansh.fitness_tracker_api.exception.UserNotFoundException;
import com.yuvaansh.fitness_tracker_api.repository.ExerciseRepository;
import com.yuvaansh.fitness_tracker_api.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final UserRepository userRepository;

    public ExerciseService(ExerciseRepository exerciseRepository, UserRepository userRepository) {
        this.exerciseRepository = exerciseRepository;
        this.userRepository = userRepository;
    }

    /**
     * Lists global (seeded) exercises plus the caller's own custom exercises,
     * optionally filtered by a name fragment and/or muscle group.
     */
    public List<ExerciseResponse> listExercises(Principal principal, String query, MuscleGroup muscle) {
        User user = resolveUser(principal);
        String trimmed = (query == null || query.isBlank()) ? null : query.trim();
        return exerciseRepository.searchAccessible(user.getId(), trimmed, muscle)
                .stream()
                .map(ExerciseResponse::fromEntity)
                .toList();
    }

    public ExerciseResponse createExercise(Principal principal, CreateExerciseRequest request) {
        User user = resolveUser(principal);
        Exercise exercise = new Exercise(
                user,
                request.getName().trim(),
                request.getPrimaryMuscle(),
                request.getEquipment());
        return ExerciseResponse.fromEntity(exerciseRepository.save(exercise));
    }

    private User resolveUser(Principal principal) {
        return userRepository.findByUsername(principal.getName())
                .orElseThrow(UserNotFoundException::new);
    }
}
