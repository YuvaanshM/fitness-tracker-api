package com.yuvaansh.fitness_tracker_api.dto;

import com.yuvaansh.fitness_tracker_api.entity.RoutineDay;

import java.util.List;

public class RoutineDayResponse {

    private Long id;
    private String name;
    private Integer orderIndex;
    private List<RoutineExerciseResponse> exercises;

    public RoutineDayResponse() {
    }

    public static RoutineDayResponse fromEntity(RoutineDay day) {
        RoutineDayResponse response = new RoutineDayResponse();
        response.setId(day.getId());
        response.setName(day.getName());
        response.setOrderIndex(day.getOrderIndex());
        response.setExercises(day.getExercises().stream()
                .map(RoutineExerciseResponse::fromEntity)
                .toList());
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

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public List<RoutineExerciseResponse> getExercises() {
        return exercises;
    }

    public void setExercises(List<RoutineExerciseResponse> exercises) {
        this.exercises = exercises;
    }
}
