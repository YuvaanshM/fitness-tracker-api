package com.yuvaansh.fitness_tracker_api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuvaansh.fitness_tracker_api.client.AiClient;
import com.yuvaansh.fitness_tracker_api.dto.AiTextResponse;
import com.yuvaansh.fitness_tracker_api.dto.MetricsResponse;
import com.yuvaansh.fitness_tracker_api.dto.ParseMealRequest;
import com.yuvaansh.fitness_tracker_api.dto.ParsedMealResponse;
import com.yuvaansh.fitness_tracker_api.entity.Routine;
import com.yuvaansh.fitness_tracker_api.entity.User;
import com.yuvaansh.fitness_tracker_api.entity.WorkoutSession;
import com.yuvaansh.fitness_tracker_api.exception.ExternalServiceException;
import com.yuvaansh.fitness_tracker_api.exception.InvalidRequestException;
import com.yuvaansh.fitness_tracker_api.exception.UserNotFoundException;
import com.yuvaansh.fitness_tracker_api.repository.MealRepository;
import com.yuvaansh.fitness_tracker_api.repository.NutritionTrendProjection;
import com.yuvaansh.fitness_tracker_api.repository.RoutineRepository;
import com.yuvaansh.fitness_tracker_api.repository.UserRepository;
import com.yuvaansh.fitness_tracker_api.repository.WorkoutSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Server-side AI features. Prompts are built from the authenticated user's own
 * data only; the API key never leaves the {@link AiClient} implementation.
 */
@Service
@Transactional(readOnly = true)
public class AiService {

    private static final int MAX_AI_CALLS_PER_MINUTE = 10;
    private static final long WINDOW_MILLIS = 60_000L;

    private final AiClient aiClient;
    private final UserRepository userRepository;
    private final MealRepository mealRepository;
    private final WorkoutSessionRepository workoutSessionRepository;
    private final RoutineRepository routineRepository;
    private final MetricsService metricsService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Clock clock;
    private final Map<String, RateWindow> rateWindows = new ConcurrentHashMap<>();

    public AiService(
            AiClient aiClient,
            UserRepository userRepository,
            MealRepository mealRepository,
            WorkoutSessionRepository workoutSessionRepository,
            RoutineRepository routineRepository,
            MetricsService metricsService,
            Clock clock) {
        this.aiClient = aiClient;
        this.userRepository = userRepository;
        this.mealRepository = mealRepository;
        this.workoutSessionRepository = workoutSessionRepository;
        this.routineRepository = routineRepository;
        this.metricsService = metricsService;
        this.clock = clock;
    }

    public AiTextResponse nutritionInsights(Principal principal) {
        User user = resolveUser(principal);
        enforceRateLimit(user.getUsername());

        LocalDate to = LocalDate.now(clock);
        LocalDate from = to.minusDays(6);
        List<NutritionTrendProjection> trend =
                mealRepository.summarizeTrendByUserIdAndDateRange(user.getId(), from, to);

        StringBuilder mealsSummary = new StringBuilder();
        if (trend.isEmpty()) {
            mealsSummary.append("No meals logged in the last 7 days.");
        } else {
            for (NutritionTrendProjection day : trend) {
                mealsSummary.append(String.format(
                        "- %s: %d kcal, P %.1f g, C %.1f g, F %.1f g%n",
                        day.getDate(),
                        day.getCalories() == null ? 0L : day.getCalories(),
                        toDouble(day.getProtein()),
                        toDouble(day.getCarbs()),
                        toDouble(day.getFats())));
            }
        }

        String metricsLine = buildMetricsLine(user);
        String system = """
                You are a concise nutrition coach for a fitness tracker.
                Give practical advice in under 200 words. Do not invent medical diagnoses.
                """;
        String userPrompt = """
                User profile:
                - sex: %s
                - goal: %s
                - weekly weight change goal (lbs): %s
                - activity: %s
                %s

                Last 7 days of daily intake:
                %s

                Provide 3-5 actionable nutrition insights.
                """.formatted(
                user.getSex(),
                user.getGoal(),
                user.getGoalWeightChangePerWeek(),
                user.getActivityLevel(),
                metricsLine,
                mealsSummary);

        return new AiTextResponse(aiClient.generate(system, userPrompt).trim());
    }

    public ParsedMealResponse parseMeal(Principal principal, ParseMealRequest request) {
        User user = resolveUser(principal);
        enforceRateLimit(user.getUsername());

        String text = request.getText() == null ? "" : request.getText().trim();
        if (!StringUtils.hasText(text)) {
            throw new InvalidRequestException("Meal text is required");
        }

        String system = """
                You estimate nutrition for a meal description.
                Reply with ONLY a JSON object (no markdown) using this schema:
                {"name":"string","calories":int,"protein":number,"carbs":number,"fats":number,"sugar":number,"fiber":number}
                Use grams for macros. If unsure, estimate conservatively.
                """;
        String raw = aiClient.generate(system, "Meal description: " + text);
        return parseMealJson(raw);
    }

    public AiTextResponse workoutSuggestion(Principal principal) {
        User user = resolveUser(principal);
        enforceRateLimit(user.getUsername());

        List<WorkoutSession> recent = workoutSessionRepository
                .findByUserIdOrderByStartedAtDesc(user.getId())
                .stream()
                .limit(5)
                .toList();
        List<Routine> routines = routineRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

        StringBuilder history = new StringBuilder();
        if (recent.isEmpty()) {
            history.append("No recent workout sessions.");
        } else {
            for (WorkoutSession session : recent) {
                String dayName = session.getRoutineDay() == null
                        ? "ad-hoc"
                        : session.getRoutineDay().getName();
                history.append(String.format(
                        "- session %d started %s (%s), sets=%d%n",
                        session.getId(),
                        session.getStartedAt(),
                        dayName,
                        session.getSets() == null ? 0 : session.getSets().size()));
            }
        }

        StringBuilder routineSummary = new StringBuilder();
        if (routines.isEmpty()) {
            routineSummary.append("No saved routines.");
        } else {
            for (Routine routine : routines) {
                routineSummary.append("- ").append(routine.getName());
                if (routine.getDays() != null && !routine.getDays().isEmpty()) {
                    String days = routine.getDays().stream()
                            .map(day -> day.getName())
                            .reduce((a, b) -> a + ", " + b)
                            .orElse("");
                    routineSummary.append(" [").append(days).append("]");
                }
                routineSummary.append('\n');
            }
        }

        String system = """
                You are a strength-training coach. Suggest the next workout session
                and one progressive-overload tip. Keep it under 200 words.
                """;
        String userPrompt = """
                User goal: %s
                Recent workouts:
                %s
                Routines:
                %s
                """.formatted(user.getGoal(), history, routineSummary);

        return new AiTextResponse(aiClient.generate(system, userPrompt).trim());
    }

    private String buildMetricsLine(User user) {
        try {
            MetricsResponse metrics = metricsService.computeMetrics(user);
            return String.format(
                    "- BMR: %d, TDEE: %d, recommended calories: %d, protein target: %d g",
                    metrics.getBmr(),
                    metrics.getTdee(),
                    metrics.getRecommendedCalories(),
                    metrics.getProteinTargetGrams());
        } catch (RuntimeException ignored) {
            return "- metrics: unavailable (complete profile / date of birth for BMR)";
        }
    }

    private ParsedMealResponse parseMealJson(String raw) {
        try {
            String json = stripCodeFences(raw);
            JsonNode node = objectMapper.readTree(json);
            String name = textOrDefault(node, "name", "Parsed meal");
            int calories = node.path("calories").asInt(0);
            return new ParsedMealResponse(
                    name.length() > 200 ? name.substring(0, 200) : name,
                    Math.max(calories, 0),
                    decimalOrZero(node, "protein"),
                    decimalOrZero(node, "carbs"),
                    decimalOrZero(node, "fats"),
                    decimalOrZero(node, "sugar"),
                    decimalOrZero(node, "fiber"),
                    true);
        } catch (Exception ex) {
            throw new ExternalServiceException("AI meal parse returned invalid data", ex);
        }
    }

    private String stripCodeFences(String raw) {
        String trimmed = raw == null ? "" : raw.trim();
        if (trimmed.startsWith("```")) {
            int firstNewline = trimmed.indexOf('\n');
            int lastFence = trimmed.lastIndexOf("```");
            if (firstNewline > 0 && lastFence > firstNewline) {
                return trimmed.substring(firstNewline + 1, lastFence).trim();
            }
        }
        int start = trimmed.indexOf('{');
        int end = trimmed.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return trimmed.substring(start, end + 1);
        }
        return trimmed;
    }

    private String textOrDefault(JsonNode node, String field, String fallback) {
        JsonNode value = node.get(field);
        if (value == null || value.isNull() || !StringUtils.hasText(value.asText())) {
            return fallback;
        }
        return value.asText().trim();
    }

    private BigDecimal decimalOrZero(JsonNode node, String field) {
        JsonNode value = node.get(field);
        if (value == null || value.isNull() || !value.isNumber()) {
            return BigDecimal.ZERO.setScale(2);
        }
        return BigDecimal.valueOf(value.asDouble()).setScale(2, java.math.RoundingMode.HALF_UP);
    }

    private double toDouble(BigDecimal value) {
        return value == null ? 0.0 : value.doubleValue();
    }

    private void enforceRateLimit(String username) {
        long now = clock.millis();
        RateWindow window = rateWindows.compute(username, (ignored, existing) -> {
            if (existing == null || now - existing.windowStartMillis >= WINDOW_MILLIS) {
                return new RateWindow(now, 1);
            }
            return new RateWindow(existing.windowStartMillis, existing.count + 1);
        });
        if (window.count > MAX_AI_CALLS_PER_MINUTE) {
            throw new InvalidRequestException("Too many AI requests. Please try again later.");
        }
    }

    private User resolveUser(Principal principal) {
        return userRepository.findByUsername(principal.getName())
                .orElseThrow(UserNotFoundException::new);
    }

    private record RateWindow(long windowStartMillis, int count) {
    }
}
