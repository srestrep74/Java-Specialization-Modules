package com.sro.SpringCoreTask1.service.impl;

import com.sro.SpringCoreTask1.entity.Trainee;
import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.exception.DatabaseOperationException;
import com.sro.SpringCoreTask1.exception.ResourceNotFoundException;
import com.sro.SpringCoreTask1.repository.TraineeRepository;
import com.sro.SpringCoreTask1.repository.TrainerRepository;
import com.sro.SpringCoreTask1.service.AuthService;

import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;

    private Long authenticatedTraineeId;
    private Long authenticatedTrainerId;

    public AuthServiceImpl(TraineeRepository traineeRepository, TrainerRepository trainerRepository) {
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
    }

    @Override
    public boolean authenticateTrainee(String username, String password) {
        try {
            Trainee trainee = this.traineeRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Trainee not found with username: " + username));

            if (!trainee.isActive()) {
                throw new ResourceNotFoundException("Trainee is not active");
            }

            if (trainee.getPassword().equals(password)) {
                this.authenticatedTraineeId = trainee.getId();
                this.authenticatedTrainerId = null;
                return true;
            }

            return false;
        } catch (ResourceNotFoundException e) {
            throw e;
        } 
        catch (Exception e) {
            throw new DatabaseOperationException("Error authenticating trainee", e);
        }
    }

    @Override
    public boolean authenticateTrainer(String username, String password) {
        try {
            Trainer trainer = this.trainerRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with username: " + username));

            if (!trainer.isActive()) {
                throw new ResourceNotFoundException("Trainer is not active");
            }

            if (trainer.getPassword().equals(password)) {
                this.authenticatedTrainerId = trainer.getId();
                this.authenticatedTraineeId = null;
                return true;
            }

            return false;
        } catch (ResourceNotFoundException e) {
            throw e;
        } 
        catch (Exception e) {
            throw new DatabaseOperationException("Error authenticating trainer", e);
        }
    }

    @Override
    public Long getCurrentTraineeId() {
        return this.authenticatedTraineeId;
    }

    @Override
    public Long getCurrentTrainerId() {
        return this.authenticatedTrainerId;
    }

    @Override
    public boolean isTraineeAuthenticated() {
        return this.authenticatedTraineeId != null;
    }

    @Override
    public boolean isTrainerAuthenticated() {
        return this.authenticatedTrainerId != null;
    }

    @Override
    public void logout() {
        this.authenticatedTraineeId = null;
        this.authenticatedTrainerId = null;
    }

    @Override
    public void changeTraineePassword(String username, String newPassword) {
        try {
            Trainee trainee = this.traineeRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Trainee not found with username: " + username));

            trainee.setPassword(newPassword);
            this.traineeRepository.save(trainee);
        } catch (ResourceNotFoundException e) {
            throw e;
        } 
        catch (Exception e) {
            throw new DatabaseOperationException("Error changing trainee password", e);
        }
    }

    @Override
    public void changeTrainerPassword(String username, String newPassword) {
        try {
            Trainer trainer = this.trainerRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with username: " + username));

            trainer.setPassword(newPassword);
            this.trainerRepository.save(trainer);
        } catch (ResourceNotFoundException e) {
            throw e;
        } 
        catch (Exception e) {
            throw new DatabaseOperationException("Error changing trainer password", e);
        }
    }
}