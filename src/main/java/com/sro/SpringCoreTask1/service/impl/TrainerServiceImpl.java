package com.sro.SpringCoreTask1.service.impl;

import com.sro.SpringCoreTask1.dto.request.TrainerRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TrainerResponseDTO;
import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.entity.TrainingType;
import com.sro.SpringCoreTask1.exception.DatabaseOperationException;
import com.sro.SpringCoreTask1.exception.ResourceNotFoundException;
import com.sro.SpringCoreTask1.mappers.TrainerMapper;
import com.sro.SpringCoreTask1.repository.TrainerRepository;
import com.sro.SpringCoreTask1.repository.TrainingTypeRepository;
import com.sro.SpringCoreTask1.service.TrainerService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
    public TrainerResponseDTO save(TrainerRequestDTO trainerRequestDTO) {
        try {
            TrainingType trainingType = this.trainingTypeRepository.findById(trainerRequestDTO.trainingTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("TrainingType not found with id: " + trainerRequestDTO.trainingTypeId()));
            Trainer trainer = this.trainerMapper.toEntity(trainerRequestDTO, trainingType);
            Trainer savedTrainer = this.trainerRepository.save(trainer);
            return this.trainerMapper.toDTO(savedTrainer);
        } catch (ResourceNotFoundException e) {
            throw e; 
        } catch (Exception e) {
            throw new DatabaseOperationException("Error saving Trainer", e);
        }
    }

    @Override
    public Optional<TrainerResponseDTO> findById(Long id) {
        try {
            return this.trainerRepository.findById(id).map(this.trainerMapper::toDTO);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding Trainer by id", e);
        }
    }

    @Override
    public List<TrainerResponseDTO> findAll() {
        try {
            return this.trainerRepository.findAll().stream().map(this.trainerMapper::toDTO).toList();
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding all Trainers", e);
        }
    }

    @Override
    public TrainerResponseDTO update(TrainerRequestDTO trainerRequestDTO) {
        try {
            TrainingType trainingType = this.trainingTypeRepository.findById(trainerRequestDTO.trainingTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("TrainingType not found with id: " + trainerRequestDTO.trainingTypeId()));
            Trainer trainer = this.trainerMapper.toEntity(trainerRequestDTO, trainingType);
            Trainer updatedTrainer = this.trainerRepository.update(trainer);
            return this.trainerMapper.toDTO(updatedTrainer);
        } catch (ResourceNotFoundException e) {
            throw e; 
        } catch (Exception e) {
            throw new DatabaseOperationException("Error updating Trainer", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        try {
            this.trainerRepository.deleteById(id);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error deleting Trainer by id", e);
        }
    }

    @Override
    public Optional<TrainerResponseDTO> findByUsername(String username) {
        try {
            return this.trainerRepository.findByUsername(username).map(this.trainerMapper::toDTO);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding Trainer by username", e);
        }
    }

    @Override
    public List<TrainerResponseDTO> getTrainersNotAssignedToTrainee(String traineeUsername) {
        try {
            return this.trainerRepository.findTrainersNotAssignedToTrainee(traineeUsername)
                    .stream()
                    .map(this.trainerMapper::toDTO)
                    .toList();
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding Trainers not assigned to Trainee", e);
        }
    }

    @Override
    public void activateTrainer(Long trainerId) {
        try {
            Trainer trainer = this.trainerRepository.findById(trainerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with id: " + trainerId));
            trainer.setActive(true);
            this.trainerRepository.save(trainer);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error activating Trainer", e);
        }
    }

    @Override
    public void deactivateTrainer(Long trainerId) {
        try {
            Trainer trainer = this.trainerRepository.findById(trainerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with id: " + trainerId));
            trainer.setActive(false);
            this.trainerRepository.save(trainer);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error deactivating Trainer", e);
        }
    }
}