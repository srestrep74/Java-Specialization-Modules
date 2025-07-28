package dev.sro.workload_service.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;

import dev.sro.workload_service.config.properties.JmsProperties;
import dev.sro.workload_service.config.properties.JmsRedeliveryProperties;

@Configuration
@EnableDiscoveryClient
@EnableConfigurationProperties({
    JmsProperties.class,
    JmsRedeliveryProperties.class
})
public class AppConfig {

}
