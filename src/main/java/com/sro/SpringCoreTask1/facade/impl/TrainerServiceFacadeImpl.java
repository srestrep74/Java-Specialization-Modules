package com.sro.SpringCoreTask1.facade.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.sro.SpringCoreTask1.dto.TrainerTrainingFilterDTO;
import com.sro.SpringCoreTask1.dto.request.TrainerRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TrainerResponseDTO;
import com.sro.SpringCoreTask1.dto.response.TrainingResponseDTO;
import com.sro.SpringCoreTask1.exception.DatabaseOperationException;
import com.sro.SpringCoreTask1.exception.ResourceAlreadyExistsException;
import com.sro.SpringCoreTask1.exception.ResourceNotFoundException;
import com.sro.SpringCoreTask1.facade.TrainerServiceFacade;
import com.sro.SpringCoreTask1.service.TrainerService;
import com.sro.SpringCoreTask1.service.TrainingService;

@Component
public class TrainerServiceFacadeImpl implements TrainerServiceFacade{
    
    private final TrainerService trainerService;
    private final TrainingService trainingService;
    
    public TrainerServiceFacadeImpl(
            TrainerService trainerService,
            TrainingService trainingService) {
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }
    
    @Override
    public TrainerResponseDTO createTrainer(TrainerRequestDTO trainerRequestDTO) {
        if (trainerRequestDTO == null) {
            throw new IllegalArgumentException("TrainerRequestDTO cannot be null");
        }
        try {
            return trainerService.save(trainerRequestDTO);
        } catch (ResourceAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error creating Trainer profile", e);
        }

    }
    
    @Override
    public TrainerResponseDTO findTrainerByUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        try {
            return trainerService.findByUsername(username);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding Trainer by username", e);
        }
    }
    
    @Override
    public TrainerResponseDTO findTrainerById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Trainer ID cannot be null");
        }
        try {
            return trainerService.findById(id);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding Trainer by ID", e);
        }
    }
    
    @Override
    public boolean updateTrainerPassword(Long trainerId, String newPassword) {
        if (trainerId == null || newPassword == null || newPassword.isEmpty()) {
            throw new IllegalArgumentException("Trainer ID and new password cannot be null or empty");
        }
        try {
            return trainerService.updateTrainerPassword(trainerId, newPassword);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error updating Trainer password", e);
        }
    }
    
    @Override
    public TrainerResponseDTO updateTrainer(TrainerRequestDTO trainerRequestDTO) {
        if (trainerRequestDTO == null) {
            throw new IllegalArgumentException("TrainerRequestDTO cannot be null");
        }
        try {
            return trainerService.update(trainerRequestDTO);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error updating Trainer profile", e);
        }
    }
    
    @Override
    public void toggleTrainerStatus(Long trainerId) {
        if (trainerId == null) {
            throw new IllegalArgumentException("Trainer ID cannot be null");
        }
        try {
            trainerService.toggleTrainerStatus(trainerId);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error setting Trainer status", e);
        }
    }
    
    @Override
    public List<TrainingResponseDTO> findTrainerTrainings(String trainerUsername, TrainerTrainingFilterDTO filterDTO) {
        if (trainerUsername == null || trainerUsername.isEmpty()) {
            throw new IllegalArgumentException("Trainer username cannot be null or empty");
        }
        try {
            return trainingService.findTrainingsByTrainerWithFilters(filterDTO);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error getting Trainer trainings list", e);
        }
    }
}