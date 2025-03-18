package com.sro.SpringCoreTask1.facade;


import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.sro.SpringCoreTask1.dto.TraineeTrainingFilterDTO;
import com.sro.SpringCoreTask1.dto.TrainerTrainingFilterDTO;
import com.sro.SpringCoreTask1.dto.request.TraineeRequestDTO;
import com.sro.SpringCoreTask1.dto.request.TrainerRequestDTO;
import com.sro.SpringCoreTask1.dto.request.TrainingRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TraineeResponseDTO;
import com.sro.SpringCoreTask1.dto.response.TrainerResponseDTO;
import com.sro.SpringCoreTask1.dto.response.TrainingResponseDTO;
import com.sro.SpringCoreTask1.dto.response.TrainingTypeResponseDTO;
import com.sro.SpringCoreTask1.service.AuthService;
import com.sro.SpringCoreTask1.service.TraineeService;
import com.sro.SpringCoreTask1.service.TrainerService;
import com.sro.SpringCoreTask1.service.TrainingService;
import com.sro.SpringCoreTask1.service.TrainingTypeService;


@Component
public class SystemServiceFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;
    private final TrainingTypeService trainingTypeService;
    private final AuthService authService;

    public SystemServiceFacade(TraineeService traineeService, TrainerService trainerService, TrainingService trainingService,    TrainingTypeService trainingTypeService, AuthService authService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
        this.trainingTypeService = trainingTypeService;
        this.authService = authService;
    }

    public TrainerResponseDTO createTrainerProfile(TrainerRequestDTO trainerRequestDTO) {
        return trainerService.save(trainerRequestDTO);
    }

    public TraineeResponseDTO createTraineeProfile(TraineeRequestDTO traineeRequestDTO) {
        return traineeService.save(traineeRequestDTO);
    }

    public boolean authenticateTrainee(String username, String password) {
        return authService.authenticateTrainee(username, password);
    }

    public boolean authenticateTrainer(String username, String password) {
        return authService.authenticateTrainer(username, password);
    }

    public Long getCurrentTraineeId() {
        return authService.getCurrentTraineeId();
    }

    public Long getCurrentTrainerId() {
        return authService.getCurrentTrainerId();
    }

    public boolean isTraineeAuthenticated() {
        return authService.isTraineeAuthenticated();
    }

    public boolean isTrainerAuthenticated() {
        return authService.isTrainerAuthenticated();
    }

    public void logout() {
        authService.logout();
    }

    public boolean validateTraineeCredentials(String username, String password) {
        return authService.authenticateTrainee(username, password);
    }

    public boolean validateTrainerCredentials(String username, String password) {
        return authService.authenticateTrainer(username, password);
    }

    public TraineeResponseDTO selectTraineeProfileByUsername(String username) {
        return traineeService.findByUsername(username);
    }

    public TrainerResponseDTO selectTrainerProfileByUsername(String username) {
        return trainerService.findByUsername(username);
    }

    public TrainerResponseDTO getTrainerById(Long id) {
        return trainerService.findById(id);
    }

    public TraineeResponseDTO getTraineeById(Long id) {
        return traineeService.findById(id);
    }

    public boolean changeTraineePassword(Long traineeId, String newPassword) {
        return traineeService.updateTraineePassword(traineeId, newPassword);
    }

    public boolean changeTrainerPassword(Long trainerId, String newPassword) {
        return trainerService.updateTrainerPassword(trainerId, newPassword);
    }

    public TrainerResponseDTO updateTrainerProfile(TrainerRequestDTO trainerRequestDTO) {
        return trainerService.update(trainerRequestDTO);
    }

    public TraineeResponseDTO updateTraineeProfile(TraineeRequestDTO traineeRequestDTO) {
        return traineeService.update(traineeRequestDTO);
    }

    public void setTraineeStatus(Long traineeId, boolean isActive) {
        traineeService.setTraineeStatus(traineeId, isActive);
    }

    public void setTrainerStatus(Long trainerId, boolean isActive) {
        trainerService.setTrainerStatus(trainerId, isActive);
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

    public List<TrainingTypeResponseDTO> getTrainingTypes() {
        return trainingTypeService.findAll();
    }

    public void updateTraineeTrainersList(Long traineeId, Long trainerId, String action) {
        if (action == null || (!action.equalsIgnoreCase("add") && !action.equalsIgnoreCase("remove"))) {
            throw new IllegalArgumentException("Action must be 'add' or 'remove'");
        }
        if (action.equalsIgnoreCase("add")) {
            traineeService.addTrainerToTrainee(traineeId, trainerId);
        } else {
            traineeService.removeTrainerFromTrainee(traineeId, trainerId);
        }
    }

    public Set<TrainerResponseDTO> getTraineeTrainers(Long traineeId) {
        return trainerService.getTraineeTrainers(traineeId);
    }
}
