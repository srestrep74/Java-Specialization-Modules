package com.sro.SpringCoreTask1.facade.impl;

import org.springframework.stereotype.Component;

import com.sro.SpringCoreTask1.exception.DatabaseOperationException;
import com.sro.SpringCoreTask1.facade.AuthServiceFacade;
import com.sro.SpringCoreTask1.service.AuthService;

@Component
public class AuthServiceFacadeImpl implements AuthServiceFacade {
    
    private final AuthService authService;
    
    public AuthServiceFacadeImpl(AuthService authService) {
        this.authService = authService;
    }
    
    @Override
    public boolean authenticateTrainee(String username, String password) {
        if (username == null || password == null) {
            throw new IllegalArgumentException("Username and password cannot be null");
        }
        try {
            return authService.authenticateTrainee(username, password);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error authenticating Trainee", e);
        }
    }

    @Override
    public boolean authenticateTrainer(String username, String password) {
        if (username == null || password == null) {
            throw new IllegalArgumentException("Username and password cannot be null");
        }
        try {
            return authService.authenticateTrainer(username, password);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error authenticating Trainer", e);
        }
    }

    @Override
    public Long getAuthenticatedTraineeId() {
        try {
            return authService.getCurrentTraineeId();
        } catch (Exception e) {
            throw new DatabaseOperationException("Error getting current Trainee ID", e);
        }
    }

    @Override
    public Long getAuthenticatedTrainerId() {
        try {
            return authService.getCurrentTrainerId();
        } catch (Exception e) {
            throw new DatabaseOperationException("Error getting current Trainer ID", e);
        }
    }

    @Override
    public boolean isTraineeAuthenticated() {
        try {
            return authService.isTraineeAuthenticated();
        } catch (Exception e) {
            throw new DatabaseOperationException("Error checking Trainee authentication status", e);
        }
    }

    @Override
    public boolean isTrainerAuthenticated() {
        try {
            return authService.isTrainerAuthenticated();
        } catch (Exception e) {
            throw new DatabaseOperationException("Error checking Trainer authentication status", e);
        }
    }

    @Override
    public void perfomLogout() {
        try {
            authService.logout();
        } catch (Exception e) {
            throw new DatabaseOperationException("Error logging out", e);
        }
    }

    @Override
    public boolean verifyTraineeCredentials(String username, String password) {
        if (username == null || password == null) {
            throw new IllegalArgumentException("Username and password cannot be null");
        }
        try {
            return authService.authenticateTrainee(username, password);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error validating Trainee credentials", e);
        }
    }

    @Override
    public boolean verifyTrainerCredentials(String username, String password) {
        if (username == null || password == null) {
            throw new IllegalArgumentException("Username and password cannot be null");
        }
        try {
            return authService.authenticateTrainer(username, password);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error validating Trainer credentials", e);
        }
    }
}