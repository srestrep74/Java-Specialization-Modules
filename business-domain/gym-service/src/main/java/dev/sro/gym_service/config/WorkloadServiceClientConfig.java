package dev.sro.gym_service.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import dev.sro.gym_service.client.interceptor.FeignJwtInterceptor;

@Profile("!test")
public class WorkloadServiceClientConfig {

    @Bean
    public RequestInterceptor feignJwtInterceptor() {
        return new FeignJwtInterceptor();
    }
} 