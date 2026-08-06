package com.yuvaansh.fitness_tracker_api.controller;

import com.yuvaansh.fitness_tracker_api.dto.FoodDetailResponse;
import com.yuvaansh.fitness_tracker_api.dto.FoodSummaryResponse;
import com.yuvaansh.fitness_tracker_api.exception.ExternalServiceException;
import com.yuvaansh.fitness_tracker_api.exception.GlobalExceptionHandler;
import com.yuvaansh.fitness_tracker_api.exception.ResourceNotFoundException;
import com.yuvaansh.fitness_tracker_api.service.FoodService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class FoodControllerTest {

    @Mock
    private FoodService foodService;

    @InjectMocks
    private FoodController foodController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(foodController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void search_returnsResults() throws Exception {
        when(foodService.search("banana", 25))
                .thenReturn(List.of(new FoodSummaryResponse(173944L, "Banana, raw", null, 89)));

        mockMvc.perform(get("/api/foods/search")
                        .principal(() -> "alice")
                        .param("query", "banana"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fdcId").value(173944))
                .andExpect(jsonPath("$[0].description").value("Banana, raw"))
                .andExpect(jsonPath("$[0].caloriesPer100g").value(89));
    }

    @Test
    void search_missingQueryParam_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/foods/search").principal(() -> "alice"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getFood_returnsDetail() throws Exception {
        FoodDetailResponse detail = new FoodDetailResponse();
        detail.setFdcId(173944L);
        detail.setDescription("Banana, raw");
        detail.setCalories(89);
        detail.setProtein(new BigDecimal("1.09"));
        when(foodService.getFood(173944L)).thenReturn(detail);

        mockMvc.perform(get("/api/foods/173944").principal(() -> "alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fdcId").value(173944))
                .andExpect(jsonPath("$.calories").value(89));
    }

    @Test
    void getFood_whenNotFound_returns404() throws Exception {
        when(foodService.getFood(999L)).thenThrow(new ResourceNotFoundException());

        mockMvc.perform(get("/api/foods/999").principal(() -> "alice"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getFood_whenUpstreamFails_returns502() throws Exception {
        when(foodService.getFood(1L)).thenThrow(new ExternalServiceException("down"));

        mockMvc.perform(get("/api/foods/1").principal(() -> "alice"))
                .andExpect(status().is(502))
                .andExpect(jsonPath("$.message").exists());
    }
}
