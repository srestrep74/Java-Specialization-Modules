package dev.sro.gym_service.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        String secret,
        long expiration,
        long refreshExpiration,
        @NestedConfigurationProperty BlacklistProperties blacklist,
        @NestedConfigurationProperty RefreshProperties refresh) {
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
        if (blacklist == null) {
            throw new IllegalArgumentException("blacklist cannot be null");
        }
        if (refresh == null) {
            throw new IllegalArgumentException("refresh cannot be null");
        }
    }

    public record BlacklistProperties(
            String prefix,
            String cleanupInterval) {
        public BlacklistProperties {
            if (prefix == null || prefix.trim().isEmpty()) {
                throw new IllegalArgumentException("blacklist prefix cannot be null or empty");
            }
            if (cleanupInterval == null || cleanupInterval.trim().isEmpty()) {
                throw new IllegalArgumentException("blacklist cleanup interval cannot be null or empty");
            }
        }
    }

    public record RefreshProperties(
            String prefix,
            int expiry) {
        public RefreshProperties {
            if (prefix == null || prefix.trim().isEmpty()) {
                throw new IllegalArgumentException("refresh prefix cannot be null or empty");
            }
            if (expiry <= 0) {
                throw new IllegalArgumentException("refresh expiry must be positive");
            }
        }
    }
}