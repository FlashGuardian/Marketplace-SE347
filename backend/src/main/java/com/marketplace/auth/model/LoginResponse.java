package com.marketplace.auth.model;

public record LoginResponse(
        Long userId,
        String email,
        String displayName,
        String role,
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn,
        String message
) {
}
