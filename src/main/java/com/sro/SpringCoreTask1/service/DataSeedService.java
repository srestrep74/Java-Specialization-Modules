package com.sro.SpringCoreTask1.service;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sro.SpringCoreTask1.dtos.v1.request.seed.TraineeSeedRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.seed.TrainerSeedRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.seed.TrainingSeedRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.seed.TrainingTypeSeedRequest;
import com.sro.SpringCoreTask1.entity.Trainee;
import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.entity.Training;
import com.sro.SpringCoreTask1.entity.TrainingType;
import com.sro.SpringCoreTask1.exception.DatabaseOperationException;
import com.sro.SpringCoreTask1.exception.ResourceAlreadyExistsException;
import com.sro.SpringCoreTask1.exception.ResourceNotFoundException;
import com.sro.SpringCoreTask1.mappers.seed.TraineeSeedMapper;
import com.sro.SpringCoreTask1.mappers.seed.TrainerSeedMapper;
import com.sro.SpringCoreTask1.mappers.seed.TrainingSeedMapper;
import com.sro.SpringCoreTask1.mappers.seed.TrainingTypeSeedMapper;
import com.sro.SpringCoreTask1.metrics.TrainingMetrics;
import com.sro.SpringCoreTask1.metrics.TraineeTrainingMetrics;
import com.sro.SpringCoreTask1.metrics.TrainerTrainingMetrics;
import com.sro.SpringCoreTask1.repository.TraineeRepository;
import com.sro.SpringCoreTask1.repository.TrainerRepository;
import com.sro.SpringCoreTask1.repository.TrainingRepository;
import com.sro.SpringCoreTask1.repository.TrainingTypeRepository;
import com.sro.SpringCoreTask1.util.ProfileUtil;

@Service
public class DataSeedService {

    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainingRepository trainingRepository;

    private final TraineeSeedMapper traineeSeedMapper;
    private final TrainerSeedMapper trainerSeedMapper;
    private final TrainingSeedMapper trainingSeedMapper;
    private final TrainingTypeSeedMapper trainingTypeSeedMapper;

    private final PasswordEncoder passwordEncoder;

    private final TrainingMetrics trainingMetrics;
    private final TraineeTrainingMetrics traineeTrainingMetrics;
    private final TrainerTrainingMetrics trainerTrainingMetrics;

    public DataSeedService(TraineeRepository traineeRepository, TrainerRepository trainerRepository,
            TrainingTypeRepository trainingTypeRepository, TrainingRepository trainingRepository,
            TraineeSeedMapper traineeSeedMapper, TrainerSeedMapper trainerSeedMapper,
            TrainingSeedMapper trainingSeedMapper, TrainingTypeSeedMapper trainingTypeSeedMapper,
            TrainingMetrics trainingMetrics, TraineeTrainingMetrics traineeTrainingMetrics,
            TrainerTrainingMetrics trainerTrainingMetrics, PasswordEncoder passwordEncoder) {
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
        this.trainingTypeRepository = trainingTypeRepository;
        this.trainingRepository = trainingRepository;
        this.traineeSeedMapper = traineeSeedMapper;
        this.trainerSeedMapper = trainerSeedMapper;
        this.trainingSeedMapper = trainingSeedMapper;
        this.trainingTypeSeedMapper = trainingTypeSeedMapper;
        this.passwordEncoder = passwordEncoder;
        this.trainingMetrics = trainingMetrics;
        this.traineeTrainingMetrics = traineeTrainingMetrics;
        this.trainerTrainingMetrics = trainerTrainingMetrics;
    }

    @Transactional
    public void seedTrainee(TraineeSeedRequest traineeRequestDTO) {
        try {
            Trainee trainee = traineeSeedMapper.toEntity(traineeRequestDTO);
            trainee.setUsername(
                    ProfileUtil.generateUsername(traineeRequestDTO.firstName(), traineeRequestDTO.lastName(),
                            username -> traineeRepository.existsByUsername(username)));
            trainee.setPassword(passwordEncoder.encode(ProfileUtil.generatePassword()));
            Trainee savedTrainee = traineeRepository.save(trainee);

            if (traineeRequestDTO.trainerIds() != null && !traineeRequestDTO.trainerIds().isEmpty()) {
                for (Long trainerId : traineeRequestDTO.trainerIds()) {
                    addTrainerToTrainee(savedTrainee.getId(), trainerId);
                }
            }
        } catch (ConstraintViolationException e) {
            throw new ResourceAlreadyExistsException(
                    "Trainee with username " + traineeRequestDTO.username() + " already exists");
        } catch (Exception e) {
            throw new DatabaseOperationException("Error saving Trainee", e);
        }
    }

    @Transactional
    public void seedTrainer(TrainerSeedRequest trainerRequestDTO) {
        if (trainerRequestDTO == null) {
            throw new IllegalArgumentException("Trainer cannot be null");
        }

        try {
            TrainingType trainingType = trainingTypeRepository.findById(trainerRequestDTO.trainingTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "TrainingType not found with id: " + trainerRequestDTO.trainingTypeId()));
            Trainer trainer = trainerSeedMapper.toEntity(trainerRequestDTO, trainingType);
            trainer.setUsername(
                    ProfileUtil.generateUsername(trainerRequestDTO.firstName(), trainerRequestDTO.lastName(),
                            username -> trainerRepository.existsByUsername(username)));
            trainer.setPassword(passwordEncoder.encode(ProfileUtil.generatePassword()));
            trainerRepository.save(trainer);
        } catch (ConstraintViolationException e) {
            throw new ResourceAlreadyExistsException(
                    "Trainer with username " + trainerRequestDTO.username() + " already exists");
        } catch (Exception e) {
            throw new DatabaseOperationException("Error saving Trainer", e);
        }
    }

    @Transactional
    public void seedTraining(TrainingSeedRequest trainingRequestDTO) {
        if (trainingRequestDTO == null) {
            throw new IllegalArgumentException("TrainingRequestDTO cannot be null");
        }

        try {
            Trainee trainee = traineeRepository.findById(trainingRequestDTO.traineeId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Trainee not found with id: " + trainingRequestDTO.traineeId()));
            Trainer trainer = trainerRepository.findById(trainingRequestDTO.trainerId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Trainer not found with id: " + trainingRequestDTO.trainerId()));

            if (!trainer.isActive()) {
                throw new ResourceNotFoundException("Trainer with id: " + trainer.getId() + " is not active");
            }

            if (!trainee.isActive()) {
                throw new ResourceNotFoundException("Trainee with id: " + trainee.getId() + " is not active");
            }

            if (!trainee.getTrainers().contains(trainer)) {
                throw new IllegalArgumentException("Trainer not assigned to Trainee");
            }

            boolean isDuplicateTraining = trainingRepository.existsByTraineeAndTrainerAndTrainingDate(trainee, trainer,
                    trainingRequestDTO.trainingDate());
            if (isDuplicateTraining) {
                throw new ResourceAlreadyExistsException("A training with the same trainer and date already exists.");
            }

            TrainingType trainingType = trainingTypeRepository.findById(trainingRequestDTO.trainingTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "TrainingType not found with id: " + trainingRequestDTO.trainingTypeId()));

            Training training = trainingSeedMapper.toEntity(trainingRequestDTO, trainee, trainer, trainingType);
            trainingRepository.save(training);

            trainingMetrics.recordNewTraining();
            trainingMetrics.recordTrainingDuration(training.getDuration());

            traineeTrainingMetrics.recordTraineeSession();
            traineeTrainingMetrics.recordTraineeTrainingDuration(training.getDuration());

            trainerTrainingMetrics.recordTrainerSession();
            trainerTrainingMetrics.recordTrainerTrainingDuration(training.getDuration());
        } catch (ConstraintViolationException e) {
            throw new ResourceAlreadyExistsException(
                    "Training with name " + trainingRequestDTO.trainingName() + " already exists");
        } catch (Exception e) {
            throw new DatabaseOperationException("Error saving Training", e);
        }
    }

    @Transactional
    public void seedTrainingType(TrainingTypeSeedRequest trainingTypeRequestDTO) {
        if (trainingTypeRequestDTO == null) {
            throw new IllegalArgumentException("TrainingTypeRequestDTO cannot be null");
        }

        try {
            TrainingType trainingType = trainingTypeSeedMapper.toEntity(trainingTypeRequestDTO);
            trainingTypeRepository.save(trainingType);
        } catch (ConstraintViolationException e) {
            throw new ResourceAlreadyExistsException(
                    "Training Type with name " + trainingTypeRequestDTO.trainingTypeName() + " already exists");
        } catch (Exception e) {
            throw new DatabaseOperationException("Error saving Training Type", e);
        }
    }

    @Transactional
    private void addTrainerToTrainee(Long traineeId, Long trainerId) {
        if (traineeId == null || trainerId == null) {
            throw new IllegalArgumentException("Trainee id and Trainer id cannot be null");
        }

        try {
            Trainee trainee = traineeRepository.findById(traineeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Trainee not found with id: " + traineeId));

            Trainer trainer = trainerRepository.findById(trainerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with id: " + trainerId));

            if (!trainer.isActive()) {
                throw new ResourceNotFoundException("Trainer with id: " + trainerId + " is not active");
            }

            if (trainer.getTrainees().contains(trainee)) {
                throw new ResourceAlreadyExistsException("Trainer already assigned to Trainee");
            }

            trainee.addTrainer(trainer);
            traineeRepository.save(trainee);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error adding Trainer to Trainee", e);
        }
    }

}
