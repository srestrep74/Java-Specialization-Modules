package dev.sro.workload_service.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.jms")
public record JmsRedeliveryProperties(
    int maxRedeliveries,
    long initialRedeliveryDelay,
    long redeliveryDelayMax
) {
    public JmsRedeliveryProperties {
        if (maxRedeliveries <= 0) {
            throw new IllegalArgumentException("maxRedeliveries must be positive");
        }
        if (initialRedeliveryDelay < 0) {
            throw new IllegalArgumentException("initialRedeliveryDelay must be non-negative");
        }
        if (redeliveryDelayMax < 0) {
            throw new IllegalArgumentException("redeliveryDelayMax must be non-negative");
        }
    }
} 