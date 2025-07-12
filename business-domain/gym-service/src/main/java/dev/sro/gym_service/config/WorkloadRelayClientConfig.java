package dev.sro.gym_service.config;

import dev.sro.gym_service.client.ServiceTokenInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class WorkloadRelayClientConfig {

    @Bean
    public ServiceTokenInterceptor serviceTokenInterceptor(@Value("${internal.service.token}") String serviceToken) {
        return new ServiceTokenInterceptor(serviceToken);
    }
} 