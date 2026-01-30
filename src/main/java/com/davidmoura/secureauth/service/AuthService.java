package com.davidmoura.secureauth.service;

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
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository users;
    private final RefreshTokenRepository refreshTokens;
    private final PasswordEncoder encoder;
    private final TokenService tokenService;

    public AuthService(
            UserRepository users,
            RefreshTokenRepository refreshTokens,
            PasswordEncoder encoder,
            TokenService tokenService
    ) {
        this.users = users;
        this.refreshTokens = refreshTokens;
        this.encoder = encoder;
        this.tokenService = tokenService;
    }

    public LoginResponse login(LoginRequest req) {
        User user = users.findByEmail(req.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!encoder.matches(req.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        Set<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        String access = tokenService.generateAccessToken(user.getId(), user.getEmail(), roles);
        String refresh = tokenService.generateRefreshToken(user.getId());

        // store hashed refresh token
        String hash = TokenHash.sha256(refresh);

        Instant exp = tokenService.parseClaims(refresh).getExpiration().toInstant();
        refreshTokens.save(new RefreshToken(user.getId(), hash, exp));

        return new LoginResponse(access, refresh, "Bearer");
    }

    public RefreshResponse refresh(RefreshRequest req) {
        Claims claims = tokenService.parseClaims(req.refreshToken());

        String typ = String.valueOf(claims.get("typ"));
        if (!"refresh".equals(typ)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        UUID userId = UUID.fromString(claims.getSubject());

        String hash = TokenHash.sha256(req.refreshToken());

        RefreshToken stored = refreshTokens.findByTokenHash(hash)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));

        if (stored.isRevoked() || stored.isExpired()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        // rotation: revoke old refresh and issue a new one
        stored.revoke();
        refreshTokens.save(stored);

        User user = users.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));

        Set<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        String newAccess = tokenService.generateAccessToken(user.getId(), user.getEmail(), roles);
        String newRefresh = tokenService.generateRefreshToken(user.getId());

        String newHash = TokenHash.sha256(newRefresh);
        Instant newExp = tokenService.parseClaims(newRefresh).getExpiration().toInstant();

        refreshTokens.save(new RefreshToken(user.getId(), newHash, newExp));

        return new RefreshResponse(newAccess, newRefresh, "Bearer");
    }

    public void logout(RefreshRequest req) {
        String hash = TokenHash.sha256(req.refreshToken());
        refreshTokens.findByTokenHash(hash).ifPresent(rt -> {
            rt.revoke();
            refreshTokens.save(rt);
        });
    }
}
