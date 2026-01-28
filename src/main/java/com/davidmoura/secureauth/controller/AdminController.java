package com.davidmoura.secureauth.controller;

import com.davidmoura.secureauth.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/ping")
    public String ping() {
        return "pong (admin)";
    }

    @PostMapping("/grant")
    public void grantRole(
            @RequestParam String email,
            @RequestParam String role
    ) {
        userService.grantRole(email, role);
    }

    @PostMapping("/revoke")
    public void revokeRole(
            @RequestParam String email,
            @RequestParam String role
    ) {
        userService.revokeRole(email, role);
    }
}
