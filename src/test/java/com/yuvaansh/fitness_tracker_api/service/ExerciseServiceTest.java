package com.yuvaansh.fitness_tracker_api.service;

import com.yuvaansh.fitness_tracker_api.dto.CreateExerciseRequest;
import com.yuvaansh.fitness_tracker_api.dto.ExerciseResponse;
import com.yuvaansh.fitness_tracker_api.entity.Exercise;
import com.yuvaansh.fitness_tracker_api.entity.User;
import com.yuvaansh.fitness_tracker_api.enums.Equipment;
import com.yuvaansh.fitness_tracker_api.enums.MuscleGroup;
import com.yuvaansh.fitness_tracker_api.repository.ExerciseRepository;
import com.yuvaansh.fitness_tracker_api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExerciseServiceTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Principal principal;

    @InjectMocks
    private ExerciseService exerciseService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("alice", "hash", "F", 165.0, 140.0, "MODERATELY_ACTIVE", "LOSE", -1.0);
        user.setId(1L);
        when(principal.getName()).thenReturn("alice");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
    }

    @Test
    void listExercises_scopesToAuthenticatedUser() {
        Exercise exercise = new Exercise(null, "Global Bench Press", MuscleGroup.CHEST, Equipment.BARBELL);
        exercise.setId(5L);
        when(exerciseRepository.searchAccessible(eq(1L), isNull(), isNull()))
                .thenReturn(List.of(exercise));

        List<ExerciseResponse> results = exerciseService.listExercises(principal, null, null);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Global Bench Press");
        assertThat(results.get(0).isGlobal()).isTrue();
    }

    @Test
    void createExercise_tiesCustomExerciseToUserAndTrimsName() {
        CreateExerciseRequest request = new CreateExerciseRequest();
        request.setName("  Incline Press  ");
        request.setPrimaryMuscle(MuscleGroup.CHEST);
        request.setEquipment(Equipment.DUMBBELL);
        when(exerciseRepository.save(any(Exercise.class))).thenAnswer(inv -> {
            Exercise e = inv.getArgument(0);
            e.setId(9L);
            return e;
        });

        ExerciseResponse response = exerciseService.createExercise(principal, request);

        ArgumentCaptor<Exercise> captor = ArgumentCaptor.forClass(Exercise.class);
        verify(exerciseRepository).save(captor.capture());
        Exercise saved = captor.getValue();

        assertThat(saved.getUser()).isEqualTo(user);
        assertThat(saved.getName()).isEqualTo("Incline Press");
        assertThat(response.isGlobal()).isFalse();
        assertThat(response.getId()).isEqualTo(9L);
    }
}
