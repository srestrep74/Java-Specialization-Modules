package dev.sro.gym_service.cucumber.integration.config;

import dev.sro.gym_service.config.properties.JwtProperties;
import dev.sro.gym_service.service.impl.InMemoryTokenStorageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class IntegrationTestConfig {

    @Bean
    @Primary
    public JwtProperties jwtProperties() {
        return new JwtProperties(
                "5JI1p09GOcOlK9z8A/QBiLM7P+ZzS7DBvzIKM5G6Md2jYMkSvCbdQR13nPhJGwKkXZvRK9lNCPUXX/bSA44qzw==",
                120000L,
                604800000L,
                new JwtProperties.BlacklistProperties("blacklisted_token:", "60000"),
                new JwtProperties.RefreshProperties("user:refresh_tokens:", 30));
    }

    @Bean
    @Primary
    public InMemoryTokenStorageServiceImpl tokenStorageService(JwtProperties jwtProperties) {
        return new InMemoryTokenStorageServiceImpl(jwtProperties);
    }

    @Bean
    @Primary
    public dev.sro.gym_service.service.WorkloadRelayService workloadRelayService() {
        return mock(dev.sro.gym_service.service.WorkloadRelayService.class);
    }

    @Bean
    public ActiveMQFailureSimulator activeMQFailureSimulator(@Autowired ActiveMQTestcontainersConfig activeMQConfig) {
        return new ActiveMQFailureSimulator(activeMQConfig);
    }
} 