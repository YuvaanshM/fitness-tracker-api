package com.yuvaansh.fitness_tracker_api.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yuvaansh.fitness_tracker_api.exception.ExternalServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;

/**
 * Google Gemini implementation of {@link AiClient}. The API key is supplied
 * through configuration and is never logged or returned to callers.
 */
@Component
@ConditionalOnProperty(name = "app.ai.provider", havingValue = "gemini", matchIfMissing = true)
public class GeminiAiClient implements AiClient {

    private final RestClient restClient;
    private final String apiKey;
    private final String model;

    public GeminiAiClient(
            RestClient.Builder builder,
            @Value("${app.ai.base-url}") String baseUrl,
            @Value("${app.ai.api-key:}") String apiKey,
            @Value("${app.ai.model}") String model) {
        this.restClient = builder.baseUrl(baseUrl).build();
        this.apiKey = apiKey;
        this.model = model;
    }

    @Override
    public String generate(String systemPrompt, String userPrompt) {
        requireConfiguration();
        Map<String, Object> body = Map.of(
                "system_instruction", Map.of(
                        "parts", List.of(Map.of("text", systemPrompt))),
                "contents", List.of(Map.of(
                        "role", "user",
                        "parts", List.of(Map.of("text", userPrompt)))),
                "generationConfig", Map.of(
                        "temperature", 0.2,
                        "maxOutputTokens", 1200));

        try {
            GeminiResponse response = restClient.post()
                    .uri(uri -> uri
                            .path("/models/{model}:generateContent")
                            .queryParam("key", apiKey)
                            .build(model))
                    .body(body)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, upstreamResponse) -> {
                        throw new ExternalServiceException("AI provider request failed");
                    })
                    .body(GeminiResponse.class);
            return extractText(response);
        } catch (ExternalServiceException ex) {
            throw ex;
        } catch (RestClientException ex) {
            throw new ExternalServiceException("Unable to reach AI provider", ex);
        }
    }

    private String extractText(GeminiResponse response) {
        if (response == null || response.candidates() == null) {
            throw new ExternalServiceException("AI provider returned no result");
        }
        return response.candidates().stream()
                .filter(candidate -> candidate.content() != null
                        && candidate.content().parts() != null)
                .flatMap(candidate -> candidate.content().parts().stream())
                .map(Part::text)
                .filter(StringUtils::hasText)
                .findFirst()
                .orElseThrow(() -> new ExternalServiceException(
                        "AI provider returned no usable result"));
    }

    private void requireConfiguration() {
        if (!StringUtils.hasText(apiKey) || !StringUtils.hasText(model)) {
            throw new ExternalServiceException("AI provider is not configured");
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record GeminiResponse(List<Candidate> candidates) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record Candidate(Content content) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record Content(List<Part> parts) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record Part(String text) {
    }
}
