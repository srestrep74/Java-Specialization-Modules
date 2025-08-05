package dev.sro.workload_service.cucumber.component;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Mockito.mock;

@CucumberContextConfiguration
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.cloud.discovery.enabled=false",
        "eureka.client.enabled=false",
        "spring.cloud.config.enabled=false",
        "spring.cloud.config.discovery.enabled=false",
        "spring.cloud.config.retry.enabled=false",
        "spring.cloud.config.fail-fast=false"
    }
)
@EnableAutoConfiguration(exclude = {EurekaClientAutoConfiguration.class})
@ActiveProfiles("test")
public class CucumberComponentTestConfig {

    @TestConfiguration
    static class ComponentTestConfig {
        
        // Disable Eureka client for component tests
        @Bean
        @Primary
        public org.springframework.cloud.client.discovery.DiscoveryClient discoveryClient() {
            return new org.springframework.cloud.client.discovery.DiscoveryClient() {
                @Override
                public String description() {
                    return "Mock Discovery Client for Tests";
                }
                
                @Override
                public java.util.List<org.springframework.cloud.client.ServiceInstance> getInstances(String serviceId) {
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