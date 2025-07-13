package dev.sro.gym_service.client.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;

public class ServiceTokenInterceptor implements RequestInterceptor {

    private final String serviceToken;

    public ServiceTokenInterceptor(String serviceToken) {
        this.serviceToken = serviceToken;
    }

    @Override
    public void apply(RequestTemplate template) {
        if (serviceToken != null && !serviceToken.isEmpty()) {
            template.header("Authorization", "Bearer " + serviceToken);
        }
    }
}