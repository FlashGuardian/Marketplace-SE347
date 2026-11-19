package com.marketplace.auth.service;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RevokedTokenService {

    private final Map<String, Long> revokedUntil = new ConcurrentHashMap<>();

    public void revoke(String tokenId, Date expiration) {
        if (tokenId != null && expiration != null) {
            revokedUntil.put(tokenId, expiration.getTime());
        }
    }

    public boolean isRevoked(String tokenId) {
        if (tokenId == null) {
            return false;
        }

        Long expiresAt = revokedUntil.get(tokenId);
        if (expiresAt == null) {
            return false;
        }
        if (expiresAt <= System.currentTimeMillis()) {
            revokedUntil.remove(tokenId);
            return false;
        }
        return true;
    }
}
