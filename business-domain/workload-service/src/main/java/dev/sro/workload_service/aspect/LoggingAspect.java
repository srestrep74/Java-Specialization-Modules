package dev.sro.workload_service.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Aspect for comprehensive logging across all layers of the workload-service.
 * Implements best practices for logging including:
 * - Request/Response logging for controllers
 * - Service method execution logging
 * - Repository operation logging
 * - Exception logging with stack traces
 * - Performance monitoring with execution times
 * - MDC (Mapped Diagnostic Context) for request tracing
 */
@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
    private static final String REQUEST_ID_KEY = "requestId";
    private static final String USER_KEY = "user";

    // =================== CONTROLLER LAYER ===================

    @Before("execution(* dev.sro.workload_service.presentation.controller.v1.*.*(..))")
    public void logBeforeControllerMethod(JoinPoint joinPoint) {
        try {
            HttpServletRequest request = getCurrentHttpRequest();
            if (request == null) return;

            // Try to get request ID from header, otherwise generate a new one
            String requestId = request.getHeader("X-Request-ID");
            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString().substring(0, 8);
            }
            MDC.put(REQUEST_ID_KEY, requestId);

            // Extract user from security context if available
            String username = extractUsernameFromSecurity();
            if (username != null) {
                MDC.put(USER_KEY, username);
            }

            Map<String, String> headers = extractRelevantHeaders(request);
            String queryParams = extractQueryParameters(request);

            logger.info("REST Request - START: {} {} | Handler: {}.{}() | RequestID: {} | User: {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    joinPoint.getSignature().getDeclaringType().getSimpleName(),
                    joinPoint.getSignature().getName(),
                    requestId,
                    username != null ? username : "anonymous");

            if (logger.isDebugEnabled()) {
                logger.debug("Request Headers: {}", headers);
                logger.debug("Query Parameters: {}", queryParams);
                logger.debug("Method Arguments: {}", Arrays.toString(joinPoint.getArgs()));
            }
        } catch (Exception e) {
            logger.warn("Failed to log controller method start: {}", e.getMessage());
        }
    }

    @Around("execution(* dev.sro.workload_service.presentation.controller.v1.*.*(..))")
    public Object logAroundControllerMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = null;
        
        try {
            result = joinPoint.proceed();
            return result;
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;
            logControllerMethodCompletion(joinPoint, result, executionTime, null);
        }
    }

    @AfterThrowing(pointcut = "execution(* dev.sro.workload_service.presentation.controller.v1.*.*(..))", throwing = "exception")
    public void logAfterControllerException(JoinPoint joinPoint, Exception exception) {
        try {
            HttpServletRequest request = getCurrentHttpRequest();
            if (request == null) return;

            logger.error("REST Request - ERROR: {} {} | Handler: {}.{}() | RequestID: {} | Error: {}",
                    request.getMethod(),
                    request.getRequestURI(),
                    joinPoint.getSignature().getDeclaringType().getSimpleName(),
                    joinPoint.getSignature().getName(),
                    MDC.get(REQUEST_ID_KEY),
                    exception.getMessage(),
                    exception);
        } catch (Exception e) {
            logger.warn("Failed to log controller exception: {}", e.getMessage());
        } finally {
            // Clear MDC to prevent memory leaks
            MDC.clear();
        }
    }

    // =================== APPLICATION SERVICE LAYER ===================

    @Before("execution(* dev.sro.workload_service.application.service.*.*(..))")
    public void logBeforeApplicationServiceMethod(JoinPoint joinPoint) {
        logger.info("Application Service - START: {}.{}() | RequestID: {}",
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(),
                MDC.get(REQUEST_ID_KEY));

        if (logger.isDebugEnabled()) {
            logger.debug("Service Arguments: {}", Arrays.toString(joinPoint.getArgs()));
        }
    }

    @Around("execution(* dev.sro.workload_service.application.service.*.*(..))")
    public Object logAroundApplicationServiceMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = null;
        Exception caughtException = null;

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Exception e) {
            caughtException = e;
            throw e;
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;
            
            if (caughtException != null) {
                logger.error("Application Service - FAILED: {}.{}() | ExecutionTime: {}ms | RequestID: {} | Error: {}",
                        joinPoint.getSignature().getDeclaringType().getSimpleName(),
                        joinPoint.getSignature().getName(),
                        executionTime,
                        MDC.get(REQUEST_ID_KEY),
                        caughtException.getMessage(),
                        caughtException);
            } else {
                logger.info("Application Service - SUCCESS: {}.{}() | ExecutionTime: {}ms | RequestID: {}",
                        joinPoint.getSignature().getDeclaringType().getSimpleName(),
                        joinPoint.getSignature().getName(),
                        executionTime,
                        MDC.get(REQUEST_ID_KEY));

                if (logger.isDebugEnabled() && result != null) {
                    logger.debug("Service Return Value: {}", result.toString());
                }
            }
        }
    }

    // =================== DOMAIN REPOSITORY LAYER ===================

    @Before("execution(* dev.sro.workload_service.domain.repository.*.*(..))")
    public void logBeforeDomainRepositoryMethod(JoinPoint joinPoint) {
        logger.debug("Domain Repository - START: {}.{}() | RequestID: {}",
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(),
                MDC.get(REQUEST_ID_KEY));
    }

    @Around("execution(* dev.sro.workload_service.domain.repository.*.*(..))")
    public Object logAroundDomainRepositoryMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = null;
        Exception caughtException = null;

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Exception e) {
            caughtException = e;
            throw e;
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;
            
            if (caughtException != null) {
                logger.error("Domain Repository - FAILED: {}.{}() | ExecutionTime: {}ms | RequestID: {} | Error: {}",
                        joinPoint.getSignature().getDeclaringType().getSimpleName(),
                        joinPoint.getSignature().getName(),
                        executionTime,
                        MDC.get(REQUEST_ID_KEY),
                        caughtException.getMessage(),
                        caughtException);
            } else {
                logger.debug("Domain Repository - SUCCESS: {}.{}() | ExecutionTime: {}ms | RequestID: {}",
                        joinPoint.getSignature().getDeclaringType().getSimpleName(),
                        joinPoint.getSignature().getName(),
                        executionTime,
                        MDC.get(REQUEST_ID_KEY));
            }
        }
    }

    // =================== INFRASTRUCTURE REPOSITORY LAYER ===================

    @Before("execution(* dev.sro.workload_service.infrastructure.repository.*.*(..))")
    public void logBeforeInfrastructureRepositoryMethod(JoinPoint joinPoint) {
        logger.debug("Infrastructure Repository - START: {}.{}() | RequestID: {}",
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(),
                MDC.get(REQUEST_ID_KEY));
    }

    @Around("execution(* dev.sro.workload_service.infrastructure.repository.*.*(..))")
    public Object logAroundInfrastructureRepositoryMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = null;
        Exception caughtException = null;

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Exception e) {
            caughtException = e;
            throw e;
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;
            
            if (caughtException != null) {
                logger.error("Infrastructure Repository - FAILED: {}.{}() | ExecutionTime: {}ms | RequestID: {} | Error: {}",
                        joinPoint.getSignature().getDeclaringType().getSimpleName(),
                        joinPoint.getSignature().getName(),
                        executionTime,
                        MDC.get(REQUEST_ID_KEY),
                        caughtException.getMessage(),
                        caughtException);
            } else {
                logger.debug("Infrastructure Repository - SUCCESS: {}.{}() | ExecutionTime: {}ms | RequestID: {}",
                        joinPoint.getSignature().getDeclaringType().getSimpleName(),
                        joinPoint.getSignature().getName(),
                        executionTime,
                        MDC.get(REQUEST_ID_KEY));
            }
        }
    }

    // =================== MAPPER LAYER ===================

    @Around("execution(* dev.sro.workload_service.application.mapper.*.*(..))")
    public Object logAroundMapperMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        if (logger.isTraceEnabled()) {
            long startTime = System.currentTimeMillis();
            Object result = null;
            Exception caughtException = null;

            try {
                result = joinPoint.proceed();
                return result;
            } catch (Exception e) {
                caughtException = e;
                throw e;
            } finally {
                long executionTime = System.currentTimeMillis() - startTime;
                
                if (caughtException != null) {
                    logger.trace("Mapper - FAILED: {}.{}() | ExecutionTime: {}ms | RequestID: {} | Error: {}",
                            joinPoint.getSignature().getDeclaringType().getSimpleName(),
                            joinPoint.getSignature().getName(),
                            executionTime,
                            MDC.get(REQUEST_ID_KEY),
                            caughtException.getMessage());
                } else {
                    logger.trace("Mapper - SUCCESS: {}.{}() | ExecutionTime: {}ms | RequestID: {}",
                            joinPoint.getSignature().getDeclaringType().getSimpleName(),
                            joinPoint.getSignature().getName(),
                            executionTime,
                            MDC.get(REQUEST_ID_KEY));
                }
            }
        } else {
            return joinPoint.proceed();
        }
    }

    // =================== UTILITY METHODS ===================

    private void logControllerMethodCompletion(JoinPoint joinPoint, Object result, long executionTime, Exception exception) {
        try {
            HttpServletRequest request = getCurrentHttpRequest();
            if (request == null) return;

            int statusCode = extractStatusCode(result);
            String responseInfo = extractResponseInfo(result);

            if (exception != null) {
                logger.error("REST Request - ERROR: {} {} | Status: {} | ExecutionTime: {}ms | Handler: {}.{}() | RequestID: {} | Error: {}",
                        request.getMethod(),
                        request.getRequestURI(),
                        statusCode,
                        executionTime,
                        joinPoint.getSignature().getDeclaringType().getSimpleName(),
                        joinPoint.getSignature().getName(),
                        MDC.get(REQUEST_ID_KEY),
                        exception.getMessage());
            } else {
                logger.info("REST Request - COMPLETED: {} {} | Status: {} | ExecutionTime: {}ms | Handler: {}.{}() | RequestID: {}",
                        request.getMethod(),
                        request.getRequestURI(),
                        statusCode,
                        executionTime,
                        joinPoint.getSignature().getDeclaringType().getSimpleName(),
                        joinPoint.getSignature().getName(),
                        MDC.get(REQUEST_ID_KEY));

                if (logger.isDebugEnabled()) {
                    logger.debug("Response Info: {}", responseInfo);
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to log controller method completion: {}", e.getMessage());
        } finally {
            // Clear MDC only after request completion
            if (exception != null || result != null) {
                MDC.clear();
            }
        }
    }

    private HttpServletRequest getCurrentHttpRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            return attributes.getRequest();
        } catch (Exception e) {
            return null;
        }
    }

    private String extractUsernameFromSecurity() {
        try {
            return org.springframework.security.core.context.SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getName();
        } catch (Exception e) {
            return null;
        }
    }

    private Map<String, String> extractRelevantHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            // Only log relevant headers, avoid sensitive information
            if (isRelevantHeader(headerName)) {
                headers.put(headerName, request.getHeader(headerName));
            }
        }
        return headers;
    }

    private boolean isRelevantHeader(String headerName) {
        String lowerHeaderName = headerName.toLowerCase();
        return lowerHeaderName.equals("content-type") ||
               lowerHeaderName.equals("accept") ||
               lowerHeaderName.equals("user-agent") ||
               lowerHeaderName.equals("x-forwarded-for") ||
               lowerHeaderName.equals("x-real-ip");
    }

    private String extractQueryParameters(HttpServletRequest request) {
        String queryString = request.getQueryString();
        return queryString != null ? queryString : "none";
    }

    private int extractStatusCode(Object result) {
        if (result instanceof ResponseEntity) {
            return ((ResponseEntity<?>) result).getStatusCode().value();
        }
        return 200; // Default status code
    }

    private String extractResponseInfo(Object result) {
        if (result instanceof ResponseEntity) {
            ResponseEntity<?> response = (ResponseEntity<?>) result;
            Object body = response.getBody();
            if (body != null) {
                String bodyStr = body.toString();
                // Truncate long responses for readability
                return bodyStr.length() > 200 ? bodyStr.substring(0, 200) + "..." : bodyStr;
            }
            return "empty body";
        }
        return result != null ? result.toString() : "null";
    }
} 