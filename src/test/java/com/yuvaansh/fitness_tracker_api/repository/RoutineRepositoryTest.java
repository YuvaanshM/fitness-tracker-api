package com.yuvaansh.fitness_tracker_api.repository;

import com.yuvaansh.fitness_tracker_api.entity.Routine;
import com.yuvaansh.fitness_tracker_api.entity.RoutineDay;
import com.yuvaansh.fitness_tracker_api.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class RoutineRepositoryTest {

    @Autowired
    private RoutineRepository routineRepository;

    @Autowired
    private RoutineDayRepository routineDayRepository;

    @Autowired
    private UserRepository userRepository;

    private User alice;
    private User bob;
    private Routine aliceRoutine;
    private RoutineDay aliceDay;

    @BeforeEach
    void setUp() {
        alice = userRepository.save(new User(
                "alice", "hash", "F", 165.0, 140.0, "MODERATELY_ACTIVE", "LOSE", -1.0));
        bob = userRepository.save(new User(
                "bob", "hash", "M", 180.0, 175.0, "LIGHTLY_ACTIVE", "MAINTAIN", 0.0));

        aliceRoutine = new Routine(alice, "Push Pull Legs", "Alice's split");
        aliceDay = new RoutineDay(aliceRoutine, "Push", 0);
        aliceRoutine.addDay(aliceDay);
        aliceRoutine = routineRepository.save(aliceRoutine);
        aliceDay = aliceRoutine.getDays().get(0);

        routineRepository.save(new Routine(bob, "Full Body", "Bob's routine"));
    }

    @Test
    void findByUserIdOrderByCreatedAtDesc_returnsOnlyOwnRoutines() {
        List<Routine> results = routineRepository.findByUserIdOrderByCreatedAtDesc(alice.getId());

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Push Pull Legs");
    }

    @Test
    void findByIdAndUserId_enforcesOwnership() {
        assertThat(routineRepository.findByIdAndUserId(aliceRoutine.getId(), alice.getId())).isPresent();
        assertThat(routineRepository.findByIdAndUserId(aliceRoutine.getId(), bob.getId())).isEmpty();
    }

    @Test
    void findRoutineDay_isScopedToOwningUserAndRoutine() {
        assertThat(routineDayRepository
                .findByIdAndRoutineIdAndRoutineUserId(aliceDay.getId(), aliceRoutine.getId(), alice.getId()))
                .isPresent();
        assertThat(routineDayRepository
                .findByIdAndRoutineIdAndRoutineUserId(aliceDay.getId(), aliceRoutine.getId(), bob.getId()))
                .isEmpty();
        assertThat(routineDayRepository.findByIdAndRoutineUserId(aliceDay.getId(), bob.getId())).isEmpty();
    }
}
