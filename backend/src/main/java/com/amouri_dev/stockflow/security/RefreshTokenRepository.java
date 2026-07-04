package com.amouri_dev.stockflow.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByToken(String token);

    /**
     * Removes tokens that are past their expiry (revoked or not). Revoked-but-still-valid
     * tokens are kept until natural expiry so token reuse can still be detected.
     */
    @Modifying
    @Query("DELETE FROM RefreshToken t WHERE t.expires < :now")
    int deleteExpiredTokens(@Param("now") Instant now);
}
