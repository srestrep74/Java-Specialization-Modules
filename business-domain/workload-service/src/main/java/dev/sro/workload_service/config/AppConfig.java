package dev.sro.workload_service.config;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableDiscoveryClient
@EnableJpaRepositories(basePackages = "dev.sro.workload_service.repository")
public class AppConfig {

}
