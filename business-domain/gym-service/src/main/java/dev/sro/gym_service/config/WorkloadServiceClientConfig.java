package dev.sro.gym_service.config;

import dev.sro.gym_service.client.FeignJwtInterceptor;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;

public class WorkloadServiceClientConfig {

    @Bean
    public RequestInterceptor feignJwtInterceptor() {
        return new FeignJwtInterceptor();
    }
} 