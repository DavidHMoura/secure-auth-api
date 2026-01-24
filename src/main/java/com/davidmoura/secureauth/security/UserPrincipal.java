package com.davidmoura.secureauth.security;

import java.util.Set;
import java.util.UUID;

public record UserPrincipal(
        UUID id,
        String name,
        String email,
        Set<String> roles
) {}
