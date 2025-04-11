package com.sro.SpringCoreTask1.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
    
    @Before("execution(* com.sro.SpringCoreTask1.service.*.*(..))")
    public void logBeforeServiceMethod(JoinPoint joinPoint) {
        logger.info("Service Method Invocation - START: {}.{}()", 
            joinPoint.getSignature().getDeclaringType().getSimpleName(),
            joinPoint.getSignature().getName());
            
        if (logger.isDebugEnabled()) {
            logger.debug("Method Arguments: {}", Arrays.toString(joinPoint.getArgs()));
        }
    }

    @AfterReturning(
        pointcut = "execution(* com.sro.SpringCoreTask1.service.*.*(..))", 
        returning = "result"
    )
    public void logAfterServiceMethod(JoinPoint joinPoint, Object result) {
        logger.info("Service Method Invocation - SUCCESS: {}.{}()", 
            joinPoint.getSignature().getDeclaringType().getSimpleName(),
            joinPoint.getSignature().getName());
            
        if (logger.isDebugEnabled()) {
            logger.debug("Method Return Value: {}", result != null ? result.toString() : "null");
        }
    }

    @AfterThrowing(
        pointcut = "execution(* com.sro.SpringCoreTask1.service.*.*(..))", 
        throwing = "exception"
    )
    public void logAfterServiceException(JoinPoint joinPoint, Exception exception) {
        logger.error("Service Method Invocation - FAILED: {}.{}() - Error: {}",
            joinPoint.getSignature().getDeclaringType().getSimpleName(),
            joinPoint.getSignature().getName(),
            exception.getMessage(),
            exception);
    }
    
    @Before("execution(* com.sro.SpringCoreTask1.repository.*.*(..))")
    public void logBeforeRepositoryMethod(JoinPoint joinPoint) {
        logger.debug("Repository Operation - START: {}.{}()", 
            joinPoint.getSignature().getDeclaringType().getSimpleName(),
            joinPoint.getSignature().getName());
    }

    @AfterThrowing(
        pointcut = "execution(* com.sro.SpringCoreTask1.repository.*.*(..))", 
        throwing = "exception"
    )
    public void logAfterRepositoryException(JoinPoint joinPoint, Exception exception) {
        logger.error("Repository Operation - FAILED: {}.{}() - Error: {}",
            joinPoint.getSignature().getDeclaringType().getSimpleName(),
            joinPoint.getSignature().getName(),
            exception.getMessage(),
            exception);
    }
    
    @Before("execution(* com.sro.SpringCoreTask1.controller.v1.*.*(..))")
    public void logBeforeControllerMethod(JoinPoint joinPoint) {
        HttpServletRequest request = 
            ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }
        
        logger.info("REST Request - START: {} {} - Handler: {}.{}()",
            request.getMethod(),
            request.getRequestURI(),
            joinPoint.getSignature().getDeclaringType().getSimpleName(),
            joinPoint.getSignature().getName());
            
        if (logger.isDebugEnabled()) {
            logger.debug("Request Headers: {}", headers);
            logger.debug("Request Parameters: {}", Arrays.toString(joinPoint.getArgs()));
        }
    }
    
    @AfterReturning(
        pointcut = "execution(* com.sro.SpringCoreTask1.controller.v1.*.*(..))", 
        returning = "result"
    )
    public void logAfterControllerMethod(JoinPoint joinPoint, Object result) {
        HttpServletRequest request = 
            ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            
        int statusCode = 200;
        String responseBody = "empty";
        
        if (result instanceof ResponseEntity) {
            ResponseEntity<?> response = (ResponseEntity<?>) result;
            statusCode = response.getStatusCode().value();
            Object body = response.getBody();
            if (body != null) {
                responseBody = body.toString();
            }
        }
        
        logger.info("REST Request - COMPLETED: {} {} - Status: {} - Handler: {}.{}()",
            request.getMethod(),
            request.getRequestURI(),
            statusCode,
            joinPoint.getSignature().getDeclaringType().getSimpleName(),
            joinPoint.getSignature().getName());
            
        if (logger.isDebugEnabled()) {
            logger.debug("Response Body: {}", responseBody);
        }
    }
    
    @AfterThrowing(
        pointcut = "execution(* com.sro.SpringCoreTask1.controller.v1.*.*(..))", 
        throwing = "exception"
    )
    public void logAfterControllerException(JoinPoint joinPoint, Exception exception) {
        HttpServletRequest request = 
            ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            
        logger.error("REST Request - ERROR: {} {} - Handler: {}.{}() - Error: {}",
            request.getMethod(),
            request.getRequestURI(),
            joinPoint.getSignature().getDeclaringType().getSimpleName(),
            joinPoint.getSignature().getName(),
            exception.getMessage(),
            exception);
    }
}