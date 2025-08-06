package dev.sro.gym_service.cucumber.component;

import dev.sro.gym_service.config.properties.JwtProperties;
import dev.sro.gym_service.cucumber.component.config.properties.TestProperties;
import dev.sro.gym_service.service.WorkloadNotificationService;
import dev.sro.gym_service.service.impl.auth.LoginAttemptService;
import dev.sro.gym_service.service.impl.InMemoryTokenStorageServiceImpl;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Mockito.mock;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration(exclude = { EurekaClientAutoConfiguration.class })
@EnableConfigurationProperties(TestProperties.class)
@ActiveProfiles("test")
@ComponentScan(
    basePackages = {
        "dev.sro.gym_service",
        "dev.sro.gym_service.cucumber.component"
    },
    excludeFilters = {
        @ComponentScan.Filter(
            type = FilterType.REGEX, 
            pattern = "dev\\.sro\\.gym_service\\.cucumber\\.integration\\..*"
        ),
        @ComponentScan.Filter(
            type = FilterType.REGEX, 
            pattern = ".*Test\\$.*"
        ),
        @ComponentScan.Filter(
            type = FilterType.REGEX, 
            pattern = ".*ControllerTest\\$.*"
        )
    }
)
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