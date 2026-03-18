package com.yuvaansh.fitness_tracker_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Provides beans used across the application.
 *
 * Right now we only need one security-related bean: a PasswordEncoder.
 * This lets us hash passwords on registration and verify them on login
 * without storing plaintext passwords in the database.
 */
@Configuration
public class SecurityBeans {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

