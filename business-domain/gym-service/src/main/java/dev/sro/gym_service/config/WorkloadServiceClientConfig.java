package dev.sro.gym_service.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;

import dev.sro.gym_service.client.interceptor.FeignJwtInterceptor;

public class WorkloadServiceClientConfig {

    @Bean
    public RequestInterceptor feignJwtInterceptor() {
        return new FeignJwtInterceptor();
    }
} 