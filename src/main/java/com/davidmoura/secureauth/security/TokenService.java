package com.davidmoura.secureauth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Service
public class TokenService {

    private final SecretKey key;
    private final long accessExpSeconds;
    private final long refreshExpSeconds;
    private final String issuer;

    public TokenService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.access-expiration-seconds:900}") long accessExpSeconds,
            @Value("${security.jwt.refresh-expiration-seconds:604800}") long refreshExpSeconds,
            @Value("${security.jwt.issuer:secure-auth-api}") String issuer
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpSeconds = accessExpSeconds;
        this.refreshExpSeconds = refreshExpSeconds;
        this.issuer = issuer;
    }

    public String generateAccessToken(UUID userId, String email, Set<String> roles) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(accessExpSeconds);

        return Jwts.builder()
                .header().type("JWT").and()
                .id(UUID.randomUUID().toString())
                .issuer(issuer)
                .subject(userId.toString())
                .claim("email", email)
                .claim("roles", roles)
                .claim("typ", "access")
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(UUID userId) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(refreshExpSeconds);

        return Jwts.builder()
                .header().type("JWT").and()
                .id(UUID.randomUUID().toString())
                .issuer(issuer)
                .subject(userId.toString())
                .claim("typ", "refresh")
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(key)
                .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
