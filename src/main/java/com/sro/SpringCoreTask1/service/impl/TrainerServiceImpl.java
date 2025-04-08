package com.sro.SpringCoreTask1.service.impl;

import com.sro.SpringCoreTask1.dto.request.TrainerRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TrainerResponseDTO;
import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.entity.TrainingType;
import com.sro.SpringCoreTask1.exception.DatabaseOperationException;
import com.sro.SpringCoreTask1.exception.ResourceAlreadyExistsException;
import com.sro.SpringCoreTask1.exception.ResourceNotFoundException;
import com.sro.SpringCoreTask1.mappers.TrainerMapper;
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
    private final TrainerMapper trainerMapper;

    public TrainerServiceImpl(TrainerRepository trainerRepository, TrainingTypeRepository trainingTypeRepository, TrainerMapper trainerMapper) {
        this.trainerRepository = trainerRepository;
        this.trainingTypeRepository = trainingTypeRepository;
        this.trainerMapper = trainerMapper;
    }

    @Override
    @Transactional
    public TrainerResponseDTO save(TrainerRequestDTO trainerRequestDTO) {
        if (trainerRequestDTO == null) {
            throw new IllegalArgumentException("Trainer cannot be null");
        }

        try {
            TrainingType trainingType = trainingTypeRepository.findById(trainerRequestDTO.trainingTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("TrainingType not found with id: " + trainerRequestDTO.trainingTypeId()));
            Trainer trainer = trainerMapper.toEntity(trainerRequestDTO, trainingType);
            trainer.setUsername(ProfileUtil.generateUsername(trainerRequestDTO.firstName(), trainerRequestDTO.lastName()));
            trainer.setPassword(ProfileUtil.generatePassword());
            Trainer savedTrainer = trainerRepository.save(trainer);
            return trainerMapper.toDTO(savedTrainer);
        } catch (ConstraintViolationException e) {
            throw new ResourceAlreadyExistsException("Trainer with username " + trainerRequestDTO.username() + " already exists");
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error saving Trainer", e);
        }
    }

    @Override
    public TrainerResponseDTO findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Trainer id cannot be null");
        }

        try {
            return trainerRepository.findById(id)
                    .map(trainerMapper::toDTO)
                    .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with id: " + id));
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding Trainer by id", e);
        }
    }

    @Override
    public List<TrainerResponseDTO> findAll() {
        try {
            return trainerRepository.findAll().stream()
                    .map(trainerMapper::toDTO)
                    .toList();
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding all Trainers", e);
        }
    }

    @Override
    public TrainerResponseDTO update(TrainerRequestDTO trainerRequestDTO) {
        if (trainerRequestDTO == null) {
            throw new IllegalArgumentException("Trainer cannot be null");
        }

        try {
            TrainingType trainingType = trainingTypeRepository.findById(trainerRequestDTO.trainingTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("TrainingType not found with id: " + trainerRequestDTO.trainingTypeId()));
            Trainer existingTrainer = trainerRepository.findByUsername(trainerRequestDTO.username())
                    .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with username: " + trainerRequestDTO.username()));
            Trainer trainer = trainerMapper.toEntity(trainerRequestDTO, trainingType);
            trainer.setId(existingTrainer.getId());
            trainer.setPassword(existingTrainer.getPassword());
            return trainerRepository.update(trainer)
                    .map(trainerMapper::toDTO)
                    .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with id: " + trainer.getId()));
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error updating Trainer", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Trainer id cannot be null");
        }

        try {
            if (!trainerRepository.deleteById(id)) {
                throw new ResourceNotFoundException("Trainer not found with id: " + id);
            }
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error deleting Trainer by id", e);
        }
    }

    @Override
    public TrainerResponseDTO findByUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Trainer username cannot be null or empty");
        }

        try {
            return trainerRepository.findByUsername(username)
                    .map(trainerMapper::toDTO)
                    .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with username: " + username));
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding Trainer by username", e);
        }
    }

    @Override
    public List<TrainerResponseDTO> findUnassignedTrainersByTraineeUsername(String traineeUsername) {
        if (traineeUsername == null || traineeUsername.isEmpty()) {
            throw new IllegalArgumentException("Trainee username cannot be null or empty");
        }

        try {
            return trainerRepository.findUnassignedTrainersByTraineeUsername(traineeUsername).stream()
                    .map(trainerMapper::toDTO)
                    .toList();
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding Trainers not assigned to Trainee", e);
        }
    }

    @Override
    public void toggleTrainerStatus(Long trainerId) {
        if (trainerId == null) {
            throw new IllegalArgumentException("Trainer id cannot be null");
        }

        try {
            Trainer trainer = trainerRepository.findById(trainerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with id: " + trainerId));

            trainer.setActive(!trainer.isActive());
            this.trainerRepository.save(trainer);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error setting Trainer status", e);
        }
    }

    @Override
    public boolean updateTrainerPassword(Long trainerId, String newPassword) {
        if (trainerId == null || newPassword == null || newPassword.isEmpty()) {
            throw new IllegalArgumentException("Trainer id and new password cannot be null or empty");
        }

        try {
            return trainerRepository.changeTrainerPassword(trainerId, newPassword);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error updating Trainer password", e);
        }
    }

    @Override
    public Set<TrainerResponseDTO> findTrainersByTraineeId(Long traineeId) {
        if (traineeId == null) {
            throw new IllegalArgumentException("Trainee id cannot be null");
        }

        try {
            return trainerRepository.findTrainersByTraineeId(traineeId).stream()
                    .map(trainerMapper::toDTO)
                    .collect(Collectors.toSet());
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding Trainee Trainers", e);
        }
    }
}