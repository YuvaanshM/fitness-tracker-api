package com.yuvaansh.fitness_tracker_api.client;

import com.yuvaansh.fitness_tracker_api.exception.ExternalServiceException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class GeminiAiClientTest {

    private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta";

    private GeminiAiClient clientBoundTo(MockRestServiceServer[] holder, String apiKey) {
        RestClient.Builder builder = RestClient.builder();
        holder[0] = MockRestServiceServer.bindTo(builder).build();
        return new GeminiAiClient(builder, BASE_URL, apiKey, "gemini-2.0-flash");
    }

    @Test
    void generate_extractsFirstCandidateText() {
        MockRestServiceServer[] holder = new MockRestServiceServer[1];
        GeminiAiClient client = clientBoundTo(holder, "test-key");

        String json = """
                {
                  "candidates": [
                    {"content":{"parts":[{"text":"Hello athlete"}]}}
                  ]
                }
                """;
        holder[0].expect(requestTo(containsString("/models/gemini-2.0-flash:generateContent")))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        assertThat(client.generate("system", "user")).isEqualTo("Hello athlete");
        holder[0].verify();
    }

    @Test
    void generate_withoutApiKey_throwsExternalService() {
        MockRestServiceServer[] holder = new MockRestServiceServer[1];
        GeminiAiClient client = clientBoundTo(holder, "");

        assertThatThrownBy(() -> client.generate("system", "user"))
                .isInstanceOf(ExternalServiceException.class);
    }

    @Test
    void generate_whenServerError_throwsExternalService() {
        MockRestServiceServer[] holder = new MockRestServiceServer[1];
        GeminiAiClient client = clientBoundTo(holder, "test-key");

        holder[0].expect(requestTo(containsString("/models/gemini-2.0-flash:generateContent")))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThatThrownBy(() -> client.generate("system", "user"))
                .isInstanceOf(ExternalServiceException.class);
    }
}
