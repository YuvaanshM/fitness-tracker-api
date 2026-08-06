package com.yuvaansh.fitness_tracker_api.controller;

import com.yuvaansh.fitness_tracker_api.dto.AiTextResponse;
import com.yuvaansh.fitness_tracker_api.dto.ParseMealRequest;
import com.yuvaansh.fitness_tracker_api.dto.ParsedMealResponse;
import com.yuvaansh.fitness_tracker_api.exception.ExternalServiceException;
import com.yuvaansh.fitness_tracker_api.exception.GlobalExceptionHandler;
import com.yuvaansh.fitness_tracker_api.service.AiService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AiControllerTest {

    @Mock
    private AiService aiService;

    @InjectMocks
    private AiController aiController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(aiController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void nutritionInsights_returnsOk() throws Exception {
        when(aiService.nutritionInsights(any(Principal.class)))
                .thenReturn(new AiTextResponse("Add a protein source at lunch."));

        mockMvc.perform(post("/api/ai/nutrition-insights").principal(() -> "alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Add a protein source at lunch."));
    }

    @Test
    void parseMeal_returnsOk() throws Exception {
        when(aiService.parseMeal(any(Principal.class), any(ParseMealRequest.class)))
                .thenReturn(new ParsedMealResponse(
                        "Eggs and toast",
                        420,
                        new BigDecimal("28.00"),
                        new BigDecimal("30.00"),
                        new BigDecimal("18.00"),
                        new BigDecimal("4.00"),
                        new BigDecimal("3.00"),
                        true));

        mockMvc.perform(post("/api/ai/parse-meal")
                        .principal(() -> "alice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"text":"2 eggs and toast"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Eggs and toast"))
                .andExpect(jsonPath("$.requiresConfirmation").value(true));
    }

    @Test
    void parseMeal_invalidBody_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/ai/parse-meal")
                        .principal(() -> "alice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void workoutSuggestion_whenProviderDown_returns502() throws Exception {
        when(aiService.workoutSuggestion(any(Principal.class)))
                .thenThrow(new ExternalServiceException("down"));

        mockMvc.perform(post("/api/ai/workout-suggestion").principal(() -> "alice"))
                .andExpect(status().is(502))
                .andExpect(jsonPath("$.message").exists());
    }
}
