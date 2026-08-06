package com.yuvaansh.fitness_tracker_api.repository;

import com.yuvaansh.fitness_tracker_api.entity.BodyWeightEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BodyWeightEntryRepository extends JpaRepository<BodyWeightEntry, Long> {

    Optional<BodyWeightEntry> findByUserIdAndEntryDate(Long userId, LocalDate entryDate);

    List<BodyWeightEntry> findByUserIdAndEntryDateBetweenOrderByEntryDateAsc(
            Long userId,
            LocalDate from,
            LocalDate to);
}
