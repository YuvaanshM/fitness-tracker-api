package com.yuvaansh.fitness_tracker_api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuvaansh.fitness_tracker_api.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MealIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void mealFlow_registerLoginCreateAndFetchByDate() throws Exception {
        String username = "mealuser_" + System.nanoTime();
        String token = registerAndGetToken(username);

        String mealJson = """
                {
                  "name": "Chicken Bowl",
                  "mealDate": "2026-06-28",
                  "calories": 650,
                  "protein": 45.00,
                  "carbs": 55.00,
                  "fats": 20.00,
                  "sugar": 5.00,
                  "fiber": 8.00
                }
                """;

        mockMvc.perform(post("/api/meals")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mealJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Chicken Bowl"))
                .andExpect(jsonPath("$.mealDate").value("2026-06-28"))
                .andExpect(jsonPath("$.calories").value(650))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());

        mockMvc.perform(get("/api/meals")
                        .header("Authorization", "Bearer " + token)
                        .param("date", "2026-06-28"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Chicken Bowl"))
                .andExpect(jsonPath("$[0].mealDate").value("2026-06-28"));
    }

    @Test
    void createMeal_withoutToken_returnsUnauthorized() throws Exception {
        String mealJson = """
                {
                  "name": "Chicken Bowl",
                  "mealDate": "2026-06-28",
                  "calories": 650,
                  "protein": 45.00,
                  "carbs": 55.00,
                  "fats": 20.00
                }
                """;

        mockMvc.perform(post("/api/meals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mealJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void usersCannotSeeEachOthersMeals() throws Exception {
        String alice = "alice_" + System.nanoTime();
        String bob = "bob_" + System.nanoTime();
        String aliceToken = registerAndGetToken(alice);
        String bobToken = registerAndGetToken(bob);

        String mealJson = """
                {
                  "name": "Private Meal",
                  "mealDate": "2026-06-28",
                  "calories": 400,
                  "protein": 25.00,
                  "carbs": 30.00,
                  "fats": 12.00
                }
                """;

        mockMvc.perform(post("/api/meals")
                        .header("Authorization", "Bearer " + aliceToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mealJson))
                .andExpect(status().isCreated());

        MvcResult result = mockMvc.perform(get("/api/meals")
                        .header("Authorization", "Bearer " + bobToken)
                        .param("date", "2026-06-28"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode meals = objectMapper.readTree(result.getResponse().getContentAsString());
        assertThat(meals).isEmpty();
    }

    @Test
    void authEndpoints_areRateLimitedByClientIp() throws Exception {
        String loginJson = """
                {
                  "username": "missing-user",
                  "password": "password123"
                }
                """;

        for (int i = 0; i < 10; i++) {
            mockMvc.perform(post("/api/auth/login")
                            .header("X-Forwarded-For", "203.0.113.77")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loginJson))
                    .andExpect(status().isUnauthorized());
        }

        mockMvc.perform(post("/api/auth/login")
                        .header("X-Forwarded-For", "203.0.113.77")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.message").value("Too many authentication attempts. Please try again later."));
    }

    private String registerAndGetToken(String username) throws Exception {
        RegisterRequest request = new RegisterRequest(
                username,
                "password123",
                "M",
                180.0,
                170.0,
                "MODERATELY_ACTIVE",
                "MAINTAIN",
                0.0
        );

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        String token = body.get("token").asText();
        assertThat(token).isNotBlank();
        return token;
    }
}
