package com.marketplace.auth.model;

public record User(
        Long id,
        String email,
        String passwordHash,
        String displayName
) {
}
