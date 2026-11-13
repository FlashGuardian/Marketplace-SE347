package com.marketplace.auth.model;

public record LoginResponse(
        Long userId,
        String email,
        String displayName,
        String message
) {
}
