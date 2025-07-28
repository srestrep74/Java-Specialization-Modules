package dev.sro.gym_service.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.activemq")
public record JmsProperties(
    String brokerUrl,
    String user,
    String password
) {
    public JmsProperties {
        if (brokerUrl == null || brokerUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("brokerUrl cannot be null or empty");
        }
        if (user == null || user.trim().isEmpty()) {
            throw new IllegalArgumentException("user cannot be null or empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("password cannot be null or empty");
        }
    }
} 