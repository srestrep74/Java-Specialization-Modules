package com.sro.SpringCoreTask1.service.impl;

import org.springframework.stereotype.Service;

import com.sro.SpringCoreTask1.entity.Trainee;
import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.repository.TraineeRepository;
import com.sro.SpringCoreTask1.repository.TrainerRepository;
import com.sro.SpringCoreTask1.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService{
    
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
        Trainee trainee = this.traineeRepository.findByUsername(username).orElseThrow(
            () -> new IllegalArgumentException("Trainee not found with username: " + username)
        );

        if(!trainee.isActive()) {
            throw new IllegalArgumentException("Trainee is not active");
        }
        if(trainee.getPassword().equals(password)) {
           this.authenticatedTraineeId = trainee.getId();
           this.authenticatedTrainerId = null;
           return true; 
        }

        return false;
    }

    @Override
    public boolean authenticateTrainer(String username, String password) {
        Trainer trainer = this.trainerRepository.findByUsername(username).orElseThrow(
            () -> new IllegalArgumentException("Trainer not found with username: " + username)
        );

        if(!trainer.isActive()) {
            throw new IllegalArgumentException("Trainer is not active");
        }

        if(trainer.getPassword().equals(password)) {
            this.authenticatedTrainerId = trainer.getId();
            this.authenticatedTraineeId = null;
            return true;
        }

        return false;
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

    public void changeTraineePassword(String username, String newPassword) {
        this.traineeRepository.findByUsername(username).ifPresent(trainee -> trainee.setPassword(newPassword));
    }

    public void changeTrainerPassword(String username, String newPassword) {
        this.trainerRepository.findByUsername(username).ifPresent(trainer -> trainer.setPassword(newPassword));
    }

}
