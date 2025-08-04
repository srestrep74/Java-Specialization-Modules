package dev.sro.gym_service.cucumber.component.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.TestPropertySource;

@TestConfiguration
@TestPropertySource(properties = {
    "spring.cloud.discovery.enabled=false",
    "eureka.client.enabled=false",
    "spring.cloud.config.enabled=false",
    "spring.cloud.config.discovery.enabled=false",
    "spring.cloud.config.retry.enabled=false",
    "spring.cloud.config.fail-fast=false",
    "spring.cloud.config.request-read-timeout=5000",
    "spring.cloud.config.connect-timeout=5000",
    "spring.cloud.config.enabled=false",
    "spring.cloud.config.discovery.enabled=false",
    "spring.cloud.config.retry.enabled=false",
    "spring.cloud.config.fail-fast=false",
    "spring.cloud.config.request-read-timeout=5000",
    "spring.cloud.config.connect-timeout=5000"
})
public class TestConfig {
    
    // This configuration ensures that external dependencies are disabled
    // and the application can run in isolation for component tests
} 