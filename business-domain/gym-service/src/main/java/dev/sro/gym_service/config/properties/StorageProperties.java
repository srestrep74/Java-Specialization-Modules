package dev.sro.gym_service.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.core.io.Resource;

@ConfigurationProperties(prefix = "storage")
public record StorageProperties(
    @NestedConfigurationProperty InitProperties init
) {
    public StorageProperties {
        if (init == null) {
            throw new IllegalArgumentException("init cannot be null");
        }
    }

    public record InitProperties(
        Resource file
    ) {
        public InitProperties {
            if (file == null) {
                throw new IllegalArgumentException("init file cannot be null");
            }
        }
    }
} 