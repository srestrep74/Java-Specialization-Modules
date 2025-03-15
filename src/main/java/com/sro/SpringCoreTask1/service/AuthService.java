package com.sro.SpringCoreTask1.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.sro.SpringCoreTask1.entity.Trainee;
import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.repository.TraineeRepository;
import com.sro.SpringCoreTask1.repository.TrainerRepository;

@Service
public class AuthService {
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;

    public AuthService(TraineeRepository traineeRepository, TrainerRepository trainerRepository) {
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
    }

    public boolean validateTraineeCredentials(String username, String password) {
        Optional<Trainee> trainee = traineeRepository.findByUsername(username);
        return trainee.isPresent() && trainee.get().getPassword().equals(password);
    }

    public boolean validateTrainerCredentials(String username, String password) {
        Optional<Trainer> trainer = trainerRepository.findByUsername(username);
        return trainer.isPresent() && trainer.get().getPassword().equals(password);
    }
}
