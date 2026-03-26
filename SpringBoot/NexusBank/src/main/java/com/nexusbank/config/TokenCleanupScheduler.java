package com.nexusbank.config;

import com.nexusbank.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Scheduled job that purges expired and revoked refresh tokens
 * from the database to prevent unbounded table growth.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TokenCleanupScheduler {

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * Runs every day at 03:00 AM UTC.
     */
    @Scheduled(cron = "0 0 3 * * *", zone = "UTC")
    @Transactional
    public void cleanupExpiredTokens() {
        log.info("Starting scheduled cleanup of expired/revoked refresh tokens...");
        try {
            refreshTokenRepository.deleteExpiredAndRevokedTokens(Instant.now());
            log.info("Token cleanup completed successfully.");
        } catch (Exception e) {
            log.error("Token cleanup failed: {}", e.getMessage(), e);
        }
    }
}
