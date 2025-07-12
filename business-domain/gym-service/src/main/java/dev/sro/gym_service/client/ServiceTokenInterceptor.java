package dev.sro.gym_service.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;

public class ServiceTokenInterceptor implements RequestInterceptor {

    private final String serviceToken;

    public ServiceTokenInterceptor(@Value("${internal.service.token}") String serviceToken) {
        this.serviceToken = serviceToken;
    }

    @Override
    public void apply(RequestTemplate template) {
        if (serviceToken != null && !serviceToken.isEmpty()) {
            template.header("Authorization", "Bearer " + serviceToken);
        }
    }
} 