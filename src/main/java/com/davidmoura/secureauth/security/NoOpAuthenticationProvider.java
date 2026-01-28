package com.davidmoura.secureauth.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * This API authenticates via JWT filter and manual login flow.
 * We declare a provider to prevent Spring Boot from auto-configuring
 * an in-memory UserDetailsService.
 */
@Component
public class NoOpAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        return null; // returning null lets other providers try (we have none)
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return false;
    }
}
