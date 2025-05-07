package com.sro.SpringCoreTask1.service.impl;

import com.sro.SpringCoreTask1.service.TokenStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class RedisTokenStorageServiceImpl implements TokenStorageService {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.blacklist.prefix:blacklisted_token:}")
    private String blacklistKeyPrefix;
    
    @Value("${jwt.refresh.prefix:user:refresh_tokens:}")
    private String refreshTokensKeyPrefix;
    
    @Value("${jwt.refresh.expiry:30}")
    private int refreshTokenExpiryDays;

    public RedisTokenStorageServiceImpl(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void blacklistToken(String tokenId, Instant expiryDate) {
        if (tokenId == null || tokenId.isEmpty()) {
            return;
        }

        String key = blacklistKeyPrefix + tokenId;

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

        String key = blacklistKeyPrefix + tokenId;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    @Override
    public void storeRefreshToken(String username, String tokenId) {
        if (username == null || username.isEmpty() || tokenId == null || tokenId.isEmpty()) {
            return;
        }
        
        String key = refreshTokensKeyPrefix + username;
        redisTemplate.opsForSet().add(key, tokenId);
        
        if (redisTemplate.getExpire(key) < 0) {
            redisTemplate.expire(key, refreshTokenExpiryDays, TimeUnit.DAYS);
        }
    }

    @Override
    public Set<String> getUserRefreshTokens(String username) {
        if (username == null || username.isEmpty()) {
            return Collections.emptySet();
        }
        
        String key = refreshTokensKeyPrefix + username;
        Set<String> tokens = redisTemplate.opsForSet().members(key);
        return tokens != null ? tokens : Collections.emptySet();
    }

    @Override
    public void removeRefreshToken(String username, String tokenId) {
        if (username == null || username.isEmpty() || tokenId == null || tokenId.isEmpty()) {
            return;
        }
        
        String key = refreshTokensKeyPrefix + username;
        redisTemplate.opsForSet().remove(key, tokenId);
    }

    @Override
    public void clearUserRefreshTokens(String username) {
        if (username == null || username.isEmpty()) {
            return;
        }
        
        String key = refreshTokensKeyPrefix + username;
        redisTemplate.delete(key);
    }
} 