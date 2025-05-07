package com.sro.SpringCoreTask1.service.impl;

import com.sro.SpringCoreTask1.service.TokenStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Profile("local")
public class InMemoryTokenStorageServiceImpl implements TokenStorageService {

    private final Map<String, Instant> blacklistedTokens = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> userRefreshTokens = new ConcurrentHashMap<>();

    @Value("${jwt.blacklist.prefix:blacklisted_token:}")
    private String blacklistKeyPrefix;

    @Value("${jwt.refresh.prefix:user:refresh_tokens:}")
    private String refreshTokensKeyPrefix;

    @Value("${jwt.refresh.expiry:30}")
    private int refreshTokenExpiryDays;

    @Override
    public void blacklistToken(String tokenId, Instant expiryDate) {
        if (tokenId == null || tokenId.isEmpty()) {
            return;
        }

        Duration timeToExpiry = Duration.between(Instant.now(), expiryDate);
        if (timeToExpiry.isNegative() || timeToExpiry.isZero()) {
            return;
        }

        String key = blacklistKeyPrefix + tokenId;
        blacklistedTokens.put(key, expiryDate);
    }

    @Override
    public boolean isTokenBlacklisted(String tokenId) {
        if (tokenId == null || tokenId.isEmpty()) {
            return false;
        }

        String key = blacklistKeyPrefix + tokenId;
        Instant expiryDate = blacklistedTokens.get(key);

        if (expiryDate == null) {
            return false;
        }

        if (expiryDate.isBefore(Instant.now())) {
            blacklistedTokens.remove(key);
            return false;
        }

        return true;
    }

    @Override
    public void storeRefreshToken(String username, String tokenId) {
        if (username == null || username.isEmpty() || tokenId == null || tokenId.isEmpty()) {
            return;
        }

        String key = refreshTokensKeyPrefix + username;
        userRefreshTokens.computeIfAbsent(key, k -> new HashSet<>()).add(tokenId);
    }

    @Override
    public Set<String> getUserRefreshTokens(String username) {
        if (username == null || username.isEmpty()) {
            return Collections.emptySet();
        }

        String key = refreshTokensKeyPrefix + username;
        Set<String> tokens = userRefreshTokens.get(key);
        return tokens != null ? new HashSet<>(tokens) : Collections.emptySet();
    }

    @Override
    public void removeRefreshToken(String username, String tokenId) {
        if (username == null || username.isEmpty() || tokenId == null || tokenId.isEmpty()) {
            return;
        }

        String key = refreshTokensKeyPrefix + username;
        Set<String> tokens = userRefreshTokens.get(key);
        if (tokens != null) {
            tokens.remove(tokenId);
        }
    }

    @Override
    public void clearUserRefreshTokens(String username) {
        if (username == null || username.isEmpty()) {
            return;
        }

        String key = refreshTokensKeyPrefix + username;
        userRefreshTokens.remove(key);
    }

    @Scheduled(fixedDelayString = "${jwt.blacklist.cleanup-interval:600000}")
    public void cleanupExpiredTokens() {
        Instant now = Instant.now();
        blacklistedTokens.entrySet().removeIf(entry -> entry.getValue().isBefore(now));
    }
}