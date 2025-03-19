package com.sro.SpringCoreTask1.facade.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.sro.SpringCoreTask1.dto.TraineeTrainingFilterDTO;
import com.sro.SpringCoreTask1.dto.request.TraineeRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TraineeResponseDTO;
import com.sro.SpringCoreTask1.dto.response.TrainerResponseDTO;
import com.sro.SpringCoreTask1.dto.response.TrainingResponseDTO;
import com.sro.SpringCoreTask1.exception.DatabaseOperationException;
import com.sro.SpringCoreTask1.exception.ResourceAlreadyExistsException;
import com.sro.SpringCoreTask1.exception.ResourceNotFoundException;
import com.sro.SpringCoreTask1.facade.TraineeServiceFacade;
import com.sro.SpringCoreTask1.service.TraineeService;
import com.sro.SpringCoreTask1.service.TrainerService;
import com.sro.SpringCoreTask1.service.TrainingService;

@Component
public class TraineeServiceFacadeImpl implements TraineeServiceFacade {
    
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;
    
    public TraineeServiceFacadeImpl(
            TraineeService traineeService,
            TrainerService trainerService,
            TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }
    
    @Override
    public TraineeResponseDTO createTrainee(TraineeRequestDTO traineeRequestDTO) {
        if (traineeRequestDTO == null) {
            throw new IllegalArgumentException("TraineeRequestDTO cannot be null");
        }
        try {
            return traineeService.save(traineeRequestDTO);
        } catch (ResourceAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error creating Trainee profile", e);
        }
    }
    
    @Override
    public TraineeResponseDTO findTraineeByUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        try {
            return traineeService.findByUsername(username);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding Trainee by username", e);
        }
    }
    
    @Override
    public TraineeResponseDTO findTraineeById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Trainee ID cannot be null");
        }
        try {
            return traineeService.findById(id);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding Trainee by ID", e);
        }
    }
    
    @Override
    public boolean updateTraineePassword(Long traineeId, String newPassword) {
        if (traineeId == null || newPassword == null || newPassword.isEmpty()) {
            throw new IllegalArgumentException("Trainee ID and new password cannot be null or empty");
        }
        try {
            return traineeService.updateTraineePassword(traineeId, newPassword);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error updating Trainee password", e);
        }
    }
    
    @Override
    public TraineeResponseDTO updateTrainee(TraineeRequestDTO traineeRequestDTO) {
        if (traineeRequestDTO == null) {
            throw new IllegalArgumentException("TraineeRequestDTO cannot be null");
        }
        try {
            return traineeService.update(traineeRequestDTO);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error updating Trainee profile", e);
        }
    }
    
    @Override
    public void toggleTraineeStatus(Long traineeId) {
        if (traineeId == null) {
            throw new IllegalArgumentException("Trainee ID cannot be null");
        }
        try {
            traineeService.setTraineeStatus(traineeId);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error setting Trainee status", e);
        }
    }
    
    @Override
    public void deleteTraineeByUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        try {
            traineeService.deleteByUsername(username);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error deleting Trainee profile by username", e);
        }
    }
    
    @Override
    public List<TrainingResponseDTO> findTraineeTrainings(String traineeUsername, TraineeTrainingFilterDTO filterDTO) {
        if (traineeUsername == null || traineeUsername.isEmpty()) {
            throw new IllegalArgumentException("Trainee username cannot be null or empty");
        }
        try {
            return trainingService.findTrainingsByTraineeWithFilters(filterDTO);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error getting Trainee trainings list", e);
        }
    }
    
    @Override
    public List<TrainerResponseDTO> findUnassignedTrainers(String traineeUsername) {
        if (traineeUsername == null || traineeUsername.isEmpty()) {
            throw new IllegalArgumentException("Trainee username cannot be null or empty");
        }
        try {
            return trainerService.findUnassignedTrainersByTraineeUsername(traineeUsername);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error getting Trainers not assigned to Trainee", e);
        }
    }
    
    @Override
    public void updateTraineeTrainers(Long traineeId, Long trainerId, String action) {
        if (traineeId == null || trainerId == null || action == null) {
            throw new IllegalArgumentException("Trainee ID, Trainer ID, and action cannot be null");
        }
        if (!action.equalsIgnoreCase("add") && !action.equalsIgnoreCase("remove")) {
            throw new IllegalArgumentException("Action must be 'add' or 'remove'");
        }
        try {
            if (action.equalsIgnoreCase("add")) {
                traineeService.addTrainerToTrainee(traineeId, trainerId);
            } else {
                traineeService.removeTrainerFromTrainee(traineeId, trainerId);
            }
        } catch (ResourceNotFoundException | ResourceAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error updating Trainee's Trainers list", e);
        }
    }
    
    @Override
    public Set<TrainerResponseDTO> findTraineeTrainers(Long traineeId) {
        if (traineeId == null) {
            throw new IllegalArgumentException("Trainee ID cannot be null");
        }
        try {
            return trainerService.findTrainersByTraineeId(traineeId);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error getting Trainee's Trainers", e);
        }
    }
}