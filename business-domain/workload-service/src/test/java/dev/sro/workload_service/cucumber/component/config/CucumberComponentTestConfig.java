package dev.sro.workload_service.cucumber.component.config;

import java.util.Collections;
import java.util.List;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration(exclude = { EurekaClientAutoConfiguration.class })
@Import(TestMongoDBConfig.class)
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
                public List<ServiceInstance> getInstances(String serviceId) {
                    return Collections.emptyList();
                }

                @Override
                public List<String> getServices() {
                    return Collections.emptyList();
                }
            };
        }

    }
}