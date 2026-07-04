package com.amouri_dev.stockflow.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Periodically purges expired refresh tokens so the table stays bounded.
 * Schedule is configurable via {@code security.jwt.refresh-cleanup-cron} (default: daily at 03:00).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokenCleanupTask {

    private final RefreshTokenRepository refreshTokenRepository;

    @Scheduled(cron = "${security.jwt.refresh-cleanup-cron:0 0 3 * * *}")
    @Transactional
    public void purgeExpiredTokens() {
        int deleted = this.refreshTokenRepository.deleteExpiredTokens(Instant.now());
        if (deleted > 0) {
            log.info("Purged {} expired refresh token(s)", deleted);
        }
    }
}
