package com.davidmoura.secureauth.dto;

public record LoginResponse(String accessToken, String refreshToken, String tokenType) {}
