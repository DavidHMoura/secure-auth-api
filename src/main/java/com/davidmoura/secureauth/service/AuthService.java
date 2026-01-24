package com.davidmoura.secureauth.service;

import com.davidmoura.secureauth.domain.Role;
import com.davidmoura.secureauth.domain.User;
import com.davidmoura.secureauth.dto.LoginRequest;
import com.davidmoura.secureauth.dto.LoginResponse;
import com.davidmoura.secureauth.repository.UserRepository;
import com.davidmoura.secureauth.security.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final TokenService tokenService;

    public AuthService(UserRepository users, PasswordEncoder encoder, TokenService tokenService) {
        this.users = users;
        this.encoder = encoder;
        this.tokenService = tokenService;
    }

    public LoginResponse login(LoginRequest req) {
        User user = users.findByEmail(req.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!encoder.matches(req.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        // ✅ roles vêm do banco
        Set<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        String token = tokenService.generateToken(user.getId(), user.getEmail(), roles);
        return new LoginResponse(token, "Bearer");
    }
}
