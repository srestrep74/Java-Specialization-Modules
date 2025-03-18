package com.sro.SpringCoreTask1.service.impl;

import org.springframework.stereotype.Service;

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
    public boolean authenticateTrainee(String username, String password) {;
        return this.traineeRepository.findByUsername(username)
                    .filter(trainee -> password.equals(trainee.getPassword()))
                    .map(trainee -> {
                        this.authenticatedTraineeId = trainee.getId();
                        this.authenticatedTrainerId = null;
                        return true;
                    })
                    .orElse(false);
    }

    @Override
    public boolean authenticateTrainer(String username, String password) {
        return this.trainerRepository.findByUsername(username)
                    .filter(trainer -> password.equals(trainer.getPassword()))
                    .map(trainer -> {
                        this.authenticatedTraineeId = null;
                        this.authenticatedTrainerId = trainer.getId();
                        return true;
                    })
                    .orElse(false);
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
