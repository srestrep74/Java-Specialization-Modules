package dev.sro.gym_service.service.impl;

import dev.sro.gym_service.config.properties.JwtProperties;
import dev.sro.gym_service.service.TokenStorageService;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Profile({ "local", "test" })
public class InMemoryTokenStorageServiceImpl implements TokenStorageService {

    private final Map<String, Instant> blacklistedTokens = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> userRefreshTokens = new ConcurrentHashMap<>();
    private final JwtProperties jwtProperties;

    public InMemoryTokenStorageServiceImpl(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Override
    public void blacklistToken(String tokenId, Instant expiryDate) {
        if (tokenId == null || tokenId.isEmpty()) {
            return;
        }

        Duration timeToExpiry = Duration.between(Instant.now(), expiryDate);
        if (timeToExpiry.isNegative() || timeToExpiry.isZero()) {
            return;
        }

        String key = jwtProperties.blacklistPrefix() + tokenId;
        blacklistedTokens.put(key, expiryDate);
    }

    @Override
    public boolean isTokenBlacklisted(String tokenId) {
        if (tokenId == null || tokenId.isEmpty()) {
            return false;
        }

        String key = jwtProperties.blacklistPrefix() + tokenId;
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

        String key = jwtProperties.refreshPrefix() + username;
        userRefreshTokens.computeIfAbsent(key, k -> new HashSet<>()).add(tokenId);
    }

    @Override
    public Set<String> getUserRefreshTokens(String username) {
        if (username == null || username.isEmpty()) {
            return Collections.emptySet();
        }

        String key = jwtProperties.refreshPrefix() + username;
        Set<String> tokens = userRefreshTokens.get(key);
        return tokens != null ? new HashSet<>(tokens) : Collections.emptySet();
    }

    @Override
    public void removeRefreshToken(String username, String tokenId) {
        if (username == null || username.isEmpty() || tokenId == null || tokenId.isEmpty()) {
            return;
        }

        String key = jwtProperties.refreshPrefix() + username;
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

        String key = jwtProperties.refreshPrefix() + username;
        userRefreshTokens.remove(key);
    }

    @Scheduled(fixedDelayString = "#{@jwtProperties.blacklistCleanupInterval()}")
    public void cleanupExpiredTokens() {
        Instant now = Instant.now();
        blacklistedTokens.entrySet().removeIf(entry -> entry.getValue().isBefore(now));
    }
}