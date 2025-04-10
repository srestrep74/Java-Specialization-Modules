package com.sro.SpringCoreTask1.service.impl;

import com.sro.SpringCoreTask1.dtos.v1.request.training.CreateTrainingRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.training.TraineeTrainingFilter;
import com.sro.SpringCoreTask1.dtos.v1.request.training.TraineeTrainingResponse;
import com.sro.SpringCoreTask1.dtos.v1.request.training.TrainerTrainingFilter;
import com.sro.SpringCoreTask1.dtos.v1.request.training.TrainerTrainingResponse;
import com.sro.SpringCoreTask1.dtos.v1.request.training.UpdateTrainingRequest;
import com.sro.SpringCoreTask1.dtos.v1.response.training.TrainingSummaryResponse;
import com.sro.SpringCoreTask1.entity.Trainee;
import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.entity.Training;
import com.sro.SpringCoreTask1.exception.DatabaseOperationException;
import com.sro.SpringCoreTask1.exception.ResourceNotFoundException;
import com.sro.SpringCoreTask1.mappers.training.TrainingCreateMapper;
import com.sro.SpringCoreTask1.mappers.training.TrainingResponseMapper;
import com.sro.SpringCoreTask1.mappers.training.TrainingTraineeMapper;
import com.sro.SpringCoreTask1.mappers.training.TrainingUpdateMapper;
import com.sro.SpringCoreTask1.mappers.training.TraininigTrainerMapper;
import com.sro.SpringCoreTask1.repository.TraineeRepository;
import com.sro.SpringCoreTask1.repository.TrainerRepository;
import com.sro.SpringCoreTask1.repository.TrainingRepository;
import com.sro.SpringCoreTask1.service.TrainingService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrainingServiceImpl implements TrainingService {

    private final TrainingRepository trainingRepository;
    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;

    private final TrainingTraineeMapper trainingTraineeMapper;
    private final TraininigTrainerMapper traininigTrainerMapper;
    private final TrainingCreateMapper trainingCreateMapper;
    private final TrainingResponseMapper trainingResponseMapper;
    private final TrainingUpdateMapper trainingUpdateMapper;

    public TrainingServiceImpl(
            TrainingRepository trainingRepository,
            TrainerRepository trainerRepository,
            TraineeRepository traineeRepository,
            TrainingTraineeMapper trainingTraineeMapper,
            TraininigTrainerMapper traininigTrainerMapper,
            TrainingCreateMapper trainingCreateMapper,
            TrainingResponseMapper trainingResponseMapper,
            TrainingUpdateMapper trainingUpdateMapper) {
        this.trainingRepository = trainingRepository;
        this.trainerRepository = trainerRepository;
        this.traineeRepository = traineeRepository;
        this.trainingTraineeMapper = trainingTraineeMapper;
        this.traininigTrainerMapper = traininigTrainerMapper;
        this.trainingCreateMapper = trainingCreateMapper;
        this.trainingResponseMapper = trainingResponseMapper;
        this.trainingUpdateMapper = trainingUpdateMapper;
    }

    @Override
    @Transactional
    public void save(CreateTrainingRequest createTrainingRequest) {
        if (createTrainingRequest == null) {
            throw new IllegalArgumentException("CreateTrainingRequest cannot be null");
        }

        try {
            Trainee trainee = traineeRepository.findByUsername(createTrainingRequest.traineeUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("Trainee not found with username: " + createTrainingRequest.traineeUsername()));
            Trainer trainer = trainerRepository.findByUsername(createTrainingRequest.trainerUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with username: " + createTrainingRequest.trainerUsername()));


            Training training = trainingCreateMapper.toEntity(createTrainingRequest, trainer, trainee, trainer.getTrainingType());
            trainingRepository.save(training);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error adding Training", e);
        }
        
        
    }

    @Override
    @Transactional(readOnly = true)
    public TrainingSummaryResponse findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Training id cannot be null");
        }

        try {
            return trainingRepository.findById(id)
                    .map(trainingResponseMapper::toTrainingSummaryResponse)
                    .orElseThrow(() -> new ResourceNotFoundException("Training not found with id: " + id));
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding Training by id", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingSummaryResponse> findAll() {
        try {
            return trainingRepository.findAll().stream()
                    .map(trainingResponseMapper::toTrainingSummaryResponse)
                    .toList();
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding all Trainings", e);
        }
    }

    @Override
    @Transactional
    public TrainingSummaryResponse update(UpdateTrainingRequest updateTrainingRequest) {
        if (updateTrainingRequest == null) {
            throw new IllegalArgumentException("UpdateTrainingRequest cannot be null");
        }

        try {
            Trainee trainee = traineeRepository.findByUsername(updateTrainingRequest.traineeUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("Trainee not found with username: " + updateTrainingRequest.traineeUsername()));
            Trainer trainer = trainerRepository.findByUsername(updateTrainingRequest.trainerUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with username: " + updateTrainingRequest.trainerUsername()));

            Training training = trainingUpdateMapper.toEntity(updateTrainingRequest, trainer, trainee, trainer.getTrainingType());
            return trainingRepository.update(training)
                    .map(trainingResponseMapper::toTrainingSummaryResponse)
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