package com.davidmoura.secureauth.controller;

import com.davidmoura.secureauth.dto.CreateUserRequest;
import com.davidmoura.secureauth.dto.ForgotPasswordRequest;
import com.davidmoura.secureauth.dto.LoginRequest;
import com.davidmoura.secureauth.dto.LoginResponse;
import com.davidmoura.secureauth.dto.RefreshRequest;
import com.davidmoura.secureauth.dto.RefreshResponse;
import com.davidmoura.secureauth.dto.ResetPasswordRequest;
import com.davidmoura.secureauth.dto.UserResponse;
import com.davidmoura.secureauth.service.AuthService;
import com.davidmoura.secureauth.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody CreateUserRequest req) {
        UserResponse response = userService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest req, HttpServletRequest http) {
        authService.forgotPassword(req.email(), resolveIp(http));
        return ResponseEntity.ok("If that email address is in our database, we will send you an email to reset your password.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest req, HttpServletRequest http) {
        authService.resetPassword(req.token(), req.newPassword(), resolveIp(http));
        return ResponseEntity.ok("Password has been successfully reset. You can now log in.");
    }

    private String resolveIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}