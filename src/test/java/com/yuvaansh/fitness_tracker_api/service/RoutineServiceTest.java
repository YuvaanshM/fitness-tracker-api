package com.yuvaansh.fitness_tracker_api.service;

import com.yuvaansh.fitness_tracker_api.dto.AddRoutineExerciseRequest;
import com.yuvaansh.fitness_tracker_api.dto.CreateRoutineRequest;
import com.yuvaansh.fitness_tracker_api.dto.RoutineResponse;
import com.yuvaansh.fitness_tracker_api.entity.Exercise;
import com.yuvaansh.fitness_tracker_api.entity.Routine;
import com.yuvaansh.fitness_tracker_api.entity.RoutineDay;
import com.yuvaansh.fitness_tracker_api.entity.User;
import com.yuvaansh.fitness_tracker_api.enums.Equipment;
import com.yuvaansh.fitness_tracker_api.enums.MuscleGroup;
import com.yuvaansh.fitness_tracker_api.exception.ResourceNotFoundException;
import com.yuvaansh.fitness_tracker_api.repository.ExerciseRepository;
import com.yuvaansh.fitness_tracker_api.repository.RoutineDayRepository;
import com.yuvaansh.fitness_tracker_api.repository.RoutineRepository;
import com.yuvaansh.fitness_tracker_api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoutineServiceTest {

    @Mock
    private RoutineRepository routineRepository;

    @Mock
    private RoutineDayRepository routineDayRepository;

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Principal principal;

    @InjectMocks
    private RoutineService routineService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("alice", "hash", "F", 165.0, 140.0, "MODERATELY_ACTIVE", "LOSE", -1.0);
        user.setId(1L);
        when(principal.getName()).thenReturn("alice");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
    }

    @Test
    void createRoutine_tiesToUserAndTrimsName() {
        CreateRoutineRequest request = new CreateRoutineRequest();
        request.setName("  Push Pull Legs  ");
        request.setDescription("  6 day split  ");
        when(routineRepository.save(any(Routine.class))).thenAnswer(inv -> {
            Routine r = inv.getArgument(0);
            r.setId(10L);
            return r;
        });

        RoutineResponse response = routineService.createRoutine(principal, request);

        ArgumentCaptor<Routine> captor = ArgumentCaptor.forClass(Routine.class);
        verify(routineRepository).save(captor.capture());
        Routine saved = captor.getValue();

        assertThat(saved.getUser()).isEqualTo(user);
        assertThat(saved.getName()).isEqualTo("Push Pull Legs");
        assertThat(saved.getDescription()).isEqualTo("6 day split");
        assertThat(response.getId()).isEqualTo(10L);
    }

    @Test
    void getRoutine_throwsWhenNotOwned() {
        when(routineRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> routineService.getRoutine(principal, 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void addExercise_addsAccessibleExerciseToOwnedDay() {
        Routine routine = new Routine(user, "Push Pull Legs", null);
        routine.setId(1L);
        RoutineDay day = new RoutineDay(routine, "Push", 0);
        day.setId(2L);
        routine.addDay(day);

        Exercise exercise = new Exercise(null, "Bench Press", MuscleGroup.CHEST, Equipment.BARBELL);
        exercise.setId(5L);

        when(routineDayRepository.findByIdAndRoutineIdAndRoutineUserId(2L, 1L, 1L))
                .thenReturn(Optional.of(day));
        when(exerciseRepository.findAccessibleById(5L, 1L)).thenReturn(Optional.of(exercise));
        when(routineDayRepository.save(any(RoutineDay.class))).thenAnswer(inv -> inv.getArgument(0));

        AddRoutineExerciseRequest request = new AddRoutineExerciseRequest();
        request.setExerciseId(5L);
        request.setTargetSets(4);
        request.setTargetReps(8);
        request.setRestSeconds(120);

        RoutineResponse response = routineService.addExercise(principal, 1L, 2L, request);

        assertThat(response.getDays()).hasSize(1);
        assertThat(response.getDays().get(0).getExercises()).hasSize(1);
        assertThat(response.getDays().get(0).getExercises().get(0).getExerciseId()).isEqualTo(5L);
        assertThat(response.getDays().get(0).getExercises().get(0).getRestSeconds()).isEqualTo(120);
    }

    @Test
    void addExercise_throwsWhenDayNotOwned() {
        when(routineDayRepository.findByIdAndRoutineIdAndRoutineUserId(2L, 1L, 1L))
                .thenReturn(Optional.empty());

        AddRoutineExerciseRequest request = new AddRoutineExerciseRequest();
        request.setExerciseId(5L);

        assertThatThrownBy(() -> routineService.addExercise(principal, 1L, 2L, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void addExercise_throwsWhenExerciseNotAccessible() {
        Routine routine = new Routine(user, "Push Pull Legs", null);
        routine.setId(1L);
        RoutineDay day = new RoutineDay(routine, "Push", 0);
        day.setId(2L);
        routine.addDay(day);

        when(routineDayRepository.findByIdAndRoutineIdAndRoutineUserId(2L, 1L, 1L))
                .thenReturn(Optional.of(day));
        when(exerciseRepository.findAccessibleById(7L, 1L)).thenReturn(Optional.empty());

        AddRoutineExerciseRequest request = new AddRoutineExerciseRequest();
        request.setExerciseId(7L);

        assertThatThrownBy(() -> routineService.addExercise(principal, 1L, 2L, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
