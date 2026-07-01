package com.yuvaansh.fitness_tracker_api.controller;

import com.yuvaansh.fitness_tracker_api.dto.CreateExerciseRequest;
import com.yuvaansh.fitness_tracker_api.dto.ExerciseResponse;
import com.yuvaansh.fitness_tracker_api.enums.Equipment;
import com.yuvaansh.fitness_tracker_api.enums.MuscleGroup;
import com.yuvaansh.fitness_tracker_api.exception.GlobalExceptionHandler;
import com.yuvaansh.fitness_tracker_api.service.ExerciseService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.security.Principal;

@ExtendWith(MockitoExtension.class)
class ExerciseControllerTest {

    @Mock
    private ExerciseService exerciseService;

    @InjectMocks
    private ExerciseController exerciseController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(exerciseController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void listExercises_returnsOk() throws Exception {
        when(exerciseService.listExercises(any(Principal.class), isNull(), isNull()))
                .thenReturn(List.of(buildResponse()));

        mockMvc.perform(get("/api/exercises").principal(() -> "alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Bench Press"))
                .andExpect(jsonPath("$[0].global").value(true));
    }

    @Test
    void createExercise_returnsCreated() throws Exception {
        when(exerciseService.createExercise(any(Principal.class), any(CreateExerciseRequest.class)))
                .thenReturn(buildResponse());

        mockMvc.perform(post("/api/exercises")
                        .principal(() -> "alice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Bench Press","primaryMuscle":"CHEST","equipment":"BARBELL"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Bench Press"));
    }

    @Test
    void createExercise_withInvalidBody_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/exercises")
                        .principal(() -> "alice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    private ExerciseResponse buildResponse() {
        ExerciseResponse response = new ExerciseResponse();
        response.setId(1L);
        response.setName("Bench Press");
        response.setPrimaryMuscle(MuscleGroup.CHEST);
        response.setEquipment(Equipment.BARBELL);
        response.setGlobal(true);
        return response;
    }
}
