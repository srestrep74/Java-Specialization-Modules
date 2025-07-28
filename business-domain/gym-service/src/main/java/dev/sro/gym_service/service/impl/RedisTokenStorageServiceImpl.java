package dev.sro.gym_service.service.impl;

import dev.sro.gym_service.config.properties.JwtProperties;
import dev.sro.gym_service.service.TokenStorageService;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@Profile("!local")
public class RedisTokenStorageServiceImpl implements TokenStorageService {

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtProperties jwtProperties;

    public RedisTokenStorageServiceImpl(RedisTemplate<String, String> redisTemplate, JwtProperties jwtProperties) {
        this.redisTemplate = redisTemplate;
        this.jwtProperties = jwtProperties;
    }

    @Override
    public void blacklistToken(String tokenId, Instant expiryDate) {
        if (tokenId == null || tokenId.isEmpty()) {
            return;
        }

        String key = jwtProperties.blacklistPrefix() + tokenId;

        Duration timeToExpiry = Duration.between(Instant.now(), expiryDate);
        if (timeToExpiry.isNegative() || timeToExpiry.isZero()) {
            return;
        }

        redisTemplate.opsForValue().set(key, "1", timeToExpiry.toMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean isTokenBlacklisted(String tokenId) {
        if (tokenId == null || tokenId.isEmpty()) {
            return false;
        }

        String key = jwtProperties.blacklistPrefix() + tokenId;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    @Override
    public void storeRefreshToken(String username, String tokenId) {
        if (username == null || username.isEmpty() || tokenId == null || tokenId.isEmpty()) {
            return;
        }
        
        String key = jwtProperties.refreshPrefix() + username;
        redisTemplate.opsForSet().add(key, tokenId);
        
        if (redisTemplate.getExpire(key) < 0) {
            redisTemplate.expire(key, jwtProperties.refreshExpiry(), TimeUnit.DAYS);
        }
    }

    @Override
    public Set<String> getUserRefreshTokens(String username) {
        if (username == null || username.isEmpty()) {
            return Collections.emptySet();
        }
        
        String key = jwtProperties.refreshPrefix() + username;
        Set<String> tokens = redisTemplate.opsForSet().members(key);
        return tokens != null ? tokens : Collections.emptySet();
    }

    @Override
    public void removeRefreshToken(String username, String tokenId) {
        if (username == null || username.isEmpty() || tokenId == null || tokenId.isEmpty()) {
            return;
        }
        
        String key = jwtProperties.refreshPrefix() + username;
        redisTemplate.opsForSet().remove(key, tokenId);
    }

    @Override
    public void clearUserRefreshTokens(String username) {
        if (username == null || username.isEmpty()) {
            return;
        }
        
        String key = jwtProperties.refreshPrefix() + username;
        redisTemplate.delete(key);
    }
} 