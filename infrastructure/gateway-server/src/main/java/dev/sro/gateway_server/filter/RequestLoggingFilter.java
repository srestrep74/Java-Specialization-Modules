package dev.sro.gateway_server.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class RequestLoggingFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);
    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final String REQUEST_ID_MDC_KEY = "requestId";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long startTime = System.currentTimeMillis();
        ServerHttpRequest request = exchange.getRequest();

        // 1. Get request ID from header or generate a new one
        String requestId = request.getHeaders().getFirst(REQUEST_ID_HEADER);
        if (requestId == null) {
            requestId = UUID.randomUUID().toString().substring(0, 8);
        }

        // 2. Add request ID to the MDC for logging
        MDC.put(REQUEST_ID_MDC_KEY, requestId);

        // 3. Log incoming request
        log.info("Incoming request | {} {} | User-Agent: {}",
                request.getMethod(),
                request.getURI(),
                request.getHeaders().getFirst("User-Agent"));


        // 4. Add the request ID to the request headers for downstream services
        ServerHttpRequest mutatedRequest = request.mutate()
                .header(REQUEST_ID_HEADER, requestId)
                .build();

        ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();

        // 5. Chain processing and log response
        return chain.filter(mutatedExchange).then(Mono.fromRunnable(() -> {
            long duration = System.currentTimeMillis() - startTime;
            log.info("Outgoing response | Status: {} | Duration: {}ms",
                    exchange.getResponse().getStatusCode(),
                    duration);
            MDC.remove(REQUEST_ID_MDC_KEY);
        }));
    }

    @Override
    public int getOrder() {
        // Run this filter with high precedence
        return Ordered.HIGHEST_PRECEDENCE;
    }
} 