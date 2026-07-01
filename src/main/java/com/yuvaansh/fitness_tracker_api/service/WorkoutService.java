package com.yuvaansh.fitness_tracker_api.service;

import com.yuvaansh.fitness_tracker_api.dto.FinishWorkoutRequest;
import com.yuvaansh.fitness_tracker_api.dto.LogSetRequest;
import com.yuvaansh.fitness_tracker_api.dto.StartWorkoutRequest;
import com.yuvaansh.fitness_tracker_api.dto.WorkoutSessionResponse;
import com.yuvaansh.fitness_tracker_api.entity.Exercise;
import com.yuvaansh.fitness_tracker_api.entity.RoutineDay;
import com.yuvaansh.fitness_tracker_api.entity.User;
import com.yuvaansh.fitness_tracker_api.entity.WorkoutSession;
import com.yuvaansh.fitness_tracker_api.entity.WorkoutSet;
import com.yuvaansh.fitness_tracker_api.exception.ResourceNotFoundException;
import com.yuvaansh.fitness_tracker_api.exception.UserNotFoundException;
import com.yuvaansh.fitness_tracker_api.repository.ExerciseRepository;
import com.yuvaansh.fitness_tracker_api.repository.RoutineDayRepository;
import com.yuvaansh.fitness_tracker_api.repository.UserRepository;
import com.yuvaansh.fitness_tracker_api.repository.WorkoutSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
public class WorkoutService {

    private final WorkoutSessionRepository workoutSessionRepository;
    private final RoutineDayRepository routineDayRepository;
    private final ExerciseRepository exerciseRepository;
    private final UserRepository userRepository;

    public WorkoutService(
            WorkoutSessionRepository workoutSessionRepository,
            RoutineDayRepository routineDayRepository,
            ExerciseRepository exerciseRepository,
            UserRepository userRepository) {
        this.workoutSessionRepository = workoutSessionRepository;
        this.routineDayRepository = routineDayRepository;
        this.exerciseRepository = exerciseRepository;
        this.userRepository = userRepository;
    }

    public WorkoutSessionResponse startWorkout(Principal principal, StartWorkoutRequest request) {
        User user = resolveUser(principal);

        WorkoutSession session = new WorkoutSession();
        session.setUser(user);
        session.setStartedAt(request.getStartedAt() != null ? request.getStartedAt() : LocalDateTime.now());
        session.setNotes(trimToNull(request.getNotes()));

        if (request.getRoutineDayId() != null) {
            RoutineDay day = routineDayRepository
                    .findByIdAndRoutineUserId(request.getRoutineDayId(), user.getId())
                    .orElseThrow(ResourceNotFoundException::new);
            session.setRoutineDay(day);
        }

        return WorkoutSessionResponse.fromEntity(workoutSessionRepository.save(session));
    }

    public WorkoutSessionResponse logSet(Principal principal, Long sessionId, LogSetRequest request) {
        User user = resolveUser(principal);
        WorkoutSession session = requireOwnedSession(sessionId, user);
        Exercise exercise = exerciseRepository
                .findAccessibleById(request.getExerciseId(), user.getId())
                .orElseThrow(ResourceNotFoundException::new);

        WorkoutSet set = new WorkoutSet();
        set.setExercise(exercise);
        set.setSetNumber(request.getSetNumber());
        set.setReps(request.getReps());
        set.setWeightLbs(request.getWeightLbs());
        set.setRestSeconds(request.getRestSeconds());
        set.setCompleted(request.getCompleted() == null ? Boolean.TRUE : request.getCompleted());
        session.addSet(set);

        return WorkoutSessionResponse.fromEntity(workoutSessionRepository.save(session));
    }

    public WorkoutSessionResponse finishWorkout(Principal principal, Long sessionId, FinishWorkoutRequest request) {
        User user = resolveUser(principal);
        WorkoutSession session = requireOwnedSession(sessionId, user);
        session.setEndedAt(request.getEndedAt() != null ? request.getEndedAt() : LocalDateTime.now());
        if (request.getNotes() != null) {
            session.setNotes(trimToNull(request.getNotes()));
        }
        return WorkoutSessionResponse.fromEntity(workoutSessionRepository.save(session));
    }

    @Transactional(readOnly = true)
    public List<WorkoutSessionResponse> listWorkouts(Principal principal, LocalDate from, LocalDate to) {
        User user = resolveUser(principal);
        List<WorkoutSession> sessions;
        if (from != null && to != null) {
            LocalDateTime start = from.atStartOfDay();
            LocalDateTime end = to.atTime(LocalTime.MAX);
            sessions = workoutSessionRepository
                    .findByUserIdAndStartedAtBetweenOrderByStartedAtDesc(user.getId(), start, end);
        } else {
            sessions = workoutSessionRepository.findByUserIdOrderByStartedAtDesc(user.getId());
        }
        return sessions.stream().map(WorkoutSessionResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public WorkoutSessionResponse getWorkout(Principal principal, Long sessionId) {
        User user = resolveUser(principal);
        return WorkoutSessionResponse.fromEntity(requireOwnedSession(sessionId, user));
    }

    private WorkoutSession requireOwnedSession(Long sessionId, User user) {
        return workoutSessionRepository.findByIdAndUserId(sessionId, user.getId())
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
