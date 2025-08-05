package dev.sro.workload_service.cucumber.component;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;

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