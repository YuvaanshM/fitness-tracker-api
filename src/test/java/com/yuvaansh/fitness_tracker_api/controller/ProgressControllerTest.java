package com.yuvaansh.fitness_tracker_api.controller;

import com.yuvaansh.fitness_tracker_api.dto.BodyWeightEntryResponse;
import com.yuvaansh.fitness_tracker_api.dto.CreateBodyWeightEntryRequest;
import com.yuvaansh.fitness_tracker_api.dto.ExerciseStrengthTrendResponse;
import com.yuvaansh.fitness_tracker_api.dto.NutritionTrendResponse;
import com.yuvaansh.fitness_tracker_api.exception.GlobalExceptionHandler;
import com.yuvaansh.fitness_tracker_api.exception.InvalidRequestException;
import com.yuvaansh.fitness_tracker_api.service.ProgressService;
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

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProgressControllerTest {

    @Mock
    private ProgressService progressService;

    @InjectMocks
    private ProgressController progressController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(progressController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void recordWeight_returnsCreated() throws Exception {
        BodyWeightEntryResponse response = new BodyWeightEntryResponse();
        response.setId(1L);
        response.setEntryDate(LocalDate.of(2026, 7, 2));
        response.setWeightLbs(new BigDecimal("140.00"));
        when(progressService.recordWeight(any(Principal.class), any(CreateBodyWeightEntryRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/progress/weight")
                        .principal(() -> "alice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"entryDate":"2026-07-02","weightLbs":140.00}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.weightLbs").value(140.00));
    }

    @Test
    void recordWeight_invalidBody_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/progress/weight")
                        .principal(() -> "alice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getWeightTrend_returnsOk() throws Exception {
        BodyWeightEntryResponse response = new BodyWeightEntryResponse();
        response.setId(1L);
        response.setEntryDate(LocalDate.of(2026, 7, 1));
        response.setWeightLbs(new BigDecimal("140.00"));
        when(progressService.getWeightTrend(
                any(Principal.class),
                eq(LocalDate.of(2026, 7, 1)),
                eq(LocalDate.of(2026, 7, 7))))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/progress/weight")
                        .principal(() -> "alice")
                        .param("from", "2026-07-01")
                        .param("to", "2026-07-07"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].entryDate").value("2026-07-01"));
    }

    @Test
    void getNutritionTrend_returnsOk() throws Exception {
        when(progressService.getNutritionTrend(any(Principal.class), any(), any()))
                .thenReturn(List.of(new NutritionTrendResponse(
                        LocalDate.of(2026, 7, 1),
                        2000L,
                        new BigDecimal("150.00"),
                        new BigDecimal("200.00"),
                        new BigDecimal("60.00"))));

        mockMvc.perform(get("/api/progress/nutrition")
                        .principal(() -> "alice")
                        .param("from", "2026-07-01")
                        .param("to", "2026-07-07"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].calories").value(2000));
    }

    @Test
    void getExerciseTrend_whenInvalidRange_returnsBadRequest() throws Exception {
        when(progressService.getExerciseStrengthTrend(any(), eq(5L), any(), any()))
                .thenThrow(new InvalidRequestException("from must be on or before to"));

        mockMvc.perform(get("/api/progress/exercise/5")
                        .principal(() -> "alice")
                        .param("from", "2026-07-07")
                        .param("to", "2026-07-01"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getExerciseTrend_returnsOk() throws Exception {
        when(progressService.getExerciseStrengthTrend(any(), eq(5L), any(), any()))
                .thenReturn(List.of(new ExerciseStrengthTrendResponse(
                        20L,
                        LocalDateTime.of(2026, 7, 3, 10, 0),
                        new BigDecimal("145.00"),
                        new BigDecimal("1110.00"),
                        new BigDecimal("159.50"))));

        mockMvc.perform(get("/api/progress/exercise/5")
                        .principal(() -> "alice")
                        .param("from", "2026-07-01")
                        .param("to", "2026-07-07"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].topWeightLbs").value(145.00));
    }
}
