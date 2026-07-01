package com.yuvaansh.fitness_tracker_api.config;

import com.yuvaansh.fitness_tracker_api.entity.Exercise;
import com.yuvaansh.fitness_tracker_api.enums.Equipment;
import com.yuvaansh.fitness_tracker_api.enums.MuscleGroup;
import com.yuvaansh.fitness_tracker_api.repository.ExerciseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Seeds a set of common global exercises (user = null) on startup. Idempotent:
 * each exercise is only inserted if a global one with the same name does not
 * already exist, so repeated restarts do not create duplicates.
 */
@Configuration
public class ExerciseDataSeeder {

    private record SeedExercise(String name, MuscleGroup muscle, Equipment equipment) {
    }

    private static final List<SeedExercise> SEED = List.of(
            // Push
            new SeedExercise("Barbell Bench Press", MuscleGroup.CHEST, Equipment.BARBELL),
            new SeedExercise("Incline Dumbbell Press", MuscleGroup.CHEST, Equipment.DUMBBELL),
            new SeedExercise("Overhead Press", MuscleGroup.SHOULDERS, Equipment.BARBELL),
            new SeedExercise("Dumbbell Lateral Raise", MuscleGroup.SHOULDERS, Equipment.DUMBBELL),
            new SeedExercise("Triceps Pushdown", MuscleGroup.TRICEPS, Equipment.CABLE),
            // Pull
            new SeedExercise("Deadlift", MuscleGroup.BACK, Equipment.BARBELL),
            new SeedExercise("Pull-Up", MuscleGroup.BACK, Equipment.BODYWEIGHT),
            new SeedExercise("Bent-Over Barbell Row", MuscleGroup.BACK, Equipment.BARBELL),
            new SeedExercise("Lat Pulldown", MuscleGroup.BACK, Equipment.CABLE),
            new SeedExercise("Dumbbell Biceps Curl", MuscleGroup.BICEPS, Equipment.DUMBBELL),
            // Legs
            new SeedExercise("Back Squat", MuscleGroup.QUADS, Equipment.BARBELL),
            new SeedExercise("Romanian Deadlift", MuscleGroup.HAMSTRINGS, Equipment.BARBELL),
            new SeedExercise("Leg Press", MuscleGroup.QUADS, Equipment.MACHINE),
            new SeedExercise("Standing Calf Raise", MuscleGroup.CALVES, Equipment.MACHINE),
            new SeedExercise("Hip Thrust", MuscleGroup.GLUTES, Equipment.BARBELL),
            // Core / cardio
            new SeedExercise("Plank", MuscleGroup.CORE, Equipment.BODYWEIGHT),
            new SeedExercise("Hanging Leg Raise", MuscleGroup.CORE, Equipment.BODYWEIGHT),
            new SeedExercise("Treadmill Run", MuscleGroup.CARDIO, Equipment.MACHINE)
    );

    @Bean
    CommandLineRunner seedExercises(ExerciseRepository exerciseRepository) {
        return args -> {
            for (SeedExercise seed : SEED) {
                if (!exerciseRepository.existsByUserIsNullAndNameIgnoreCase(seed.name())) {
                    exerciseRepository.save(new Exercise(null, seed.name(), seed.muscle(), seed.equipment()));
                }
            }
        };
    }
}
