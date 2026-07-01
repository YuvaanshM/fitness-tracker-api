package com.yuvaansh.fitness_tracker_api.repository;

import com.yuvaansh.fitness_tracker_api.entity.User;
import com.yuvaansh.fitness_tracker_api.entity.WorkoutSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class WorkoutSessionRepositoryTest {

    @Autowired
    private WorkoutSessionRepository workoutSessionRepository;

    @Autowired
    private UserRepository userRepository;

    private User alice;
    private User bob;

    @BeforeEach
    void setUp() {
        alice = userRepository.save(new User(
                "alice", "hash", "F", 165.0, 140.0, "MODERATELY_ACTIVE", "LOSE", -1.0));
        bob = userRepository.save(new User(
                "bob", "hash", "M", 180.0, 175.0, "LIGHTLY_ACTIVE", "MAINTAIN", 0.0));
    }

    @Test
    void findByUserId_returnsOnlyOwnSessionsNewestFirst() {
        saveSession(alice, LocalDateTime.of(2026, 6, 20, 8, 0));
        saveSession(alice, LocalDateTime.of(2026, 6, 25, 8, 0));
        saveSession(bob, LocalDateTime.of(2026, 6, 25, 8, 0));

        List<WorkoutSession> results = workoutSessionRepository.findByUserIdOrderByStartedAtDesc(alice.getId());

        assertThat(results).hasSize(2);
        assertThat(results.get(0).getStartedAt()).isEqualTo(LocalDateTime.of(2026, 6, 25, 8, 0));
        assertThat(results).allMatch(s -> s.getUser().getId().equals(alice.getId()));
    }

    @Test
    void findByUserIdAndStartedAtBetween_filtersByRangeAndUser() {
        saveSession(alice, LocalDateTime.of(2026, 6, 10, 8, 0));
        saveSession(alice, LocalDateTime.of(2026, 6, 20, 8, 0));
        saveSession(bob, LocalDateTime.of(2026, 6, 20, 8, 0));

        List<WorkoutSession> results = workoutSessionRepository
                .findByUserIdAndStartedAtBetweenOrderByStartedAtDesc(
                        alice.getId(),
                        LocalDateTime.of(2026, 6, 15, 0, 0),
                        LocalDateTime.of(2026, 6, 21, 0, 0).with(LocalTime.MAX));

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getStartedAt()).isEqualTo(LocalDateTime.of(2026, 6, 20, 8, 0));
    }

    @Test
    void findByIdAndUserId_enforcesOwnership() {
        WorkoutSession aliceSession = saveSession(alice, LocalDateTime.of(2026, 6, 20, 8, 0));

        assertThat(workoutSessionRepository.findByIdAndUserId(aliceSession.getId(), alice.getId())).isPresent();
        assertThat(workoutSessionRepository.findByIdAndUserId(aliceSession.getId(), bob.getId())).isEmpty();
    }

    private WorkoutSession saveSession(User user, LocalDateTime startedAt) {
        WorkoutSession session = new WorkoutSession();
        session.setUser(user);
        session.setStartedAt(startedAt);
        return workoutSessionRepository.save(session);
    }
}
