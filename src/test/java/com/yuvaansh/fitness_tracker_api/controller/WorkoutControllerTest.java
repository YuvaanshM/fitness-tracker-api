package com.yuvaansh.fitness_tracker_api.controller;

import com.yuvaansh.fitness_tracker_api.dto.FinishWorkoutRequest;
import com.yuvaansh.fitness_tracker_api.dto.LogSetRequest;
import com.yuvaansh.fitness_tracker_api.dto.StartWorkoutRequest;
import com.yuvaansh.fitness_tracker_api.dto.WorkoutSessionResponse;
import com.yuvaansh.fitness_tracker_api.exception.GlobalExceptionHandler;
import com.yuvaansh.fitness_tracker_api.service.WorkoutService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class WorkoutControllerTest {

    @Mock
    private WorkoutService workoutService;

    @InjectMocks
    private WorkoutController workoutController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(workoutController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void startWorkout_returnsCreated() throws Exception {
        when(workoutService.startWorkout(any(Principal.class), any(StartWorkoutRequest.class)))
                .thenReturn(buildResponse());

        mockMvc.perform(post("/api/workouts")
                        .principal(() -> "alice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(50));
    }

    @Test
    void logSet_returnsCreated() throws Exception {
        when(workoutService.logSet(any(Principal.class), eq(50L), any(LogSetRequest.class)))
                .thenReturn(buildResponse());

        mockMvc.perform(post("/api/workouts/50/sets")
                        .principal(() -> "alice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"exerciseId":5,"setNumber":1,"reps":10,"weightLbs":135.00,"restSeconds":90}
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void logSet_withMissingFields_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/workouts/50/sets")
                        .principal(() -> "alice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void finishWorkout_returnsOk() throws Exception {
        when(workoutService.finishWorkout(any(Principal.class), eq(50L), any(FinishWorkoutRequest.class)))
                .thenReturn(buildResponse());

        mockMvc.perform(patch("/api/workouts/50")
                        .principal(() -> "alice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void listWorkouts_returnsOk() throws Exception {
        when(workoutService.listWorkouts(any(Principal.class), isNull(), isNull()))
                .thenReturn(List.of(buildResponse()));

        mockMvc.perform(get("/api/workouts").principal(() -> "alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(50));
    }

    private WorkoutSessionResponse buildResponse() {
        WorkoutSessionResponse response = new WorkoutSessionResponse();
        response.setId(50L);
        response.setStartedAt(LocalDateTime.of(2026, 6, 28, 8, 0));
        response.setSets(List.of());
        return response;
    }
}
