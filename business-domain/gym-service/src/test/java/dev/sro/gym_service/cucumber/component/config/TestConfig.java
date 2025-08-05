package dev.sro.gym_service.cucumber.component.config;

import org.springframework.boot.test.context.TestConfiguration;
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
}