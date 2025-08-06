package dev.sro.gym_service.cucumber.component.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties(prefix = "test")
public record TestProperties(
    Jwt jwt
) {
    
    public record Jwt(
        String secret,
        Long expiration,
        Long refreshExpiration,
        Blacklist blacklist,
        Refresh refresh
    ) {
        @ConstructorBinding
        public Jwt {
        }
    }
    
    public record Blacklist(
        String prefix,
        String cleanupInterval
    ) {
        @ConstructorBinding
        public Blacklist {
        }
    }
    
    public record Refresh(
        String prefix,
        Integer expiry
    ) {
        @ConstructorBinding
        public Refresh {
        }
    }
    
    @ConstructorBinding
    public TestProperties {
    }
}
