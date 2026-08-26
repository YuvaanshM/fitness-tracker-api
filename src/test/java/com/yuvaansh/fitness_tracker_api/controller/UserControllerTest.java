package com.yuvaansh.fitness_tracker_api.controller;

import com.yuvaansh.fitness_tracker_api.dto.MetricsResponse;
import com.yuvaansh.fitness_tracker_api.entity.User;
import com.yuvaansh.fitness_tracker_api.exception.GlobalExceptionHandler;
import com.yuvaansh.fitness_tracker_api.exception.MetricsUnavailableException;
import com.yuvaansh.fitness_tracker_api.repository.UserRepository;
import com.yuvaansh.fitness_tracker_api.service.MetricsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MetricsService metricsService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void me_returnsProfile() throws Exception {
        User user = new User("alice", "hash", "F", 165.0, 140.0,
                "MODERATELY_ACTIVE", "LOSE", -1.0, LocalDate.of(1996, 5, 15));
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/user/me").principal(() -> "alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.dateOfBirth").value("1996-05-15"));
    }

    @Test
    void metrics_returnsOk() throws Exception {
        when(metricsService.getMetrics(any(Principal.class)))
                .thenReturn(new MetricsResponse(30, 1.55, 1800L, 2790L, 2290L, 170L));

        mockMvc.perform(get("/api/user/metrics").principal(() -> "alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bmr").value(1800))
                .andExpect(jsonPath("$.tdee").value(2790))
                .andExpect(jsonPath("$.recommendedCalories").value(2290))
                .andExpect(jsonPath("$.proteinTargetGrams").value(170));
    }

    @Test
    void metrics_whenUnavailable_returnsUnprocessableEntity() throws Exception {
        when(metricsService.getMetrics(any(Principal.class)))
                .thenThrow(new MetricsUnavailableException("Date of birth is required to compute metrics"));

        mockMvc.perform(get("/api/user/metrics").principal(() -> "alice"))
                .andExpect(status().is(422))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void updateMe_updatesEditableFieldsAndReturnsProfile() throws Exception {
        User user = new User("alice", "hash", "F", 165.0, 140.0,
                "MODERATELY_ACTIVE", "LOSE", -1.0, LocalDate.of(1996, 5, 15));
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/api/user/me")
                        .principal(() -> "alice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "height": 170.0,
                                  "weight": 150.0,
                                  "activityLevel": "VERY_ACTIVE",
                                  "goal": "GAIN",
                                  "goalWeightChangePerWeek": 0.5,
                                  "dateOfBirth": "1996-05-15"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.weight").value(150.0))
                .andExpect(jsonPath("$.activityLevel").value("VERY_ACTIVE"))
                .andExpect(jsonPath("$.goal").value("GAIN"));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getWeight()).isEqualTo(150.0);
        assertThat(captor.getValue().getGoal()).isEqualTo("GAIN");
    }

    @Test
    void updateMe_withInvalidBody_returnsBadRequest() throws Exception {
        mockMvc.perform(put("/api/user/me")
                        .principal(() -> "alice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }
}
