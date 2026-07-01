package com.yuvaansh.fitness_tracker_api.controller;

import com.yuvaansh.fitness_tracker_api.dto.AddRoutineExerciseRequest;
import com.yuvaansh.fitness_tracker_api.dto.CreateRoutineDayRequest;
import com.yuvaansh.fitness_tracker_api.dto.CreateRoutineRequest;
import com.yuvaansh.fitness_tracker_api.dto.RoutineResponse;
import com.yuvaansh.fitness_tracker_api.exception.GlobalExceptionHandler;
import com.yuvaansh.fitness_tracker_api.service.RoutineService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RoutineControllerTest {

    @Mock
    private RoutineService routineService;

    @InjectMocks
    private RoutineController routineController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(routineController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void createRoutine_returnsCreated() throws Exception {
        when(routineService.createRoutine(any(Principal.class), any(CreateRoutineRequest.class)))
                .thenReturn(buildResponse());

        mockMvc.perform(post("/api/routines")
                        .principal(() -> "alice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Push Pull Legs","description":"6 day split"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Push Pull Legs"));
    }

    @Test
    void createRoutine_withBlankName_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/routines")
                        .principal(() -> "alice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"  "}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getRoutine_returnsOk() throws Exception {
        when(routineService.getRoutine(any(Principal.class), eq(1L))).thenReturn(buildResponse());

        mockMvc.perform(get("/api/routines/1").principal(() -> "alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void addDay_returnsCreated() throws Exception {
        when(routineService.addDay(any(Principal.class), eq(1L), any(CreateRoutineDayRequest.class)))
                .thenReturn(buildResponse());

        mockMvc.perform(post("/api/routines/1/days")
                        .principal(() -> "alice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Push"}
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void addExercise_returnsCreated() throws Exception {
        when(routineService.addExercise(
                any(Principal.class), eq(1L), eq(2L), any(AddRoutineExerciseRequest.class)))
                .thenReturn(buildResponse());

        mockMvc.perform(post("/api/routines/1/days/2/exercises")
                        .principal(() -> "alice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"exerciseId":5,"targetSets":4,"targetReps":8,"restSeconds":120}
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void addExercise_withMissingExerciseId_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/routines/1/days/2/exercises")
                        .principal(() -> "alice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    private RoutineResponse buildResponse() {
        RoutineResponse response = new RoutineResponse();
        response.setId(1L);
        response.setName("Push Pull Legs");
        response.setDescription("6 day split");
        response.setDays(java.util.List.of());
        return response;
    }
}
