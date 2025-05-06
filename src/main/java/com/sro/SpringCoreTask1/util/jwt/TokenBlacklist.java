package com.sro.SpringCoreTask1.util.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenBlacklist {

    private final Map<String, Instant> blacklistedTokens = new ConcurrentHashMap<>();

    @Value("${jwt.blacklist.cleanup-interval:600000}")
    private long cleanupInterval;

    public void blacklistToken(String tokenId, Instant expiryDate) {
        if (tokenId == null || tokenId.isEmpty()) {
            return;
        }

        blacklistedTokens.put(tokenId, expiryDate);
    }

    public boolean isBlacklisted(String tokenId) {
        if (tokenId == null || tokenId.isEmpty()) {
            return false;
        }

        return blacklistedTokens.containsKey(tokenId);
    }

    @Scheduled(fixedRateString = "${jwt.blacklist.cleanup-interval:600000}")
    public void cleanupExpiredTokens() {
        Instant now = Instant.now();
        blacklistedTokens.entrySet().removeIf(entry -> entry.getValue().isBefore(now));
    }
}