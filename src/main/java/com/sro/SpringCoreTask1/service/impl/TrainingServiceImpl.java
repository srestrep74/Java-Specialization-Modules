package com.sro.SpringCoreTask1.service.impl;

import com.sro.SpringCoreTask1.dto.TraineeTrainingFilterDTO;
import com.sro.SpringCoreTask1.dto.TrainerTrainingFilterDTO;
import com.sro.SpringCoreTask1.dto.request.TrainingRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TrainingResponseDTO;
import com.sro.SpringCoreTask1.entity.Trainee;
import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.entity.Training;
import com.sro.SpringCoreTask1.entity.TrainingType;
import com.sro.SpringCoreTask1.exception.DatabaseOperationException;
import com.sro.SpringCoreTask1.exception.ResourceAlreadyExistsException;
import com.sro.SpringCoreTask1.exception.ResourceNotFoundException;
import com.sro.SpringCoreTask1.mappers.TrainingMapper;
import com.sro.SpringCoreTask1.repository.TraineeRepository;
import com.sro.SpringCoreTask1.repository.TrainerRepository;
import com.sro.SpringCoreTask1.repository.TrainingRepository;
import com.sro.SpringCoreTask1.repository.TrainingTypeRepository;
import com.sro.SpringCoreTask1.service.TrainingService;

import jakarta.validation.ConstraintViolationException;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainingServiceImpl implements TrainingService {

    private final TrainingRepository trainingRepository;
    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainingMapper trainingMapper;

    public TrainingServiceImpl(TrainingRepository trainingRepository, TrainerRepository trainerRepository, TraineeRepository traineeRepository, TrainingTypeRepository trainingTypeRepository, TrainingMapper trainingMapper) {
        this.trainingRepository = trainingRepository;
        this.trainerRepository = trainerRepository;
        this.traineeRepository = traineeRepository;
        this.trainingTypeRepository = trainingTypeRepository;
        this.trainingMapper = trainingMapper;
    }

    @Override
    public TrainingResponseDTO save(TrainingRequestDTO trainingRequestDTO) {
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

            trainer.addTrainee(trainee);

            Training training = trainingMapper.toEntity(trainingRequestDTO, trainee, trainer, trainingType);
            Training savedTraining = trainingRepository.save(training);

            return trainingMapper.toDTO(savedTraining);
        }catch (ConstraintViolationException e) {
            throw new ResourceAlreadyExistsException("Training with name " + trainingRequestDTO.trainingName() + " already exists");
        }catch (Exception e) {
            throw new DatabaseOperationException("Error saving Training", e);
        }
    }

    @Override
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
    public List<TrainingResponseDTO> findTrainingsByTraineeWithFilters(TraineeTrainingFilterDTO filterDTO) {
        if (filterDTO == null) {
            throw new IllegalArgumentException("TraineeTrainingFilterDTO cannot be null");
        }

        try {
            return trainingRepository.findTrainingsByTraineeWithFilters(filterDTO).stream()
                    .map(trainingMapper::toDTO)
                    .toList();
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding Trainings by Trainee with filters", e);
        }
    }

    @Override
    public List<TrainingResponseDTO> findTrainingsByTrainerWithFilters(TrainerTrainingFilterDTO filterDTO) {
        if (filterDTO == null) {
            throw new IllegalArgumentException("TrainerTrainingFilterDTO cannot be null");
        }

        try {
            return trainingRepository.findTrainingsByTrainerWithFilters(filterDTO).stream()
                    .map(trainingMapper::toDTO)
                    .toList();
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding Trainings by Trainer with filters", e);
        }
    }
}