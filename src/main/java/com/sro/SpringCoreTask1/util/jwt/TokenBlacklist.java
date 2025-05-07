package com.sro.SpringCoreTask1.util.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Component
public class TokenBlacklist {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.blacklist.prefix:blacklisted_token:}")
    private String keyPrefix;

    public TokenBlacklist(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void blacklistToken(String tokenId, Instant expiryDate) {
        if (tokenId == null || tokenId.isEmpty()) {
            return;
        }

        String key = keyPrefix + tokenId;

        Duration timeToExpiry = Duration.between(Instant.now(), expiryDate);
        if (timeToExpiry.isNegative() || timeToExpiry.isZero()) {
            return;
        }

        redisTemplate.opsForValue().set(key, "1", timeToExpiry.toMillis(), TimeUnit.MILLISECONDS);
    }

    public boolean isBlacklisted(String tokenId) {
        if (tokenId == null || tokenId.isEmpty()) {
            return false;
        }

        String key = keyPrefix + tokenId;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}