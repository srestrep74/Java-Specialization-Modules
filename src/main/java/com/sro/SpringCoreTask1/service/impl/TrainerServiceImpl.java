package com.sro.SpringCoreTask1.service.impl;

import com.sro.SpringCoreTask1.dtos.v1.request.trainer.RegisterTrainerRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.trainer.UpdateTrainerProfileRequest;
import com.sro.SpringCoreTask1.dtos.v1.response.trainer.RegisterTrainerResponse;
import com.sro.SpringCoreTask1.dtos.v1.response.trainer.TrainerProfileResponse;
import com.sro.SpringCoreTask1.dtos.v1.response.trainer.UnassignedTrainerResponse;
import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.entity.TrainingType;
import com.sro.SpringCoreTask1.exception.DatabaseOperationException;
import com.sro.SpringCoreTask1.exception.ResourceAlreadyExistsException;
import com.sro.SpringCoreTask1.exception.ResourceNotFoundException;
import com.sro.SpringCoreTask1.mappers.trainer.TrainerCreateMapper;
import com.sro.SpringCoreTask1.mappers.trainer.TrainerResponseMapper;
import com.sro.SpringCoreTask1.mappers.trainer.TrainerUpdateMapper;
import com.sro.SpringCoreTask1.repository.TrainerRepository;
import com.sro.SpringCoreTask1.repository.TrainingTypeRepository;
import com.sro.SpringCoreTask1.service.TrainerService;
import com.sro.SpringCoreTask1.util.ProfileUtil;

import jakarta.validation.ConstraintViolationException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TrainerServiceImpl implements TrainerService {

    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;

    private final TrainerCreateMapper trainerCreateMapper;
    private final TrainerUpdateMapper trainerUpdateMapper;
    private final TrainerResponseMapper trainerResponseMapper;

    public TrainerServiceImpl(TrainerRepository trainerRepository, TrainingTypeRepository trainingTypeRepository, TrainerCreateMapper trainerCreateMapper, TrainerUpdateMapper trainerUpdateMapper, TrainerResponseMapper trainerResponseMapper) {
        this.trainerRepository = trainerRepository;
        this.trainingTypeRepository = trainingTypeRepository;
        this.trainerCreateMapper = trainerCreateMapper;
        this.trainerUpdateMapper = trainerUpdateMapper;
        this.trainerResponseMapper = trainerResponseMapper;
    }

    @Override
    @Transactional
    public RegisterTrainerResponse save(RegisterTrainerRequest trainerRequestDTO) {
        if (trainerRequestDTO == null) {
            throw new IllegalArgumentException("Trainer cannot be null");
        }

        if (trainerRequestDTO.firstName() == null || trainerRequestDTO.firstName().isEmpty() ||
            trainerRequestDTO.lastName() == null || trainerRequestDTO.lastName().isEmpty() ||
            trainerRequestDTO.specialization() == null) {
            throw new IllegalArgumentException("Trainer first name, last name, and specialization cannot be null or empty");
        }
        
        String generatedUsername = ProfileUtil.generateUsername(trainerRequestDTO.firstName(), trainerRequestDTO.lastName());
        String generatedPassword = ProfileUtil.generatePassword();

        try {
            TrainingType trainingType = trainingTypeRepository.findById(trainerRequestDTO.specialization())
                    .orElseThrow(() -> new ResourceNotFoundException("TrainingType not found with id: " + trainerRequestDTO.specialization()));
            Trainer trainer = trainerCreateMapper.toEntity(trainerRequestDTO, trainingType);
            trainer.setUsername(generatedUsername);
            trainer.setPassword(generatedPassword);
            Trainer savedTrainer = trainerRepository.save(trainer);

            return trainerCreateMapper.toRegisterResponse(savedTrainer);
        } catch(ResourceNotFoundException e) {
            throw e;
        } catch (ConstraintViolationException e) {
            throw new ResourceAlreadyExistsException("Trainer with username " + generatedUsername + " already exists");
        } catch (Exception e) {
            throw new DatabaseOperationException("Error saving Trainer", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public TrainerProfileResponse findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Trainer id cannot be null");
        }

        try {
            return trainerRepository.findById(id)
                    .map(trainerResponseMapper::toTrainerProfileResponse)
                    .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with id: " + id));
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding Trainer by id", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainerProfileResponse> findAll() {
        try {
            return trainerRepository.findAll().stream()
                    .map(trainerResponseMapper::toTrainerProfileResponse)
                    .toList();
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding all Trainers", e);
        }
    }

    @Override
    @Transactional
    public TrainerProfileResponse update(String username, UpdateTrainerProfileRequest trainerRequestDTO) {
        if (trainerRequestDTO == null) {
            throw new IllegalArgumentException("Trainer cannot be null");
        }

        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Trainer username cannot be null or empty");
        }

        try {
            TrainingType trainingType = trainingTypeRepository.findById(trainerRequestDTO.specialization())
                    .orElseThrow(() -> new ResourceNotFoundException("TrainingType not found with id: " + trainerRequestDTO.specialization()));
            Trainer existingTrainer = trainerRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with username: " + username));
            Trainer trainer = trainerUpdateMapper.toEntity(trainerRequestDTO, existingTrainer, trainingType);

            trainer.setPassword(existingTrainer.getPassword());
            return trainerResponseMapper.toTrainerProfileResponse(trainerRepository.save(trainer));
        } catch(ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error updating Trainer", e);
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Trainer id cannot be null");
        }

        try {
            if (!trainerRepository.deleteById(id)) {
                throw new ResourceNotFoundException("Trainer not found with id: " + id);
            }
        } catch(ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error deleting Trainer by id", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public TrainerProfileResponse findByUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Trainer username cannot be null or empty");
        }

        try {
            return trainerResponseMapper.toTrainerProfileResponse(trainerRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with username: " + username)));
        } catch(ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding Trainer by username", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UnassignedTrainerResponse> findUnassignedTrainersByTraineeUsername(String traineeUsername) {
        if (traineeUsername == null || traineeUsername.isEmpty()) {
            throw new IllegalArgumentException("Trainee username cannot be null or empty");
        }

        try {
            return trainerRepository.findUnassignedTrainersByTraineeUsername(traineeUsername).stream()
                    .map(trainerResponseMapper::toUnassignedTrainerResponse)
                    .toList();
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding Trainers not assigned to Trainee", e);
        }
    }

    @Override
    @Transactional
    public void updateActivationStatus(String username, boolean active) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Trainer username cannot be null or empty");
        }

        try {
            Trainer trainer = trainerRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with username: " + username));

            trainer.setActive(active);
            trainerRepository.save(trainer);
        } catch(ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error updating Trainer activation status", e);
        }
    }

    @Override
    @Transactional
    public boolean updateTrainerPassword(Long trainerId, String newPassword) {
        if (trainerId == null || newPassword == null || newPassword.isEmpty()) {
            throw new IllegalArgumentException("Trainer id and new password cannot be null or empty");
        }

        try {
            return trainerRepository.changeTrainerPassword(trainerId, newPassword);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error updating Trainer password", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Set<TrainerProfileResponse> findTrainersByTraineeId(Long traineeId) {
        if (traineeId == null) {
            throw new IllegalArgumentException("Trainee id cannot be null");
        }

        try {
            return trainerRepository.findTrainersByTraineeId(traineeId).stream()
                    .map(trainerResponseMapper::toTrainerProfileResponse)
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding Trainee Trainers", e);
        }
    }
}