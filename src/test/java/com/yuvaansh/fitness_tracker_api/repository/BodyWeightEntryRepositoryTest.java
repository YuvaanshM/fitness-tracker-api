package com.yuvaansh.fitness_tracker_api.repository;

import com.yuvaansh.fitness_tracker_api.entity.BodyWeightEntry;
import com.yuvaansh.fitness_tracker_api.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class BodyWeightEntryRepositoryTest {

    @Autowired
    private BodyWeightEntryRepository bodyWeightEntryRepository;

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
    void findByUserIdAndEntryDateBetween_returnsOnlyOwnEntriesInRange() {
        save(alice, LocalDate.of(2026, 7, 1), "140.00");
        save(alice, LocalDate.of(2026, 7, 3), "139.50");
        save(alice, LocalDate.of(2026, 7, 10), "139.00");
        save(bob, LocalDate.of(2026, 7, 2), "180.00");

        List<BodyWeightEntry> results = bodyWeightEntryRepository
                .findByUserIdAndEntryDateBetweenOrderByEntryDateAsc(
                        alice.getId(),
                        LocalDate.of(2026, 7, 1),
                        LocalDate.of(2026, 7, 7));

        assertThat(results).hasSize(2);
        assertThat(results).allMatch(e -> e.getUser().getId().equals(alice.getId()));
        assertThat(results.get(0).getEntryDate()).isEqualTo(LocalDate.of(2026, 7, 1));
        assertThat(results.get(1).getEntryDate()).isEqualTo(LocalDate.of(2026, 7, 3));
    }

    private void save(User user, LocalDate date, String weight) {
        BodyWeightEntry entry = new BodyWeightEntry();
        entry.setUser(user);
        entry.setEntryDate(date);
        entry.setWeightLbs(new BigDecimal(weight));
        bodyWeightEntryRepository.save(entry);
    }
}
