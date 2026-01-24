package com.davidmoura.secureauth.controller;

import com.davidmoura.secureauth.dto.LoginRequest;
import com.davidmoura.secureauth.dto.LoginResponse;
import com.davidmoura.secureauth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }
}
