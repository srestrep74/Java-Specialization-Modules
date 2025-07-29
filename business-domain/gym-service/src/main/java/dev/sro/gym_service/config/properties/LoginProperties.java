package dev.sro.gym_service.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.login")
public record LoginProperties(
    int maxAttempts,
    int lockTimeMinutes
) {
    public LoginProperties {
        if (maxAttempts <= 0) {
            throw new IllegalArgumentException("maxAttempts must be positive");
        }
        if (lockTimeMinutes <= 0) {
            throw new IllegalArgumentException("lockTimeMinutes must be positive");
        }
    }
}