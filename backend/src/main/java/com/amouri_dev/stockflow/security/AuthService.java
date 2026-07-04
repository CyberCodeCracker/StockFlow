package com.amouri_dev.stockflow.security;

import com.amouri_dev.stockflow.common.User;
import com.amouri_dev.stockflow.security.dto.AuthResponse;
import com.amouri_dev.stockflow.security.dto.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${security.jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    @Transactional
    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication;
        try {
            authentication = this.authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));
        } catch (AuthenticationException ex) {
            // Covers bad credentials, disabled (PENDING) accounts and locked accounts.
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials or inactive account");
        }

        AppUserDetails principal = (AppUserDetails) authentication.getPrincipal();
        String accessToken = this.jwtService.generateAccessToken(principal);
        String refreshToken = issueRefreshToken(principal.getUser());
        return AuthResponse.bearer(accessToken, refreshToken);
    }

    @Transactional
    public AuthResponse refresh(String refreshToken) {
        RefreshToken stored = this.refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));

        if (stored.isRevoked() || stored.getExpires().isBefore(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expired or revoked");
        }

        // Rotate: revoke the used token and issue a fresh pair.
        stored.setRevoked(true);
        User user = stored.getUser();
        String accessToken = this.jwtService.generateAccessToken(new AppUserDetails(user));
        String newRefreshToken = issueRefreshToken(user);
        return AuthResponse.bearer(accessToken, newRefreshToken);
    }

    @Transactional
    public void logout(String refreshToken) {
        // Access tokens are stateless; revoking the refresh token prevents further renewal.
        // Idempotent: an unknown/already-revoked token is a no-op.
        this.refreshTokenRepository.findByToken(refreshToken)
                .ifPresent(token -> token.setRevoked(true));
    }

    private String issueRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUser(user);
        refreshToken.setExpires(Instant.now().plusMillis(this.refreshExpirationMs));
        refreshToken.setRevoked(false);
        return this.refreshTokenRepository.save(refreshToken).getToken();
    }
}
