package dev.sro.workload_service.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        String secret) {
    public JwtProperties {
        if (secret == null || secret.trim().isEmpty()) {
            throw new IllegalArgumentException("secret cannot be null or empty");
        }
    }
}