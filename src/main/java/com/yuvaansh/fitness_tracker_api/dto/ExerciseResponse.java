package com.yuvaansh.fitness_tracker_api.dto;

import com.yuvaansh.fitness_tracker_api.entity.Exercise;
import com.yuvaansh.fitness_tracker_api.enums.Equipment;
import com.yuvaansh.fitness_tracker_api.enums.MuscleGroup;

public class ExerciseResponse {

    private Long id;
    private String name;
    private MuscleGroup primaryMuscle;
    private Equipment equipment;
    private boolean global;

    public ExerciseResponse() {
    }

    public static ExerciseResponse fromEntity(Exercise exercise) {
        ExerciseResponse response = new ExerciseResponse();
        response.setId(exercise.getId());
        response.setName(exercise.getName());
        response.setPrimaryMuscle(exercise.getPrimaryMuscle());
        response.setEquipment(exercise.getEquipment());
        response.setGlobal(exercise.getUser() == null);
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public boolean isGlobal() {
        return global;
    }

    public void setGlobal(boolean global) {
        this.global = global;
    }
}
