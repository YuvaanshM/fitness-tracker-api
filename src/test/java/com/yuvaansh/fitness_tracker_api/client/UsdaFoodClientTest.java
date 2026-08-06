package com.yuvaansh.fitness_tracker_api.client;

import com.yuvaansh.fitness_tracker_api.dto.FoodDetailResponse;
import com.yuvaansh.fitness_tracker_api.dto.FoodSummaryResponse;
import com.yuvaansh.fitness_tracker_api.exception.ExternalServiceException;
import com.yuvaansh.fitness_tracker_api.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class UsdaFoodClientTest {

    private static final String BASE_URL = "https://api.nal.usda.gov/fdc/v1";

    private UsdaFoodClient clientBoundTo(MockRestServiceServer[] serverHolder, String apiKey) {
        RestClient.Builder builder = RestClient.builder();
        serverHolder[0] = MockRestServiceServer.bindTo(builder).build();
        return new UsdaFoodClient(builder, BASE_URL, apiKey);
    }

    @Test
    void searchFoods_mapsFlatNutrients() {
        MockRestServiceServer[] holder = new MockRestServiceServer[1];
        UsdaFoodClient client = clientBoundTo(holder, "test-key");

        String json = """
                {
                  "foods": [
                    {
                      "fdcId": 173944,
                      "description": "Banana, raw",
                      "foodNutrients": [
                        {"nutrientNumber": "208", "value": 89.4},
                        {"nutrientNumber": "203", "value": 1.09}
                      ]
                    }
                  ]
                }
                """;
        holder[0].expect(requestTo(containsString("/foods/search")))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        List<FoodSummaryResponse> results = client.searchFoods("banana", 25);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getFdcId()).isEqualTo(173944L);
        assertThat(results.get(0).getDescription()).isEqualTo("Banana, raw");
        assertThat(results.get(0).getCaloriesPer100g()).isEqualTo(89); // 89.4 -> 89
        holder[0].verify();
    }

    @Test
    void getFood_mapsNestedNutrients() {
        MockRestServiceServer[] holder = new MockRestServiceServer[1];
        UsdaFoodClient client = clientBoundTo(holder, "test-key");

        String json = """
                {
                  "fdcId": 173944,
                  "description": "Banana, raw",
                  "foodNutrients": [
                    {"amount": 89, "nutrient": {"number": "208", "unitName": "KCAL"}},
                    {"amount": 1.09, "nutrient": {"number": "203"}},
                    {"amount": 22.87, "nutrient": {"number": "205"}},
                    {"amount": 0.33, "nutrient": {"number": "204"}},
                    {"amount": 1.00, "nutrient": {"number": "307"}}
                  ]
                }
                """;
        holder[0].expect(requestTo(containsString("/food/173944")))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        FoodDetailResponse detail = client.getFood(173944L);

        assertThat(detail.getFdcId()).isEqualTo(173944L);
        assertThat(detail.getCalories()).isEqualTo(89);
        assertThat(detail.getProtein()).isEqualByComparingTo(new BigDecimal("1.09"));
        assertThat(detail.getCarbs()).isEqualByComparingTo(new BigDecimal("22.87"));
        assertThat(detail.getFats()).isEqualByComparingTo(new BigDecimal("0.33"));
        assertThat(detail.getSodiumMg()).isEqualByComparingTo(new BigDecimal("1.00"));
        holder[0].verify();
    }

    @Test
    void getFood_whenNotFound_throwsResourceNotFound() {
        MockRestServiceServer[] holder = new MockRestServiceServer[1];
        UsdaFoodClient client = clientBoundTo(holder, "test-key");

        holder[0].expect(requestTo(containsString("/food/999")))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThatThrownBy(() -> client.getFood(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getFood_whenServerError_throwsExternalService() {
        MockRestServiceServer[] holder = new MockRestServiceServer[1];
        UsdaFoodClient client = clientBoundTo(holder, "test-key");

        holder[0].expect(requestTo(containsString("/food/1")))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThatThrownBy(() -> client.getFood(1L))
                .isInstanceOf(ExternalServiceException.class);
    }

    @Test
    void searchFoods_withoutApiKey_throwsExternalService() {
        MockRestServiceServer[] holder = new MockRestServiceServer[1];
        UsdaFoodClient client = clientBoundTo(holder, "");

        assertThatThrownBy(() -> client.searchFoods("banana", 25))
                .isInstanceOf(ExternalServiceException.class);
    }
}
