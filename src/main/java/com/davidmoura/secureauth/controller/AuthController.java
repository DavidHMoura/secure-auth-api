package com.davidmoura.secureauth.controller;

import com.davidmoura.secureauth.dto.LoginRequest;
import com.davidmoura.secureauth.dto.LoginResponse;
import com.davidmoura.secureauth.dto.RefreshRequest;
import com.davidmoura.secureauth.dto.RefreshResponse;
import com.davidmoura.secureauth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req, HttpServletRequest http) {
        return authService.login(req, resolveIp(http));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok("Email successfully verified. You can now log in.");
    }

    @PostMapping("/refresh")
    public RefreshResponse refresh(@Valid @RequestBody RefreshRequest req, HttpServletRequest http) {
        return authService.refresh(req, resolveIp(http));
    }

    @PostMapping("/logout")
    public void logout(@Valid @RequestBody RefreshRequest req, HttpServletRequest http) {
        authService.logout(req, resolveIp(http));
    }

    private String resolveIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}