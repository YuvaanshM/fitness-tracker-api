package com.yuvaansh.fitness_tracker_api.service;

import com.yuvaansh.fitness_tracker_api.dto.FinishWorkoutRequest;
import com.yuvaansh.fitness_tracker_api.dto.LogSetRequest;
import com.yuvaansh.fitness_tracker_api.dto.StartWorkoutRequest;
import com.yuvaansh.fitness_tracker_api.dto.WorkoutSessionResponse;
import com.yuvaansh.fitness_tracker_api.entity.Exercise;
import com.yuvaansh.fitness_tracker_api.entity.User;
import com.yuvaansh.fitness_tracker_api.entity.WorkoutSession;
import com.yuvaansh.fitness_tracker_api.enums.Equipment;
import com.yuvaansh.fitness_tracker_api.enums.MuscleGroup;
import com.yuvaansh.fitness_tracker_api.exception.ResourceNotFoundException;
import com.yuvaansh.fitness_tracker_api.repository.ExerciseRepository;
import com.yuvaansh.fitness_tracker_api.repository.RoutineDayRepository;
import com.yuvaansh.fitness_tracker_api.repository.UserRepository;
import com.yuvaansh.fitness_tracker_api.repository.WorkoutSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkoutServiceTest {

    @Mock
    private WorkoutSessionRepository workoutSessionRepository;

    @Mock
    private RoutineDayRepository routineDayRepository;

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Principal principal;

    @InjectMocks
    private WorkoutService workoutService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("alice", "hash", "F", 165.0, 140.0, "MODERATELY_ACTIVE", "LOSE", -1.0);
        user.setId(1L);
        when(principal.getName()).thenReturn("alice");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
    }

    @Test
    void startWorkout_tiesSessionToUserAndDefaultsStartTime() {
        when(workoutSessionRepository.save(any(WorkoutSession.class))).thenAnswer(inv -> {
            WorkoutSession s = inv.getArgument(0);
            s.setId(50L);
            return s;
        });

        WorkoutSessionResponse response = workoutService.startWorkout(principal, new StartWorkoutRequest());

        ArgumentCaptor<WorkoutSession> captor = ArgumentCaptor.forClass(WorkoutSession.class);
        verify(workoutSessionRepository).save(captor.capture());
        WorkoutSession saved = captor.getValue();

        assertThat(saved.getUser()).isEqualTo(user);
        assertThat(saved.getStartedAt()).isNotNull();
        assertThat(response.getId()).isEqualTo(50L);
    }

    @Test
    void logSet_addsSetToOwnedSessionForAccessibleExercise() {
        WorkoutSession session = new WorkoutSession();
        session.setId(50L);
        session.setUser(user);
        session.setStartedAt(LocalDateTime.now());

        Exercise exercise = new Exercise(null, "Bench Press", MuscleGroup.CHEST, Equipment.BARBELL);
        exercise.setId(5L);

        when(workoutSessionRepository.findByIdAndUserId(50L, 1L)).thenReturn(Optional.of(session));
        when(exerciseRepository.findAccessibleById(5L, 1L)).thenReturn(Optional.of(exercise));
        when(workoutSessionRepository.save(any(WorkoutSession.class))).thenAnswer(inv -> inv.getArgument(0));

        LogSetRequest request = new LogSetRequest();
        request.setExerciseId(5L);
        request.setSetNumber(1);
        request.setReps(10);
        request.setWeightLbs(new BigDecimal("135.00"));
        request.setRestSeconds(90);

        WorkoutSessionResponse response = workoutService.logSet(principal, 50L, request);

        assertThat(response.getSets()).hasSize(1);
        assertThat(response.getSets().get(0).getExerciseId()).isEqualTo(5L);
        assertThat(response.getSets().get(0).getReps()).isEqualTo(10);
        assertThat(response.getSets().get(0).getCompleted()).isTrue();
    }

    @Test
    void logSet_throwsWhenSessionNotOwned() {
        when(workoutSessionRepository.findByIdAndUserId(50L, 1L)).thenReturn(Optional.empty());

        LogSetRequest request = new LogSetRequest();
        request.setExerciseId(5L);
        request.setSetNumber(1);
        request.setReps(10);
        request.setWeightLbs(new BigDecimal("135.00"));

        assertThatThrownBy(() -> workoutService.logSet(principal, 50L, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void finishWorkout_setsEndedAt() {
        WorkoutSession session = new WorkoutSession();
        session.setId(50L);
        session.setUser(user);
        session.setStartedAt(LocalDateTime.of(2026, 6, 28, 8, 0));

        when(workoutSessionRepository.findByIdAndUserId(50L, 1L)).thenReturn(Optional.of(session));
        when(workoutSessionRepository.save(any(WorkoutSession.class))).thenAnswer(inv -> inv.getArgument(0));

        WorkoutSessionResponse response = workoutService.finishWorkout(principal, 50L, new FinishWorkoutRequest());

        assertThat(response.getEndedAt()).isNotNull();
    }
}
