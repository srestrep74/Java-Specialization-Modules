package dev.sro.workload_service.cucumber.component.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties(prefix = "test")
public record TestProperties(
    Auth auth,
    Mock mock
) {
    
    public record Auth(
        Boolean enabled,
        String internalToken
    ) {
        @ConstructorBinding
        public Auth {
        }
    }
    
    public record Mock(
        Boolean enabled
    ) {
        @ConstructorBinding
        public Mock {
        }
    }
    
    @ConstructorBinding
    public TestProperties {
    }
}
