package dev.sro.gym_service.cucumber.component;

import dev.sro.gym_service.config.properties.JwtProperties;
import dev.sro.gym_service.service.WorkloadNotificationService;
import dev.sro.gym_service.service.impl.auth.LoginAttemptService;
import dev.sro.gym_service.service.impl.InMemoryTokenStorageServiceImpl;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Mockito.mock;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.cloud.discovery.enabled=false",
        "eureka.client.enabled=false",
        "spring.cloud.config.enabled=false",
        "spring.cloud.config.discovery.enabled=false",
        "spring.cloud.config.retry.enabled=false",
        "spring.cloud.config.fail-fast=false"
})
@EnableAutoConfiguration(exclude = { EurekaClientAutoConfiguration.class })
@ActiveProfiles("test")
public class CucumberComponentTestConfig {

    @TestConfiguration
    static class ComponentTestConfig {

        @Bean
        @Primary
        public WorkloadNotificationService workloadNotificationService() {
            return mock(WorkloadNotificationService.class);
        }

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
        public LoginAttemptService loginAttemptService() {
            return mock(LoginAttemptService.class);
        }

        @Bean
        @Primary
        public DiscoveryClient discoveryClient() {
            return new DiscoveryClient() {
                @Override
                public String description() {
                    return "Mock Discovery Client for Tests";
                }

                @Override
                public java.util.List<ServiceInstance> getInstances(String serviceId) {
                    return java.util.Collections.emptyList();
                }

                @Override
                public java.util.List<String> getServices() {
                    return java.util.Collections.emptyList();
                }
            };
        }
    }
}