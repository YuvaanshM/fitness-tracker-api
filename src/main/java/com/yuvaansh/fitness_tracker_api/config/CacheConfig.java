package com.yuvaansh.fitness_tracker_api.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * Enables Spring's caching (Caffeine, see {@code spring.cache.*}). Kept separate
 * from the main application class so JPA/web test slices, which do not provide a
 * CacheManager, are not forced to satisfy {@code @EnableCaching}.
 */
@Configuration
@EnableCaching
public class CacheConfig {
}
