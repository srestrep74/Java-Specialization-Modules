package com.sro.SpringCoreTask1.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* com.sro.SpringCoreTask1.service.*.*(..))")
    public void logBeforeServiceMethod(JoinPoint joinPoint) {
        logger.info("Starting execution of service method: {}", joinPoint.getSignature().getName());
        logger.debug("Arguments for method {}: {}", joinPoint.getSignature().getName(), joinPoint.getArgs());
    }

    @AfterReturning(pointcut = "execution(* com.sro.SpringCoreTask1.service.*.*(..))", returning = "result")
    public void logAfterServiceMethod(JoinPoint joinPoint, Object result) {
        logger.info("Service method {} executed successfully.", joinPoint.getSignature().getName());
        logger.debug("Result of method {}: {}", joinPoint.getSignature().getName(), result);
    }

    @AfterThrowing(pointcut = "execution(* com.sro.SpringCoreTask1.service.*.*(..))", throwing = "exception")
    public void logAfterServiceException(JoinPoint joinPoint, Exception exception) {
        logger.error("Error in service method {}: {}", joinPoint.getSignature().getName(), exception.getMessage(), exception);
    }

    @Before("execution(* com.sro.SpringCoreTask1.facade.*.*(..))")
    public void logBeforeFacadeMethod(JoinPoint joinPoint) {
        logger.info("Starting execution of facade method: {}", joinPoint.getSignature().getName());
        logger.debug("Arguments for method {}: {}", joinPoint.getSignature().getName(), joinPoint.getArgs());
    }

    @AfterReturning(pointcut = "execution(* com.sro.SpringCoreTask1.facade.*.*(..))", returning = "result")
    public void logAfterFacadeMethod(JoinPoint joinPoint, Object result) {
        logger.info("Facade method {} executed successfully.", joinPoint.getSignature().getName());
        logger.debug("Result of method {}: {}", joinPoint.getSignature().getName(), result);
    }

    @AfterThrowing(pointcut = "execution(* com.sro.SpringCoreTask1.facade.*.*(..))", throwing = "exception")
    public void logAfterFacadeException(JoinPoint joinPoint, Exception exception) {
        logger.error("Error in facade method {}: {}", joinPoint.getSignature().getName(), exception.getMessage(), exception);
    }

    @Before("execution(* com.sro.SpringCoreTask1.repository.*.*(..))")
    public void logBeforeRepositoryMethod(JoinPoint joinPoint) {
        logger.info("Starting execution of repository method: {}", joinPoint.getSignature().getName());
        logger.debug("Arguments for method {}: {}", joinPoint.getSignature().getName(), joinPoint.getArgs());
    }

    @AfterThrowing(pointcut = "execution(* com.sro.SpringCoreTask1.repository.*.*(..))", throwing = "exception")
    public void logAfterRepositoryException(JoinPoint joinPoint, Exception exception) {
        logger.error("Error in repository method {}: {}", joinPoint.getSignature().getName(), exception.getMessage(), exception);
    }
}