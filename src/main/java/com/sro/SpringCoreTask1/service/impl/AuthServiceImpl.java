package com.sro.SpringCoreTask1.service.impl;

import com.sro.SpringCoreTask1.dtos.v1.response.auth.LoginResponse;
import com.sro.SpringCoreTask1.entity.Trainee;
import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.entity.User;
import com.sro.SpringCoreTask1.exception.AuthenticationFailedException;
import com.sro.SpringCoreTask1.exception.DatabaseOperationException;
import com.sro.SpringCoreTask1.repository.TraineeRepository;
import com.sro.SpringCoreTask1.repository.TrainerRepository;
import com.sro.SpringCoreTask1.service.AuthService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;

    private User authenticatedUser;
    private boolean isTrainee;

    public AuthServiceImpl(TraineeRepository traineeRepository, TrainerRepository trainerRepository) {
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse authenticate(String username, String password) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Username and password cannot be null or empty");
        }

        try {
            Trainee trainee = traineeRepository.findByUsername(username).orElse(null);
            if (trainee != null) {
                if (!trainee.isActive()) {
                    throw new AuthenticationFailedException("User is not active");
                }

                if (trainee.getPassword().equals(password)) {
                    this.authenticatedUser = trainee;
                    this.isTrainee = true;
                    return new LoginResponse(username, true);
                }
            }

            Trainer trainer = trainerRepository.findByUsername(username).orElse(null);
            if (trainer != null) {
                if (!trainer.isActive()) {
                    throw new AuthenticationFailedException("User is not active");
                }

                if (trainer.getPassword().equals(password)) {
                    this.authenticatedUser = trainer;
                    this.isTrainee = false;
                    return new LoginResponse(username, true);
                }
            }

            throw new AuthenticationFailedException("Invalid username or password");
        } catch (AuthenticationFailedException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error during authentication", e);
        }
    }

    @Override
    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        if (username == null || username.isEmpty() ||
                oldPassword == null || oldPassword.isEmpty() ||
                newPassword == null || newPassword.isEmpty()) {
            throw new IllegalArgumentException("Username, old password and new password cannot be null or empty");
        }

        try {
            LoginResponse loginResponse = authenticate(username, oldPassword);

            if (!loginResponse.success()) {
                throw new AuthenticationFailedException("Current password is incorrect");
            }

            if (isCurrentUserTrainee()) {
                Trainee trainee = (Trainee) authenticatedUser;
                trainee.setPassword(newPassword);
                traineeRepository.save(trainee);
            } else {
                Trainer trainer = (Trainer) authenticatedUser;
                trainer.setPassword(newPassword);
                trainerRepository.save(trainer);
            }
        } catch (AuthenticationFailedException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error changing password", e);
        }
    }

    @Override
    public void logout() {
        this.authenticatedUser = null;
    }

    @Override
    public User getCurrentUser() {
        return this.authenticatedUser;
    }

    @Override
    public void setCurrentUser(User user) {
        this.authenticatedUser = user;
    }

    @Override
    public boolean isAuthenticated() {
        return this.authenticatedUser != null;
    }

    @Override
    public boolean isCurrentUserTrainee() {
        return isAuthenticated() && this.isTrainee;
    }

    @Override
    public boolean isCurrentUserTrainer() {
        return isAuthenticated() && !this.isTrainee;
    }

}