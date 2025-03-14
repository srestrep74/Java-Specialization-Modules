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
import com.sro.SpringCoreTask1.exception.ResourceNotFoundException;
import com.sro.SpringCoreTask1.mappers.TrainingMapper;
import com.sro.SpringCoreTask1.repository.TraineeRepository;
import com.sro.SpringCoreTask1.repository.TrainerRepository;
import com.sro.SpringCoreTask1.repository.TrainingRepository;
import com.sro.SpringCoreTask1.repository.TrainingTypeRepository;
import com.sro.SpringCoreTask1.service.TrainingService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        try {
            Trainee trainee = this.traineeRepository.findById(trainingRequestDTO.traineeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Trainee not found with id: " + trainingRequestDTO.traineeId()));
            Trainer trainer = this.trainerRepository.findById(trainingRequestDTO.trainerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with id: " + trainingRequestDTO.trainerId()));
            TrainingType trainingType = this.trainingTypeRepository.findById(trainingRequestDTO.trainingTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("TrainingType not found with id: " + trainingRequestDTO.trainingTypeId()));

            Training training = this.trainingMapper.toEntity(trainingRequestDTO, trainee, trainer, trainingType);
            Training savedTraining = this.trainingRepository.save(training);
            return this.trainingMapper.toDTO(savedTraining);
        } catch (ResourceNotFoundException e) {
            throw e; 
        } catch (Exception e) {
            throw new DatabaseOperationException("Error saving Training", e);
        }
    }

    @Override
    public Optional<TrainingResponseDTO> findById(Long id) {
        try {
            return this.trainingRepository.findById(id).map(this.trainingMapper::toDTO);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding Training by id", e);
        }
    }

    @Override
    public List<TrainingResponseDTO> findAll() {
        try {
            return this.trainingRepository.findAll().stream().map(this.trainingMapper::toDTO).toList();
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding all Trainings", e);
        }
    }

    @Override
    public TrainingResponseDTO update(TrainingRequestDTO trainingRequestDTO) {
        try {
            Trainee trainee = this.traineeRepository.findById(trainingRequestDTO.traineeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Trainee not found with id: " + trainingRequestDTO.traineeId()));
            Trainer trainer = this.trainerRepository.findById(trainingRequestDTO.trainerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with id: " + trainingRequestDTO.trainerId()));
            TrainingType trainingType = this.trainingTypeRepository.findById(trainingRequestDTO.trainingTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("TrainingType not found with id: " + trainingRequestDTO.trainingTypeId()));

            Training training = this.trainingMapper.toEntity(trainingRequestDTO, trainee, trainer, trainingType);
            Training updatedTraining = this.trainingRepository.update(training);
            return this.trainingMapper.toDTO(updatedTraining);
        } catch (ResourceNotFoundException e) {
            throw e; 
        } catch (Exception e) {
            throw new DatabaseOperationException("Error updating Training", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        try {
            this.trainingRepository.deleteById(id);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error deleting Training by id", e);
        }
    }

    @Override
    public List<TrainingResponseDTO> findTrainingsByTraineeWithFilters(TraineeTrainingFilterDTO filterDTO) {
        if (filterDTO == null) {
            throw new IllegalArgumentException("The filter can't be null");
        }

        try {
            return this.trainingRepository.findTrainingsByTraineeWithFilters(filterDTO).stream().map(this.trainingMapper::toDTO).toList();
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding Trainings by Trainee with filters", e);
        }
    }

    @Override
    public List<TrainingResponseDTO> findTrainingsByTrainerWithFilters(TrainerTrainingFilterDTO filterDTO) {
        if (filterDTO == null) {
            throw new IllegalArgumentException("The filter can't be null");
        }

        try {
            return this.trainingRepository.findTrainingsByTrainerWithFilters(filterDTO).stream().map(this.trainingMapper::toDTO).toList();
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding Trainings by Trainer with filters", e);
        }
    }
}