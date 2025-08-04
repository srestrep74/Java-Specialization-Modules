package dev.sro.gym_service.cucumber.component.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
@EnableAutoConfiguration(exclude = {
    EurekaClientAutoConfiguration.class
})
public class EurekaTestConfig {
    
    // This configuration excludes Eureka client auto-configuration
    // to prevent connection attempts during component tests
} 