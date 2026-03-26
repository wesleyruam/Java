package com.nexusbank.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class IdempotencyUtil {

    private static final String IDEMPOTENCY_PREFIX = "idempotency:";
    private static final Duration IDEMPOTENCY_TTL = Duration.ofHours(24);

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * Checks if the given key has already been processed.
     * If not, marks it as processed and returns true (proceed).
     * If yes, returns false (skip — duplicate request).
     */
    public boolean tryConsume(String idempotencyKey) {
        String redisKey = IDEMPOTENCY_PREFIX + idempotencyKey;
        Boolean wasAbsent = redisTemplate.opsForValue()
                .setIfAbsent(redisKey, "1", IDEMPOTENCY_TTL);
        return Boolean.TRUE.equals(wasAbsent);
    }

    public boolean isDuplicate(String idempotencyKey) {
        return !tryConsume(idempotencyKey);
    }
}
