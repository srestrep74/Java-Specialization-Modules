package dev.sro.gym_service.cucumber.component.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration;

@TestConfiguration
@EnableAutoConfiguration(exclude = {
    EurekaClientAutoConfiguration.class
})
public class EurekaTestConfig {
} 