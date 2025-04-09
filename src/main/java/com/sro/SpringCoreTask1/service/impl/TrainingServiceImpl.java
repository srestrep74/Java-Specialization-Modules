package com.sro.SpringCoreTask1.service.impl;

import com.sro.SpringCoreTask1.dto.request.TrainingRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TrainingResponseDTO;
import com.sro.SpringCoreTask1.dtos.v1.request.training.TraineeTrainingFilter;
import com.sro.SpringCoreTask1.dtos.v1.request.training.TraineeTrainingResponse;
import com.sro.SpringCoreTask1.dtos.v1.request.training.TrainerTrainingFilter;
import com.sro.SpringCoreTask1.dtos.v1.request.training.TrainerTrainingResponse;
import com.sro.SpringCoreTask1.entity.Trainee;
import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.entity.Training;
import com.sro.SpringCoreTask1.entity.TrainingType;
import com.sro.SpringCoreTask1.exception.DatabaseOperationException;
import com.sro.SpringCoreTask1.exception.ResourceAlreadyExistsException;
import com.sro.SpringCoreTask1.exception.ResourceNotFoundException;
import com.sro.SpringCoreTask1.mappers.TrainingMapper;
import com.sro.SpringCoreTask1.mappers.training.TrainingTraineeMapper;
import com.sro.SpringCoreTask1.mappers.training.TraininigTrainerMapper;
import com.sro.SpringCoreTask1.repository.TraineeRepository;
import com.sro.SpringCoreTask1.repository.TrainerRepository;
import com.sro.SpringCoreTask1.repository.TrainingRepository;
import com.sro.SpringCoreTask1.repository.TrainingTypeRepository;
import com.sro.SpringCoreTask1.service.TrainingService;

import jakarta.validation.ConstraintViolationException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrainingServiceImpl implements TrainingService {

    private final TrainingRepository trainingRepository;
    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;
    private final TrainingTypeRepository trainingTypeRepository;

    private final TrainingMapper trainingMapper;
    private final TrainingTraineeMapper trainingTraineeMapper;
    private final TraininigTrainerMapper traininigTrainerMapper;

    public TrainingServiceImpl(
            TrainingRepository trainingRepository,
            TrainerRepository trainerRepository,
            TraineeRepository traineeRepository,
            TrainingTypeRepository trainingTypeRepository,
            TrainingMapper trainingMapper,
            TrainingTraineeMapper trainingTraineeMapper,
            TraininigTrainerMapper traininigTrainerMapper) {
        this.trainingRepository = trainingRepository;
        this.trainerRepository = trainerRepository;
        this.traineeRepository = traineeRepository;
        this.trainingTypeRepository = trainingTypeRepository;
        this.trainingMapper = trainingMapper;
        this.trainingTraineeMapper = trainingTraineeMapper;
        this.traininigTrainerMapper = traininigTrainerMapper;
    }

    @Override
    @Transactional
    public TrainingResponseDTO save(TrainingRequestDTO trainingRequestDTO) {
        if (trainingRequestDTO == null) {
            throw new IllegalArgumentException("TrainingRequestDTO cannot be null");
        }

        try {
            Trainee trainee = traineeRepository.findById(trainingRequestDTO.traineeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Trainee not found with id: " + trainingRequestDTO.traineeId()));
            Trainer trainer = trainerRepository.findById(trainingRequestDTO.trainerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with id: " + trainingRequestDTO.trainerId()));

            if (!trainer.isActive()) {
                throw new ResourceNotFoundException("Trainer with id: " + trainer.getId() + " is not active");
            }

            if (!trainee.isActive()) {
                throw new ResourceNotFoundException("Trainee with id: " + trainee.getId() + " is not active");
            }

            if (!trainee.getTrainers().contains(trainer)) {
                throw new IllegalArgumentException("Trainer not assigned to Trainee");
            }

            boolean isDuplicateTraining = trainingRepository.existsByTraineeIdAndTrainerAndTrainingDate(trainee, trainer, trainingRequestDTO.trainingDate());
            if (isDuplicateTraining) {
                throw new ResourceAlreadyExistsException("A training with the same trainer and date already exists.");
            }

            TrainingType trainingType = trainingTypeRepository.findById(trainingRequestDTO.trainingTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("TrainingType not found with id: " + trainingRequestDTO.trainingTypeId()));

            Training training = trainingMapper.toEntity(trainingRequestDTO, trainee, trainer, trainingType);
            Training savedTraining = trainingRepository.save(training);

            return trainingMapper.toDTO(savedTraining);
        } catch (ConstraintViolationException e) {
            throw new ResourceAlreadyExistsException("Training with name " + trainingRequestDTO.trainingName() + " already exists");
        } catch (Exception e) {
            throw new DatabaseOperationException("Error saving Training", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public TrainingResponseDTO findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Training id cannot be null");
        }

        try {
            return trainingRepository.findById(id)
                    .map(trainingMapper::toDTO)
                    .orElseThrow(() -> new ResourceNotFoundException("Training not found with id: " + id));
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding Training by id", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingResponseDTO> findAll() {
        try {
            return trainingRepository.findAll().stream()
                    .map(trainingMapper::toDTO)
                    .toList();
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding all Trainings", e);
        }
    }

    @Override
    @Transactional
    public TrainingResponseDTO update(TrainingRequestDTO trainingRequestDTO) {
        if (trainingRequestDTO == null) {
            throw new IllegalArgumentException("TrainingRequestDTO cannot be null");
        }

        try {
            Trainee trainee = traineeRepository.findById(trainingRequestDTO.traineeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Trainee not found with id: " + trainingRequestDTO.traineeId()));
            Trainer trainer = trainerRepository.findById(trainingRequestDTO.trainerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with id: " + trainingRequestDTO.trainerId()));
            TrainingType trainingType = trainingTypeRepository.findById(trainingRequestDTO.trainingTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("TrainingType not found with id: " + trainingRequestDTO.trainingTypeId()));

            Training training = trainingMapper.toEntity(trainingRequestDTO, trainee, trainer, trainingType);
            return trainingRepository.update(training)
                    .map(trainingMapper::toDTO)
                    .orElseThrow(() -> new ResourceNotFoundException("Training not found with id: " + training.getId()));
        } catch (Exception e) {
            throw new DatabaseOperationException("Error updating Training", e);
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Training id cannot be null");
        }

        try {
            if (!trainingRepository.deleteById(id)) {
                throw new ResourceNotFoundException("Training not found with id: " + id);
            }
        } catch (Exception e) {
            throw new DatabaseOperationException("Error deleting Training by id", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<TraineeTrainingResponse> findTrainingsByTraineeWithFilters(TraineeTrainingFilter filterDTO) {
        if (filterDTO == null) {
            throw new IllegalArgumentException("TraineeTrainingFilterDTO cannot be null");
        }

        try {
            return trainingRepository.findTrainingsByTraineeWithFilters(filterDTO).stream()
                    .map(trainingTraineeMapper::toTraineeTrainingResponse)
                    .toList();
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding Trainings by Trainee with filters", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainerTrainingResponse> findTrainingsByTrainerWithFilters(TrainerTrainingFilter filterDTO) {
        if (filterDTO == null) {
            throw new IllegalArgumentException("TrainerTrainingFilterDTO cannot be null");
        }

        try {
            return trainingRepository.findTrainingsByTrainerWithFilters(filterDTO).stream()
                    .map(traininigTrainerMapper::toTrainerTrainingResponse)
                    .toList();
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding Trainings by Trainer with filters", e);
        }
    }
}