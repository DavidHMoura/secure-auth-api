package com.davidmoura.secureauth.dto;

public record RefreshResponse(String accessToken, String refreshToken, String tokenType) {}
