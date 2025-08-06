package dev.sro.gym_service.cucumber.integration.config;

import dev.sro.gym_service.config.properties.JwtProperties;
import dev.sro.gym_service.cucumber.integration.config.properties.TestProperties;
import dev.sro.gym_service.service.WorkloadRelayService;
import dev.sro.gym_service.service.impl.InMemoryTokenStorageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.mock;

@TestConfiguration
@EnableConfigurationProperties(TestProperties.class)
public class IntegrationTestConfig {

    @Bean
    @Primary
    public JwtProperties jwtProperties(TestProperties testProperties) {
        return new JwtProperties(
                testProperties.jwt().secret(),
                testProperties.jwt().expiration(),
                testProperties.jwt().refreshExpiration(),
                new JwtProperties.BlacklistProperties(
                    testProperties.jwt().blacklist().prefix(),
                    testProperties.jwt().blacklist().cleanupInterval()
                ),
                new JwtProperties.RefreshProperties(
                    testProperties.jwt().refresh().prefix(),
                    testProperties.jwt().refresh().expiry()
                ));
    }

    @Bean
    @Primary
    public InMemoryTokenStorageServiceImpl tokenStorageService(JwtProperties jwtProperties) {
        return new InMemoryTokenStorageServiceImpl(jwtProperties);
    }

    @Bean
    @Primary
    public WorkloadRelayService workloadRelayService() {
        return mock(WorkloadRelayService.class);
    }

    @Bean
    public ActiveMQFailureSimulator activeMQFailureSimulator(@Autowired ActiveMQTestcontainersConfig activeMQConfig) {
        return new ActiveMQFailureSimulator(activeMQConfig);
    }
} 