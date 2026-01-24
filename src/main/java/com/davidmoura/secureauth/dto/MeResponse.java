package com.davidmoura.secureauth.dto;

import java.util.Set;
import java.util.UUID;

public record MeResponse(
        UUID id,
        String name,
        String email,
        Set<String> roles
) {}
