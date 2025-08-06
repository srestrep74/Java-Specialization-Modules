package dev.sro.gym_service.cucumber.integration.config;

import dev.sro.gym_service.config.properties.JwtProperties;
import dev.sro.gym_service.service.impl.InMemoryTokenStorageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class IntegrationTestConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @Value("${jwt.refresh-expiration}")
    private Long jwtRefreshExpiration;

    @Value("${jwt.blacklist.prefix}")
    private String blacklistPrefix;

    @Value("${jwt.blacklist.cleanup-interval}")
    private String cleanupInterval;

    @Value("${jwt.refresh.prefix}")
    private String refreshPrefix;

    @Value("${jwt.refresh.expiry}")
    private Integer refreshExpiry;

    @Bean
    @Primary
    public JwtProperties jwtProperties() {
        return new JwtProperties(
                jwtSecret,
                jwtExpiration,
                jwtRefreshExpiration,
                new JwtProperties.BlacklistProperties(blacklistPrefix, cleanupInterval),
                new JwtProperties.RefreshProperties(refreshPrefix, refreshExpiry));
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