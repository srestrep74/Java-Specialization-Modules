package dev.sro.gym_service.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
    String secret,
    long expiration,
    long refreshExpiration,
    String blacklistPrefix,
    String refreshPrefix,
    int refreshExpiry,
    String blacklistCleanupInterval
) {
    public JwtProperties {
        if (secret == null || secret.trim().isEmpty()) {
            throw new IllegalArgumentException("secret cannot be null or empty");
        }
        if (expiration <= 0) {
            throw new IllegalArgumentException("expiration must be positive");
        }
        if (refreshExpiration <= 0) {
            throw new IllegalArgumentException("refreshExpiration must be positive");
        }
        if (blacklistPrefix == null || blacklistPrefix.trim().isEmpty()) {
            throw new IllegalArgumentException("blacklistPrefix cannot be null or empty");
        }
        if (refreshPrefix == null || refreshPrefix.trim().isEmpty()) {
            throw new IllegalArgumentException("refreshPrefix cannot be null or empty");
        }
        if (refreshExpiry <= 0) {
            throw new IllegalArgumentException("refreshExpiry must be positive");
        }
        if (blacklistCleanupInterval == null || blacklistCleanupInterval.trim().isEmpty()) {
            throw new IllegalArgumentException("blacklistCleanupInterval cannot be null or empty");
        }
    }
} 