package dev.sro.gym_service.service.impl;

import dev.sro.gym_service.dtos.v1.request.training.CreateTrainingRequest;
import dev.sro.gym_service.dtos.v1.request.training.DeleteTrainingRequest;
import dev.sro.gym_service.dtos.v1.request.training.TraineeTrainingFilter;
import dev.sro.gym_service.dtos.v1.request.training.TraineeTrainingResponse;
import dev.sro.gym_service.dtos.v1.request.training.TrainerTrainingFilter;
import dev.sro.gym_service.dtos.v1.request.training.TrainerTrainingResponse;
import dev.sro.gym_service.dtos.v1.request.training.UpdateTrainingRequest;
import dev.sro.gym_service.dtos.v1.response.training.TrainingSummaryResponse;
import dev.sro.gym_service.entity.Trainee;
import dev.sro.gym_service.entity.Trainer;
import dev.sro.gym_service.entity.Training;
import dev.sro.gym_service.exception.DatabaseOperationException;
import dev.sro.gym_service.exception.ResourceNotFoundException;
import dev.sro.gym_service.mappers.training.TrainingCreateMapper;
import dev.sro.gym_service.mappers.training.TrainingResponseMapper;
import dev.sro.gym_service.mappers.training.TrainingTraineeMapper;
import dev.sro.gym_service.mappers.training.TrainingUpdateMapper;
import dev.sro.gym_service.mappers.training.TraininigTrainerMapper;
import dev.sro.gym_service.metrics.TraineeTrainingMetrics;
import dev.sro.gym_service.metrics.TrainerTrainingMetrics;
import dev.sro.gym_service.metrics.TrainingMetrics;
import dev.sro.gym_service.repository.TraineeRepository;
import dev.sro.gym_service.repository.TrainerRepository;
import dev.sro.gym_service.repository.TrainingRepository;
import dev.sro.gym_service.repository.specification.TrainingSpecifications;
import dev.sro.gym_service.service.TrainingService;
import dev.sro.gym_service.service.WorkloadNotificationService;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrainingServiceImpl implements TrainingService {

    private final TrainingRepository trainingRepository;
    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;
    private final TrainingMetrics trainingMetrics;
    private final TraineeTrainingMetrics traineeTrainingMetrics;
    private final TrainerTrainingMetrics trainerTrainingMetrics;
    private final WorkloadNotificationService workloadNotificationService;

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
            TrainingUpdateMapper trainingUpdateMapper,
            TrainingMetrics trainingMetrics,
            TraineeTrainingMetrics traineeTrainingMetrics,
            TrainerTrainingMetrics trainerTrainingMetrics,
            WorkloadNotificationService workloadNotificationService) {
        this.trainingRepository = trainingRepository;
        this.trainerRepository = trainerRepository;
        this.traineeRepository = traineeRepository;
        this.trainingTraineeMapper = trainingTraineeMapper;
        this.traininigTrainerMapper = traininigTrainerMapper;
        this.trainingCreateMapper = trainingCreateMapper;
        this.trainingResponseMapper = trainingResponseMapper;
        this.trainingUpdateMapper = trainingUpdateMapper;
        this.trainingMetrics = trainingMetrics;
        this.traineeTrainingMetrics = traineeTrainingMetrics;
        this.trainerTrainingMetrics = trainerTrainingMetrics;
        this.workloadNotificationService = workloadNotificationService;
    }

    @Override
    @Transactional
    public void save(CreateTrainingRequest createTrainingRequest) {
        if (createTrainingRequest == null) {
            throw new IllegalArgumentException("CreateTrainingRequest cannot be null");
        }

        try {
            Trainee trainee = traineeRepository.findByUsername(createTrainingRequest.traineeUsername())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Trainee not found with username: " + createTrainingRequest.traineeUsername()));
            Trainer trainer = trainerRepository.findByUsername(createTrainingRequest.trainerUsername())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Trainer not found with username: " + createTrainingRequest.trainerUsername()));

            Training training = trainingCreateMapper.toEntity(createTrainingRequest, trainer, trainee,
                    trainer.getTrainingType());
            Training savedTraining = trainingRepository.save(training);

            trainingMetrics.recordNewTraining();
            trainingMetrics.recordTrainingDuration(training.getDuration());

            traineeTrainingMetrics.recordTraineeSession();
            traineeTrainingMetrics.recordTraineeTrainingDuration(training.getDuration());
            
            trainerTrainingMetrics.recordTrainerSession();
            trainerTrainingMetrics.recordTrainerTrainingDuration(training.getDuration());
            
            // Notify workload service about new training
            workloadNotificationService.notifyTrainingCreated(savedTraining);
        } catch (ResourceNotFoundException e) {
            throw e;
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
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Trainee not found with username: " + updateTrainingRequest.traineeUsername()));
            Trainer trainer = trainerRepository.findByUsername(updateTrainingRequest.trainerUsername())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Trainer not found with username: " + updateTrainingRequest.trainerUsername()));

            Training training = trainingUpdateMapper.toEntity(updateTrainingRequest, trainer, trainee,
                    trainer.getTrainingType());
            Training savedTraining = trainingRepository.save(training);

            trainingMetrics.recordTrainingDuration(savedTraining.getDuration());
            traineeTrainingMetrics.recordTraineeTrainingDuration(savedTraining.getDuration());
            trainerTrainingMetrics.recordTrainerTrainingDuration(savedTraining.getDuration());

            // Notify workload service about training update (treated as new training)
            workloadNotificationService.notifyTrainingCreated(savedTraining);

            return trainingResponseMapper.toTrainingSummaryResponse(savedTraining);
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
            // Get the training before deleting to notify workload service
            Training training = trainingRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Training not found with id: " + id));
            
            trainingRepository.deleteById(id);
            
            // Notify workload service about training deletion
            workloadNotificationService.notifyTrainingDeleted(training);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error deleting Training by id", e);
        }
    }

    @Override
    @Transactional
    public void deleteTraining(DeleteTrainingRequest deleteTrainingRequest) {
        if (deleteTrainingRequest == null) {
            throw new IllegalArgumentException("DeleteTrainingRequest cannot be null");
        }

        try {
            Trainee trainee = traineeRepository.findByUsername(deleteTrainingRequest.traineeUsername())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Trainee not found with username: " + deleteTrainingRequest.traineeUsername()));
            Trainer trainer = trainerRepository.findByUsername(deleteTrainingRequest.trainerUsername())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Trainer not found with username: " + deleteTrainingRequest.trainerUsername()));

            Training training = trainingRepository.findByTraineeAndTrainerAndTrainingDate(trainee, trainer, deleteTrainingRequest.trainingDate())
                    .orElseThrow(() -> new ResourceNotFoundException("Training not found"));

            trainingRepository.delete(training);

            workloadNotificationService.notifyTrainingDeleted(training);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error deleting Training", e);
        }
    }


    @Override
    @Transactional(readOnly = true)
    public List<TraineeTrainingResponse> findTrainingsByTraineeWithFilters(TraineeTrainingFilter filterDTO,
            String sortField, String sortDirection) {
        if (filterDTO == null) {
            throw new IllegalArgumentException("TraineeTrainingFilterDTO cannot be null");
        }

        try {
            Specification<Training> spec = Specification
                    .where(TrainingSpecifications.withTraineeAndTrainerFetched())
                    .and(TrainingSpecifications.hasTraineeUsername(filterDTO.username()))
                    .and(TrainingSpecifications.dateAfterOrEqual(filterDTO.fromDate()))
                    .and(TrainingSpecifications.dateBeforeOrEqual(filterDTO.toDate()))
                    .and(TrainingSpecifications.trainerUsernameContains(filterDTO.trainerName()))
                    .and(TrainingSpecifications.hasTrainingType(filterDTO.trainingType()));

            Sort sort = Sort.by(
                    "ASC".equalsIgnoreCase(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC,
                    sortField != null ? sortField : "trainingDate");

            List<Training> trainings = trainingRepository.findAll(spec, sort);

            return trainings.stream()
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
            Specification<Training> spec = Specification
                    .where(TrainingSpecifications.withTraineeAndTrainerFetched())
                    .and(TrainingSpecifications.hasTrainerUsername(filterDTO.username()))
                    .and(TrainingSpecifications.dateAfterOrEqual(filterDTO.fromDate()))
                    .and(TrainingSpecifications.dateBeforeOrEqual(filterDTO.toDate()))
                    .and(TrainingSpecifications.traineeUsernameContains(filterDTO.traineeName()));

            List<Training> trainings = trainingRepository.findAll(spec);

            return trainings.stream()
                    .map(traininigTrainerMapper::toTrainerTrainingResponse)
                    .toList();
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding Trainings by Trainer with filters", e);
        }
    }
}