package com.sro.SpringCoreTask1.aspect;

import com.sro.SpringCoreTask1.annotations.Authenticated;
import com.sro.SpringCoreTask1.exception.UnauthorizedException;
import com.sro.SpringCoreTask1.service.AuthService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuthAspect {

    private final AuthService authService;

    public AuthAspect(AuthService authService) {
        this.authService = authService;
    }

    @Around("@annotation(authConfig)")
    public Object checkAuthentication(ProceedingJoinPoint joinPoint, Authenticated authConfig) throws Throwable {
        if (!authService.isAuthenticated()) {
            throw new UnauthorizedException("Authentication required. Please log in first.");
        }

        if (authConfig.requireTrainee() && !authService.isCurrentUserTrainee()) {
            throw new UnauthorizedException("Access denied. This resource is only available for trainees.");
        }

        if (authConfig.requireTrainer() && !authService.isCurrentUserTrainer()) {
            throw new UnauthorizedException("Access denied. This resource is only available for trainers.");
        }

        return joinPoint.proceed();
    }
}