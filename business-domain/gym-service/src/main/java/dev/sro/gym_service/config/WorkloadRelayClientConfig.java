package dev.sro.gym_service.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;

import dev.sro.gym_service.client.interceptor.ServiceTokenInterceptor;

import org.springframework.beans.factory.annotation.Value;


public class WorkloadRelayClientConfig {

    @Bean
    public RequestInterceptor serviceTokenInterceptor(@Value("${internal.service.token}") String serviceToken) {
        return new ServiceTokenInterceptor(serviceToken);
    }
} 