package com.yuvaansh.fitness_tracker_api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuvaansh.fitness_tracker_api.dto.AuthResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Clock;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AuthRateLimitingFilter extends OncePerRequestFilter {

    private static final int MAX_ATTEMPTS = 10;
    private static final long WINDOW_MILLIS = 60_000;

    private final Map<String, RateLimitWindow> attempts = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Clock clock;

    public AuthRateLimitingFilter() {
        this(Clock.systemUTC());
    }

    AuthRateLimitingFilter(Clock clock) {
        this.clock = clock;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        if (!isLimitedAuthRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String key = clientKey(request);
        long now = clock.millis();
        RateLimitWindow window = attempts.compute(key, (ignored, existing) -> {
            if (existing == null || now - existing.windowStartMillis >= WINDOW_MILLIS) {
                return new RateLimitWindow(now, 1);
            }
            return new RateLimitWindow(existing.windowStartMillis, existing.count + 1);
        });

        if (window.count > MAX_ATTEMPTS) {
            response.setStatus(429);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(
                    response.getOutputStream(),
                    new AuthResponse("Too many authentication attempts. Please try again later.", null, null, null)
            );
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isLimitedAuthRequest(HttpServletRequest request) {
        return "POST".equalsIgnoreCase(request.getMethod())
                && ("/api/auth/login".equals(request.getRequestURI())
                || "/api/auth/register".equals(request.getRequestURI()));
    }

    private String clientKey(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private record RateLimitWindow(long windowStartMillis, int count) {
    }
}
