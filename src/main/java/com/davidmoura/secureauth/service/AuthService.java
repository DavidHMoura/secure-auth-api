package com.davidmoura.secureauth.service;

import com.davidmoura.secureauth.domain.AuditEventType;
import com.davidmoura.secureauth.domain.RefreshToken;
import com.davidmoura.secureauth.domain.Role;
import com.davidmoura.secureauth.domain.User;
import com.davidmoura.secureauth.dto.LoginRequest;
import com.davidmoura.secureauth.dto.LoginResponse;
import com.davidmoura.secureauth.dto.RefreshRequest;
import com.davidmoura.secureauth.dto.RefreshResponse;
import com.davidmoura.secureauth.repository.RefreshTokenRepository;
import com.davidmoura.secureauth.repository.UserRepository;
import com.davidmoura.secureauth.security.TokenHash;
import com.davidmoura.secureauth.security.TokenService;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository users;
    private final RefreshTokenRepository refreshTokens;
    private final PasswordEncoder encoder;
    private final TokenService tokenService;
    private final ApplicationEventPublisher events;

    public AuthService(
            UserRepository users,
            RefreshTokenRepository refreshTokens,
            PasswordEncoder encoder,
            TokenService tokenService,
            ApplicationEventPublisher events
    ) {
        this.users = users;
        this.refreshTokens = refreshTokens;
        this.encoder = encoder;
        this.tokenService = tokenService;
        this.events = events;
    }

    @Transactional
    public LoginResponse login(LoginRequest req, String ipAddress) {
        User user = users.findByEmail(req.email()).orElse(null);

        if (user == null || !encoder.matches(req.password(), user.getPasswordHash())) {
            UUID userId = user != null ? user.getId() : null;
            publish(AuditEventType.LOGIN_FAILURE, userId, ipAddress, "email=" + req.email());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        if (!user.isVerified()) {
            publish(AuditEventType.LOGIN_FAILURE, user.getId(), ipAddress, "reason=unverified_email");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Email not verified. Please check your inbox.");
        }

        Set<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        String access  = tokenService.generateAccessToken(user.getId(), user.getEmail(), roles);
        String refresh = tokenService.generateRefreshToken(user.getId());

        String hash = TokenHash.sha256(refresh);
        Instant exp = tokenService.parseClaims(refresh).getExpiration().toInstant();
        refreshTokens.save(new RefreshToken(user.getId(), hash, exp));

        publish(AuditEventType.LOGIN_SUCCESS, user.getId(), ipAddress, "email=" + user.getEmail());

        return new LoginResponse(access, refresh, "Bearer");
    }

    @Transactional
    public void verifyEmail(String token) {
        User user = users.findByVerificationToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired verification token"));

        user.verify();
        users.save(user);

        log.info("Email verified successfully for user: {}", user.getEmail());
    }

    @Transactional
    public RefreshResponse refresh(RefreshRequest req, String ipAddress) {
        Claims claims = tokenService.parseClaims(req.refreshToken());

        String typ = String.valueOf(claims.get("typ"));
        if (!"refresh".equals(typ)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        UUID userId = UUID.fromString(claims.getSubject());
        String hash = TokenHash.sha256(req.refreshToken());

        RefreshToken stored = refreshTokens.findByTokenHash(hash)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));

        if (stored.isRevoked()) {
            revokeAllUserTokens(userId);
            publish(AuditEventType.TOKEN_REUSE_DETECTED, userId, ipAddress, "Possible token theft — all sessions revoked");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        if (stored.isExpired()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        stored.revoke();
        refreshTokens.save(stored);

        User user = users.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));

        Set<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        String newAccess  = tokenService.generateAccessToken(user.getId(), user.getEmail(), roles);
        String newRefresh = tokenService.generateRefreshToken(user.getId());

        String newHash = TokenHash.sha256(newRefresh);
        Instant newExp = tokenService.parseClaims(newRefresh).getExpiration().toInstant();
        refreshTokens.save(new RefreshToken(user.getId(), newHash, newExp));

        publish(AuditEventType.TOKEN_REFRESHED, userId, ipAddress, null);

        return new RefreshResponse(newAccess, newRefresh, "Bearer");
    }

    @Transactional
    public void logout(RefreshRequest req, String ipAddress) {
        try {
            Claims claims = tokenService.parseClaims(req.refreshToken());
            String typ = String.valueOf(claims.get("typ"));
            if (!"refresh".equals(typ)) return;

            UUID userId = UUID.fromString(claims.getSubject());
            String hash = TokenHash.sha256(req.refreshToken());

            refreshTokens.findByTokenHash(hash).ifPresent(rt -> {
                rt.revoke();
                refreshTokens.save(rt);
            });

            publish(AuditEventType.LOGOUT, userId, ipAddress, null);

        } catch (Exception ignored) {
        }
    }

    private void revokeAllUserTokens(UUID userId) {
        refreshTokens.findAllByUserId(userId).forEach(rt -> {
            if (!rt.isRevoked()) rt.revoke();
        });
        refreshTokens.flush();
    }

    private void publish(AuditEventType type, UUID userId, String ip, String detail) {
        events.publishEvent(new AuditService.AuthAuditEvent(type, userId, ip, detail));
    }
}