package com.yuvaansh.fitness_tracker_api.repository;

import com.yuvaansh.fitness_tracker_api.entity.Exercise;
import com.yuvaansh.fitness_tracker_api.entity.User;
import com.yuvaansh.fitness_tracker_api.enums.Equipment;
import com.yuvaansh.fitness_tracker_api.enums.MuscleGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ExerciseRepositoryTest {

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private UserRepository userRepository;

    private User alice;
    private User bob;
    private Exercise globalBench;
    private Exercise aliceCurl;
    private Exercise bobSquat;

    @BeforeEach
    void setUp() {
        alice = userRepository.save(new User(
                "alice", "hash", "F", 165.0, 140.0, "MODERATELY_ACTIVE", "LOSE", -1.0));
        bob = userRepository.save(new User(
                "bob", "hash", "M", 180.0, 175.0, "LIGHTLY_ACTIVE", "MAINTAIN", 0.0));

        globalBench = exerciseRepository.save(
                new Exercise(null, "Global Bench Press", MuscleGroup.CHEST, Equipment.BARBELL));
        aliceCurl = exerciseRepository.save(
                new Exercise(alice, "Alice Curl", MuscleGroup.BICEPS, Equipment.DUMBBELL));
        bobSquat = exerciseRepository.save(
                new Exercise(bob, "Bob Squat", MuscleGroup.QUADS, Equipment.BARBELL));
    }

    @Test
    void searchAccessible_returnsGlobalAndOwnButNotOtherUsers() {
        List<Exercise> results = exerciseRepository.searchAccessible(alice.getId(), null, null);

        assertThat(results).extracting(Exercise::getName)
                .containsExactly("Alice Curl", "Global Bench Press");
        assertThat(results).noneMatch(e -> e.getName().equals("Bob Squat"));
    }

    @Test
    void searchAccessible_filtersByNameFragmentCaseInsensitively() {
        List<Exercise> results = exerciseRepository.searchAccessible(alice.getId(), "curl", null);

        assertThat(results).extracting(Exercise::getName).containsExactly("Alice Curl");
    }

    @Test
    void searchAccessible_filtersByMuscleGroup() {
        List<Exercise> results = exerciseRepository.searchAccessible(alice.getId(), null, MuscleGroup.CHEST);

        assertThat(results).extracting(Exercise::getName).containsExactly("Global Bench Press");
    }

    @Test
    void findAccessibleById_allowsGlobalAndOwnButNotOtherUsers() {
        assertThat(exerciseRepository.findAccessibleById(globalBench.getId(), alice.getId())).isPresent();
        assertThat(exerciseRepository.findAccessibleById(aliceCurl.getId(), alice.getId())).isPresent();
        assertThat(exerciseRepository.findAccessibleById(bobSquat.getId(), alice.getId())).isEmpty();
    }

    @Test
    void existsByUserIsNullAndNameIgnoreCase_matchesOnlyGlobalExercises() {
        assertThat(exerciseRepository.existsByUserIsNullAndNameIgnoreCase("global bench press")).isTrue();
        assertThat(exerciseRepository.existsByUserIsNullAndNameIgnoreCase("Alice Curl")).isFalse();
    }
}
