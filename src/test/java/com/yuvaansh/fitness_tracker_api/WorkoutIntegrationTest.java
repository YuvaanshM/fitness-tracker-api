package com.yuvaansh.fitness_tracker_api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WorkoutIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void fullWorkoutFlow_buildRegimeThenLogSession() throws Exception {
        String token = registerAndGetToken("lifter_" + System.nanoTime());

        // Create a custom exercise
        long exerciseId = createExercise(token, "My Bench Press", "CHEST", "BARBELL");

        // Create a routine (regime) and add a "Push" day
        long routineId = createRoutine(token, "Push Pull Legs");
        long dayId = addDay(token, routineId, "Push");

        // Add the exercise to the day with a rest timer duration
        mockMvc.perform(post("/api/routines/" + routineId + "/days/" + dayId + "/exercises")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"exerciseId":%d,"targetSets":4,"targetReps":8,"restSeconds":120}
                                """.formatted(exerciseId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.days[0].exercises[0].exerciseId").value((int) exerciseId))
                .andExpect(jsonPath("$.days[0].exercises[0].restSeconds").value(120));

        // Start a workout session from that routine day
        MvcResult startResult = mockMvc.perform(post("/api/workouts")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"routineDayId":%d,"notes":"felt strong"}
                                """.formatted(dayId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.routineDayId").value((int) dayId))
                .andReturn();
        long sessionId = readJson(startResult).get("id").asLong();

        // Log a set
        mockMvc.perform(post("/api/workouts/" + sessionId + "/sets")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"exerciseId":%d,"setNumber":1,"reps":8,"weightLbs":185.00,"restSeconds":120}
                                """.formatted(exerciseId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sets[0].reps").value(8))
                .andExpect(jsonPath("$.sets[0].weightLbs").value(185.00));

        // Finish the workout
        mockMvc.perform(patch("/api/workouts/" + sessionId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.endedAt").exists());

        // List workouts for a date range containing today
        LocalDate today = LocalDate.now();
        mockMvc.perform(get("/api/workouts")
                        .header("Authorization", "Bearer " + token)
                        .param("from", today.minusDays(1).toString())
                        .param("to", today.plusDays(1).toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value((int) sessionId));
    }

    @Test
    void globalExercisesAreSeededAndVisible() throws Exception {
        String token = registerAndGetToken("seeded_" + System.nanoTime());

        mockMvc.perform(get("/api/exercises")
                        .header("Authorization", "Bearer " + token)
                        .param("query", "squat"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].global").value(true));
    }

    @Test
    void usersCannotAccessEachOthersWorkoutsOrRoutines() throws Exception {
        String aliceToken = registerAndGetToken("alice_" + System.nanoTime());
        String bobToken = registerAndGetToken("bob_" + System.nanoTime());

        long aliceRoutineId = createRoutine(aliceToken, "Alice PPL");
        long aliceExerciseId = createExercise(aliceToken, "Alice Row", "BACK", "BARBELL");
        MvcResult startResult = mockMvc.perform(post("/api/workouts")
                        .header("Authorization", "Bearer " + aliceToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isCreated())
                .andReturn();
        long aliceSessionId = readJson(startResult).get("id").asLong();

        // Bob cannot read Alice's routine
        mockMvc.perform(get("/api/routines/" + aliceRoutineId)
                        .header("Authorization", "Bearer " + bobToken))
                .andExpect(status().isNotFound());

        // Bob cannot read Alice's workout session
        mockMvc.perform(get("/api/workouts/" + aliceSessionId)
                        .header("Authorization", "Bearer " + bobToken))
                .andExpect(status().isNotFound());

        // Bob cannot log a set into Alice's session
        mockMvc.perform(post("/api/workouts/" + aliceSessionId + "/sets")
                        .header("Authorization", "Bearer " + bobToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"exerciseId":%d,"setNumber":1,"reps":8,"weightLbs":100.00}
                                """.formatted(aliceExerciseId)))
                .andExpect(status().isNotFound());

        // Bob's own workout list is empty
        MvcResult bobList = mockMvc.perform(get("/api/workouts")
                        .header("Authorization", "Bearer " + bobToken))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(readJson(bobList)).isEmpty();
    }

    @Test
    void workoutEndpoints_requireAuthentication() throws Exception {
        mockMvc.perform(get("/api/workouts")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/routines")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/exercises")).andExpect(status().isUnauthorized());
    }

    private long createExercise(String token, String name, String muscle, String equipment) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/exercises")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"%s","primaryMuscle":"%s","equipment":"%s"}
                                """.formatted(name, muscle, equipment)))
                .andExpect(status().isCreated())
                .andReturn();
        return readJson(result).get("id").asLong();
    }

    private long createRoutine(String token, String name) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/routines")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"%s"}
                                """.formatted(name)))
                .andExpect(status().isCreated())
                .andReturn();
        return readJson(result).get("id").asLong();
    }

    private long addDay(String token, long routineId, String name) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/routines/" + routineId + "/days")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"%s"}
                                """.formatted(name)))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode days = readJson(result).get("days");
        return days.get(days.size() - 1).get("id").asLong();
    }

    private JsonNode readJson(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }

    private String registerAndGetToken(String username) throws Exception {
        String registerJson = """
                {
                  "username": "%s",
                  "password": "password123",
                  "sex": "M",
                  "height": 180.0,
                  "weight": 170.0,
                  "activityLevel": "MODERATELY_ACTIVE",
                  "goal": "MAINTAIN",
                  "goalWeightChangePerWeek": 0.0,
                  "dateOfBirth": "1996-05-15"
                }
                """.formatted(username);

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
    }
}
