package com.marketplace.auth.model;

public record UserResponse(
        Long userId,
        String email,
        String displayName,
        String role
) {
}
