package com.yuvaansh.fitness_tracker_api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class CreateRoutineDayRequest {

    @NotBlank
    @Size(max = 100)
    private String name;

    /**
     * Optional display order. When omitted, the day is appended to the end.
     */
    @PositiveOrZero
    @Max(1000)
    private Integer orderIndex;

    public CreateRoutineDayRequest() {
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
}
