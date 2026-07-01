package com.yuvaansh.fitness_tracker_api.service;

import com.yuvaansh.fitness_tracker_api.dto.AddRoutineExerciseRequest;
import com.yuvaansh.fitness_tracker_api.dto.CreateRoutineDayRequest;
import com.yuvaansh.fitness_tracker_api.dto.CreateRoutineRequest;
import com.yuvaansh.fitness_tracker_api.dto.RoutineResponse;
import com.yuvaansh.fitness_tracker_api.entity.Exercise;
import com.yuvaansh.fitness_tracker_api.entity.Routine;
import com.yuvaansh.fitness_tracker_api.entity.RoutineDay;
import com.yuvaansh.fitness_tracker_api.entity.RoutineExercise;
import com.yuvaansh.fitness_tracker_api.entity.User;
import com.yuvaansh.fitness_tracker_api.exception.ResourceNotFoundException;
import com.yuvaansh.fitness_tracker_api.exception.UserNotFoundException;
import com.yuvaansh.fitness_tracker_api.repository.ExerciseRepository;
import com.yuvaansh.fitness_tracker_api.repository.RoutineDayRepository;
import com.yuvaansh.fitness_tracker_api.repository.RoutineRepository;
import com.yuvaansh.fitness_tracker_api.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

@Service
@Transactional
public class RoutineService {

    private final RoutineRepository routineRepository;
    private final RoutineDayRepository routineDayRepository;
    private final ExerciseRepository exerciseRepository;
    private final UserRepository userRepository;

    public RoutineService(
            RoutineRepository routineRepository,
            RoutineDayRepository routineDayRepository,
            ExerciseRepository exerciseRepository,
            UserRepository userRepository) {
        this.routineRepository = routineRepository;
        this.routineDayRepository = routineDayRepository;
        this.exerciseRepository = exerciseRepository;
        this.userRepository = userRepository;
    }

    public RoutineResponse createRoutine(Principal principal, CreateRoutineRequest request) {
        User user = resolveUser(principal);
        Routine routine = new Routine(
                user,
                request.getName().trim(),
                trimToNull(request.getDescription()));
        return RoutineResponse.fromEntity(routineRepository.save(routine));
    }

    @Transactional(readOnly = true)
    public List<RoutineResponse> listRoutines(Principal principal) {
        User user = resolveUser(principal);
        return routineRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(RoutineResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public RoutineResponse getRoutine(Principal principal, Long routineId) {
        return RoutineResponse.fromEntity(requireOwnedRoutine(principal, routineId));
    }

    public RoutineResponse updateRoutine(Principal principal, Long routineId, CreateRoutineRequest request) {
        Routine routine = requireOwnedRoutine(principal, routineId);
        routine.setName(request.getName().trim());
        routine.setDescription(trimToNull(request.getDescription()));
        return RoutineResponse.fromEntity(routineRepository.save(routine));
    }

    public void deleteRoutine(Principal principal, Long routineId) {
        Routine routine = requireOwnedRoutine(principal, routineId);
        routineRepository.delete(routine);
    }

    public RoutineResponse addDay(Principal principal, Long routineId, CreateRoutineDayRequest request) {
        Routine routine = requireOwnedRoutine(principal, routineId);
        int orderIndex = request.getOrderIndex() != null
                ? request.getOrderIndex()
                : routine.getDays().size();
        RoutineDay day = new RoutineDay(routine, request.getName().trim(), orderIndex);
        routine.addDay(day);
        return RoutineResponse.fromEntity(routineRepository.save(routine));
    }

    public RoutineResponse addExercise(
            Principal principal, Long routineId, Long dayId, AddRoutineExerciseRequest request) {
        User user = resolveUser(principal);
        RoutineDay day = routineDayRepository
                .findByIdAndRoutineIdAndRoutineUserId(dayId, routineId, user.getId())
                .orElseThrow(ResourceNotFoundException::new);

        Exercise exercise = exerciseRepository
                .findAccessibleById(request.getExerciseId(), user.getId())
                .orElseThrow(ResourceNotFoundException::new);

        RoutineExercise routineExercise = new RoutineExercise();
        routineExercise.setExercise(exercise);
        routineExercise.setOrderIndex(request.getOrderIndex() != null
                ? request.getOrderIndex()
                : day.getExercises().size());
        routineExercise.setTargetSets(request.getTargetSets());
        routineExercise.setTargetReps(request.getTargetReps());
        routineExercise.setRestSeconds(request.getRestSeconds());
        routineExercise.setNotes(trimToNull(request.getNotes()));
        day.addExercise(routineExercise);
        routineDayRepository.save(day);

        return RoutineResponse.fromEntity(day.getRoutine());
    }

    private Routine requireOwnedRoutine(Principal principal, Long routineId) {
        User user = resolveUser(principal);
        return routineRepository.findByIdAndUserId(routineId, user.getId())
                .orElseThrow(ResourceNotFoundException::new);
    }

    private User resolveUser(Principal principal) {
        return userRepository.findByUsername(principal.getName())
                .orElseThrow(UserNotFoundException::new);
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
