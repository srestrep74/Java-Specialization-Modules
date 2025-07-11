package dev.sro.gym_service.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Component
@Slf4j
public class FeignJwtInterceptor implements RequestInterceptor {

    private static final String REQUEST_ID_KEY = "requestId";
    private static final String REQUEST_ID_HEADER = "X-Request-ID";

    @Override
    public void apply(RequestTemplate requestTemplate) {
        // Propagate the Authorization header
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                requestTemplate.header("Authorization", authorizationHeader);
                log.debug("JWT token propagated to downstream service");
            }
        }

        // Propagate the Request ID
        String requestId = MDC.get(REQUEST_ID_KEY);
        if (requestId != null) {
            requestTemplate.header(REQUEST_ID_HEADER, requestId);
            log.debug("Request ID propagated to downstream service: {}", requestId);
        }
    }
} 