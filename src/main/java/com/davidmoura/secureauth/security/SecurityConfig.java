package com.davidmoura.secureauth.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwt;
    private final RestAuthenticationEntryPoint entryPoint;
    private final RestAccessDeniedHandler deniedHandler;

    public SecurityConfig(
            JwtAuthenticationFilter jwt,
            RestAuthenticationEntryPoint entryPoint,
            RestAccessDeniedHandler deniedHandler
    ) {
        this.jwt = jwt;
        this.entryPoint = entryPoint;
        this.deniedHandler = deniedHandler;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .requestCache(cache -> cache.disable())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(entryPoint)   // ✅ 401 em JSON
                        .accessDeniedHandler(deniedHandler)      // ✅ 403 em JSON
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/health", "/internal/health").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/users").permitAll()
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(b -> b.disable())
                .formLogin(f -> f.disable())
                .addFilterBefore(jwt, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
