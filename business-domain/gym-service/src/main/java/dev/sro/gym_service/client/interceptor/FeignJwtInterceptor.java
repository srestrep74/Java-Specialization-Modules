package dev.sro.gym_service.client.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Slf4j
public class FeignJwtInterceptor implements RequestInterceptor {

    private static final String REQUEST_ID_KEY = "requestId";
    private static final String REQUEST_ID_HEADER = "X-Request-ID";

    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                requestTemplate.header("Authorization", authorizationHeader);
                log.debug("JWT token propagated to downstream service");
            }
        }

        String requestId = MDC.get(REQUEST_ID_KEY);

        if (requestId == null && attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            requestId = request.getHeader(REQUEST_ID_HEADER);
            if (requestId != null) {
                log.debug("Request ID retrieved from original HTTP request header: {}", requestId);
            }
        }

        if (requestId != null) {
            requestTemplate.header(REQUEST_ID_HEADER, requestId);
            log.info("Request ID propagated to downstream service: {} | URL: {}", requestId, requestTemplate.url());
        } else {
            log.warn("No Request ID found in MDC or original request to propagate to downstream service | URL: {}",
                    requestTemplate.url());
        }
    }
}