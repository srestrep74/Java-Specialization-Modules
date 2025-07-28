package dev.sro.gym_service.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

@ConfigurationProperties(prefix = "storage")
public record StorageProperties(
    Resource initFile
) {
    public StorageProperties {
        if (initFile == null) {
            throw new IllegalArgumentException("initFile cannot be null");
        }
    }
} 