package com.davidmoura.secureauth.dto;

public record LoginResponse(
        String token,
        String tokenType
) {}
