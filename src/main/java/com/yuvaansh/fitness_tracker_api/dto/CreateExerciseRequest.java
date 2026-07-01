package com.yuvaansh.fitness_tracker_api.dto;

import com.yuvaansh.fitness_tracker_api.enums.Equipment;
import com.yuvaansh.fitness_tracker_api.enums.MuscleGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateExerciseRequest {

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotNull
    private MuscleGroup primaryMuscle;

    @NotNull
    private Equipment equipment;

    public CreateExerciseRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MuscleGroup getPrimaryMuscle() {
        return primaryMuscle;
    }

    public void setPrimaryMuscle(MuscleGroup primaryMuscle) {
        this.primaryMuscle = primaryMuscle;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }
}
