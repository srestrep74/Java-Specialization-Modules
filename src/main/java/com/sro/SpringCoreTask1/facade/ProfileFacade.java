package com.sro.SpringCoreTask1.facade;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.sro.SpringCoreTask1.dto.TraineeTrainingFilterDTO;
import com.sro.SpringCoreTask1.dto.TrainerTrainingFilterDTO;
import com.sro.SpringCoreTask1.dto.request.TraineeRequestDTO;
import com.sro.SpringCoreTask1.dto.request.TrainerRequestDTO;
import com.sro.SpringCoreTask1.dto.request.TrainingRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TraineeResponseDTO;
import com.sro.SpringCoreTask1.dto.response.TrainerResponseDTO;
import com.sro.SpringCoreTask1.dto.response.TrainingResponseDTO;
import com.sro.SpringCoreTask1.exception.ResourceNotFoundException;
import com.sro.SpringCoreTask1.service.AuthService;
import com.sro.SpringCoreTask1.service.TraineeService;
import com.sro.SpringCoreTask1.service.TrainerService;
import com.sro.SpringCoreTask1.service.TrainingService;

@Component
public class ProfileFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;
    private final AuthService authService;

    public ProfileFacade(TraineeService traineeService, TrainerService trainerService, TrainingService trainingService, AuthService authService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
        this.authService = authService;
    }

    public TrainerResponseDTO createTrainerProfile(TrainerRequestDTO trainerRequestDTO) {
        return trainerService.save(trainerRequestDTO);
    }

    public TraineeResponseDTO createTraineeProfile(TraineeRequestDTO traineeRequestDTO) {
        return traineeService.save(traineeRequestDTO);
    }

    public boolean validateTraineeCredentials(String username, String password) {
        return authService.validateTraineeCredentials(username, password);
    }

    public boolean validateTrainerCredentials(String username, String password) {
        return authService.validateTrainerCredentials(username, password);
    }

    public Optional<TraineeResponseDTO> selectTraineeProfileByUsername(String username) {
        return traineeService.findByUsername(username);
    }

    public Optional<TrainerResponseDTO> selectTrainerProfileByUsername(String username) {
        return trainerService.findByUsername(username);
    }

    public void changeTraineePassword(String username, String newPassword) {
        Optional<TraineeResponseDTO> trainee = traineeService.findByUsername(username);
        if(trainee.isPresent()) {
            TraineeRequestDTO requestDTO = new TraineeRequestDTO(
                trainee.get().firstName(),
                trainee.get().lastName(),
                trainee.get().username(),
                newPassword,
                trainee.get().active(),
                trainee.get().address(),
                trainee.get().dateOfBirth()
            );
            traineeService.update(requestDTO);
        }else {
            throw new ResourceNotFoundException("Trainee not found with username: " + username);
        }
    }

    public void changeTrainerPassword(String username, String newPassword) {
        Optional<TrainerResponseDTO> trainer = trainerService.findByUsername(username);
        if(trainer.isPresent()) {
            TrainerRequestDTO requestDTO = new TrainerRequestDTO(
                trainer.get().firstName(),
                trainer.get().lastName(),
                trainer.get().username(),
                newPassword,
                trainer.get().active(),
                trainer.get().trainingType().id()
            );
            trainerService.update(requestDTO);
        }else {
            throw new ResourceNotFoundException("Trainer not found with username: " + username);
        }
    }

    public TrainerResponseDTO updateTrainerProfile(TrainerRequestDTO trainerRequestDTO) {
        return trainerService.update(trainerRequestDTO);
    }

    public TraineeResponseDTO updateTraineeProfile(TraineeRequestDTO traineeRequestDTO) {
        return traineeService.update(traineeRequestDTO);
    }

    public void activateTrainee(Long traineeId) {
        traineeService.activateTrainee(traineeId);
    }

    public void deactivateTrainee(Long traineeId) {
        traineeService.deactivateTrainee(traineeId);
    }

    public void activateTrainer(Long trainerId) {
        trainerService.activateTrainer(trainerId);
    }

    public void deactivateTrainer(Long trainerId) {
        trainerService.deactivateTrainer(trainerId);
    }

    public void deleteTraineeProfileByUsername(String username) {
        traineeService.deleteByUsername(username);
    }

    public List<TrainingResponseDTO> getTraineeTrainingsList(String traineeUsername, TraineeTrainingFilterDTO filterDTO) {
        return trainingService.findTrainingsByTraineeWithFilters(filterDTO);
    }

    public List<TrainingResponseDTO> getTrainerTrainingsList(String trainerUsername, TrainerTrainingFilterDTO filterDTO) {
        return trainingService.findTrainingsByTrainerWithFilters(filterDTO);
    }

    public TrainingResponseDTO addTraining(TrainingRequestDTO trainingRequestDTO) {
        return trainingService.save(trainingRequestDTO);
    }

    public List<TrainerResponseDTO> getTrainersNotAssignedToTrainee(String traineeUsername) {
        return trainerService.getTrainersNotAssignedToTrainee(traineeUsername);
    }

    // Update Trainees trainings list
    // --------------------------------------------
}
