package com.nexusbank.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexusbank.exception.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitingFilter extends OncePerRequestFilter {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${security.rate-limit.transfer-per-minute:10}")
    private int transferLimit;

    @Value("${security.rate-limit.login-per-minute:5}")
    private int loginLimit;

    private static final String RATE_LIMIT_PREFIX = "rate:";

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // ✅ MUITO IMPORTANTE — liberar preflight CORS
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String path = request.getRequestURI();
        String ip   = resolveClientIp(request);

        Integer limit = resolveLimit(path);

        if (limit == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String key   = RATE_LIMIT_PREFIX + ip + ":" + normalizePath(path);

        long count = incrementCounter(key);

        if (count > limit) {

            log.warn(
                    "Rate limit exceeded for IP={} on path={}",
                    ip,
                    path
            );

            sendTooManyRequestsError(
                    response,
                    request.getRequestURI()
            );

            return;
        }

        filterChain.doFilter(request, response);
    }

    // ─────────────────────────────

    private Integer resolveLimit(String path) {

        if (path.contains("/transactions/transfer")
                || path.contains("/transactions/pay")) {

            return transferLimit;
        }

        if (path.contains("/auth/login")) {

            return loginLimit;
        }

        return null;
    }

    private String normalizePath(String path) {

        return path.replaceAll(
                "/[0-9a-fA-F\\-]{36}",
                "/:id"
        );
    }

    private long incrementCounter(String key) {

        try {

            Long count =
                    redisTemplate
                            .opsForValue()
                            .increment(key);

            if (count != null && count == 1) {

                redisTemplate.expire(
                        key,
                        Duration.ofMinutes(1)
                );
            }

            return count != null ? count : 0;

        } catch (Exception e) {

            log.warn(
                    "Redis unavailable for rate limiting, allowing request: {}",
                    e.getMessage()
            );

            return 0;
        }
    }

    private String resolveClientIp(
            HttpServletRequest request
    ) {

        String xff =
                request.getHeader("X-Forwarded-For");

        if (xff != null && !xff.isBlank()) {

            return xff.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }

    private void sendTooManyRequestsError(
            HttpServletResponse response,
            String path
    ) throws IOException {

        response.setStatus(
                HttpStatus.TOO_MANY_REQUESTS.value()
        );

        response.setContentType(
                MediaType.APPLICATION_JSON_VALUE
        );

        response.setCharacterEncoding("UTF-8");

        ErrorResponse error =
                ErrorResponse.builder()
                        .timestamp(Instant.now())
                        .status(
                                HttpStatus.TOO_MANY_REQUESTS.value()
                        )
                        .error("Too Many Requests")
                        .message(
                                "Limite de requisições atingido."
                        )
                        .path(path)
                        .errorCode("RATE_LIMIT_EXCEEDED")
                        .build();

        objectMapper.writeValue(
                response.getWriter(),
                error
        );
    }
}