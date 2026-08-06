package com.yuvaansh.fitness_tracker_api.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yuvaansh.fitness_tracker_api.dto.FoodDetailResponse;
import com.yuvaansh.fitness_tracker_api.dto.FoodSummaryResponse;
import com.yuvaansh.fitness_tracker_api.exception.ExternalServiceException;
import com.yuvaansh.fitness_tracker_api.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Thin wrapper over USDA FoodData Central's REST API. All macro values are
 * normalized to "per 100 g". The API key is read from configuration (env only)
 * and never logged. Upstream failures surface as {@link ExternalServiceException}.
 */
@Component
public class UsdaFoodClient {

    // USDA nutrient numbers (stable identifiers across datasets).
    private static final String N_ENERGY = "208";
    private static final String N_PROTEIN = "203";
    private static final String N_FAT = "204";
    private static final String N_CARBS = "205";
    private static final String N_FIBER = "291";
    private static final String N_SUGAR = "269";
    private static final String N_CALCIUM = "301";
    private static final String N_IRON = "303";
    private static final String N_POTASSIUM = "306";
    private static final String N_SODIUM = "307";
    private static final String N_VIT_C = "401";
    private static final String N_VIT_A = "320";
    private static final String N_VIT_D = "328";
    private static final String N_CHOLESTEROL = "601";

    private final RestClient restClient;
    private final String apiKey;

    public UsdaFoodClient(RestClient.Builder builder,
                          @Value("${usda.api.base-url}") String baseUrl,
                          @Value("${usda.api.key:}") String apiKey) {
        this.restClient = builder.baseUrl(baseUrl).build();
        this.apiKey = apiKey;
    }

    public List<FoodSummaryResponse> searchFoods(String query, int pageSize) {
        requireApiKey();
        UsdaSearchResponse response = getForObject(
                uri -> uri.path("/foods/search")
                        .queryParam("api_key", apiKey)
                        .queryParam("query", query)
                        .queryParam("pageSize", pageSize)
                        .build(),
                UsdaSearchResponse.class);

        List<FoodSummaryResponse> results = new ArrayList<>();
        if (response != null && response.foods() != null) {
            for (UsdaFood food : response.foods()) {
                Map<String, BigDecimal> nutrients = toNutrientMap(food.foodNutrients());
                Integer calories = toInteger(nutrients.get(N_ENERGY));
                results.add(new FoodSummaryResponse(
                        food.fdcId(),
                        food.description(),
                        firstNonBlank(food.brandName(), food.brandOwner()),
                        calories));
            }
        }
        return results;
    }

    public FoodDetailResponse getFood(long fdcId) {
        requireApiKey();
        UsdaFood food = getForObject(
                uri -> uri.path("/food/{fdcId}")
                        .queryParam("api_key", apiKey)
                        .build(fdcId),
                UsdaFood.class);

        if (food == null) {
            throw new ResourceNotFoundException();
        }

        Map<String, BigDecimal> nutrients = toNutrientMap(food.foodNutrients());
        FoodDetailResponse detail = new FoodDetailResponse();
        detail.setFdcId(food.fdcId());
        detail.setDescription(food.description());
        detail.setBrandName(firstNonBlank(food.brandName(), food.brandOwner()));
        detail.setCalories(toInteger(nutrients.get(N_ENERGY)));
        detail.setProtein(nutrients.get(N_PROTEIN));
        detail.setCarbs(nutrients.get(N_CARBS));
        detail.setFats(nutrients.get(N_FAT));
        detail.setSugar(nutrients.get(N_SUGAR));
        detail.setFiber(nutrients.get(N_FIBER));
        detail.setSodiumMg(nutrients.get(N_SODIUM));
        detail.setPotassiumMg(nutrients.get(N_POTASSIUM));
        detail.setCholesterolMg(nutrients.get(N_CHOLESTEROL));
        detail.setCalciumMg(nutrients.get(N_CALCIUM));
        detail.setIronMg(nutrients.get(N_IRON));
        detail.setVitaminAMcg(nutrients.get(N_VIT_A));
        detail.setVitaminCMg(nutrients.get(N_VIT_C));
        detail.setVitaminDMcg(nutrients.get(N_VIT_D));
        return detail;
    }

    private <T> T getForObject(java.util.function.Function<org.springframework.web.util.UriBuilder, java.net.URI> uriFn,
                               Class<T> type) {
        try {
            return restClient.get()
                    .uri(uriFn)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), (req, res) -> {
                        if (res.getStatusCode().value() == 404) {
                            throw new ResourceNotFoundException();
                        }
                        throw new ExternalServiceException("USDA request rejected");
                    })
                    .onStatus(status -> status.is5xxServerError(), (req, res) -> {
                        throw new ExternalServiceException("USDA service error");
                    })
                    .body(type);
        } catch (ResourceNotFoundException | ExternalServiceException ex) {
            throw ex;
        } catch (RestClientException ex) {
            throw new ExternalServiceException("Unable to reach USDA service", ex);
        }
    }

    private void requireApiKey() {
        if (!StringUtils.hasText(apiKey)) {
            throw new ExternalServiceException("USDA API key is not configured");
        }
    }

    /**
     * Handles both search-result nutrients (flat: nutrientNumber/value) and
     * detail nutrients (nested: nutrient.number/amount).
     */
    private Map<String, BigDecimal> toNutrientMap(List<UsdaNutrient> nutrients) {
        Map<String, BigDecimal> map = new java.util.HashMap<>();
        if (nutrients == null) {
            return map;
        }
        for (UsdaNutrient n : nutrients) {
            String number = n.nutrientNumber();
            BigDecimal value = n.value();
            if (number == null && n.nutrient() != null) {
                number = n.nutrient().number();
            }
            if (value == null) {
                value = n.amount();
            }
            if (number != null && value != null && !map.containsKey(number)) {
                map.put(number, value);
            }
        }
        return map;
    }

    private Integer toInteger(BigDecimal value) {
        return value == null ? null : value.setScale(0, java.math.RoundingMode.HALF_UP).intValue();
    }

    private String firstNonBlank(String a, String b) {
        if (StringUtils.hasText(a)) {
            return a;
        }
        return StringUtils.hasText(b) ? b : null;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record UsdaSearchResponse(List<UsdaFood> foods) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record UsdaFood(long fdcId, String description, String brandName, String brandOwner,
                            List<UsdaNutrient> foodNutrients) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record UsdaNutrient(String nutrientNumber, BigDecimal value, BigDecimal amount, NutrientInfo nutrient) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record NutrientInfo(String number, String unitName) {
    }
}
