package com.davidmoura.secureauth.controller;

import com.davidmoura.secureauth.dto.MeResponse;
import com.davidmoura.secureauth.security.UserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MeController {

    @GetMapping("/api/v1/me")
    public MeResponse me(@AuthenticationPrincipal UserPrincipal principal) {
        return new MeResponse(
                principal.id(),
                principal.name(),
                principal.email(),
                principal.roles()
        );
    }
}
