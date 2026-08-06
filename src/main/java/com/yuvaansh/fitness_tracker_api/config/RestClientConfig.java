package com.yuvaansh.fitness_tracker_api.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.time.Clock;

/**
 * Provides a {@link RestClient.Builder} bean for outbound HTTP clients (e.g.
 * {@link com.yuvaansh.fitness_tracker_api.client.UsdaFoodClient}). Declared
 * explicitly so the app does not rely on optional auto-configuration.
 */
@Configuration
public class RestClientConfig {

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    @ConditionalOnMissingBean(Clock.class)
    public Clock clock() {
        return Clock.systemUTC();
    }
}
