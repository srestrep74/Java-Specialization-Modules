package com.sro.SpringCoreTask1.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
import com.sro.SpringCoreTask1.metrics.TraineeTrainingMetrics;
import com.sro.SpringCoreTask1.metrics.TrainerTrainingMetrics;
import com.sro.SpringCoreTask1.metrics.TrainingMetrics;
import com.sro.SpringCoreTask1.repository.TraineeRepository;
import com.sro.SpringCoreTask1.repository.TrainerRepository;
import com.sro.SpringCoreTask1.repository.TrainingRepository;
import com.sro.SpringCoreTask1.repository.TrainingTypeRepository;
import com.sro.SpringCoreTask1.util.ProfileUtil;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class DataSeedServiceTest {

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private TraineeSeedMapper traineeSeedMapper;

    @Mock
    private TrainerSeedMapper trainerSeedMapper;

    @Mock
    private TrainingSeedMapper trainingSeedMapper;

    @Mock
    private TrainingTypeSeedMapper trainingTypeSeedMapper;

    @Mock
    private TrainingMetrics trainingMetrics;

    @Mock
    private TraineeTrainingMetrics traineeTrainingMetrics;

    @Mock
    private TrainerTrainingMetrics trainerTrainingMetrics;

    @InjectMocks
    private DataSeedService dataSeedService;

    private TraineeSeedRequest traineeSeedRequest;
    private TrainerSeedRequest trainerSeedRequest;
    private TrainingSeedRequest trainingSeedRequest;
    private TrainingTypeSeedRequest trainingTypeSeedRequest;

    private Trainee trainee;
    private Trainer trainer;
    private Training training;
    private TrainingType trainingType;

    @BeforeEach
    void setUp() {
        trainingType = new TrainingType();
        trainingType.setId(1L);
        trainingType.setTrainingTypeName("Strength");

        trainee = new Trainee();
        trainee.setId(1L);
        trainee.setFirstName("John");
        trainee.setLastName("Doe");
        trainee.setUsername("johndoe");
        trainee.setPassword("password");
        trainee.setActive(true);
        trainee.setAddress("123 Street");
        trainee.setDateOfBirth(LocalDate.of(2000, 1, 1));

        trainer = new Trainer();
        trainer.setId(1L);
        trainer.setFirstName("Trainer");
        trainer.setLastName("One");
        trainer.setUsername("trainer1");
        trainer.setPassword("password");
        trainer.setActive(true);
        trainer.setTrainingType(trainingType);

        training = new Training();
        training.setId(1L);
        training.setTrainingName("Strength Training");
        training.setTrainingDate(LocalDate.now());
        training.setDuration(60);
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(trainingType);

        traineeSeedRequest = new TraineeSeedRequest(
                "John",
                "Doe",
                "johndoe",
                "password",
                true,
                "123 Street",
                LocalDate.of(2000, 1, 1),
                List.of(1L),
                "TRAINEE");

        trainerSeedRequest = new TrainerSeedRequest(
                "Trainer",
                "One",
                "trainer1",
                "password",
                true,
                1L,
                "TRAINER");

        trainingSeedRequest = new TrainingSeedRequest(
                "Strength Training",
                LocalDate.now(),
                60,
                1L,
                1L,
                1L);

        trainingTypeSeedRequest = new TrainingTypeSeedRequest(
                "Strength");
    }

    @Test
    void seedTrainee_ShouldSaveTrainee_WhenValidInput() {
        try (MockedStatic<ProfileUtil> profileUtilMock = mockStatic(ProfileUtil.class)) {
            profileUtilMock.when(() -> ProfileUtil.generateUsername(anyString(), anyString(), any()))
                    .thenReturn("johndoe");
            profileUtilMock.when(ProfileUtil::generatePassword).thenReturn("password");

            when(traineeSeedMapper.toEntity(traineeSeedRequest)).thenReturn(trainee);
            when(traineeRepository.save(trainee)).thenReturn(trainee);
            when(traineeRepository.findById(1L)).thenReturn(Optional.of(trainee));
            when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));

            assertDoesNotThrow(() -> dataSeedService.seedTrainee(traineeSeedRequest));

            verify(traineeRepository, times(2)).save(trainee);
        }
    }

    @Test
    void seedTrainee_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        try (MockedStatic<ProfileUtil> profileUtilMock = mockStatic(ProfileUtil.class)) {
            profileUtilMock.when(() -> ProfileUtil.generateUsername(anyString(), anyString(), any()))
                    .thenReturn("johndoe");
            profileUtilMock.when(ProfileUtil::generatePassword).thenReturn("password");

            when(traineeSeedMapper.toEntity(traineeSeedRequest)).thenReturn(trainee);
            when(traineeRepository.save(trainee)).thenThrow(new RuntimeException("Database error"));

            assertThrows(DatabaseOperationException.class, () -> dataSeedService.seedTrainee(traineeSeedRequest));
        }
    }

    @Test
    void seedTrainee_ShouldThrowResourceAlreadyExistsException_WhenConstraintViolation() {
        try (MockedStatic<ProfileUtil> profileUtilMock = mockStatic(ProfileUtil.class)) {
            profileUtilMock.when(() -> ProfileUtil.generateUsername(anyString(), anyString(), any()))
                    .thenReturn("johndoe");
            profileUtilMock.when(ProfileUtil::generatePassword).thenReturn("password");

            when(traineeSeedMapper.toEntity(traineeSeedRequest)).thenReturn(trainee);
            when(traineeRepository.save(trainee)).thenThrow(ConstraintViolationException.class);

            assertThrows(ResourceAlreadyExistsException.class, () -> dataSeedService.seedTrainee(traineeSeedRequest));
        }
    }

    @Test
    void seedTrainer_ShouldSaveTrainer_WhenValidInput() {
        try (MockedStatic<ProfileUtil> profileUtilMock = mockStatic(ProfileUtil.class)) {
            profileUtilMock.when(() -> ProfileUtil.generateUsername(anyString(), anyString(), any()))
                    .thenReturn("trainer1");
            profileUtilMock.when(ProfileUtil::generatePassword).thenReturn("password");

            when(trainingTypeRepository.findById(1L)).thenReturn(Optional.of(trainingType));
            when(trainerSeedMapper.toEntity(trainerSeedRequest, trainingType)).thenReturn(trainer);
            when(trainerRepository.save(trainer)).thenReturn(trainer);

            assertDoesNotThrow(() -> dataSeedService.seedTrainer(trainerSeedRequest));

            verify(trainerRepository).save(trainer);
        }
    }

    @Test
    void seedTrainer_ShouldThrowIllegalArgumentException_WhenTrainerIsNull() {
        assertThrows(IllegalArgumentException.class, () -> dataSeedService.seedTrainer(null));
    }

    @Test
    void seedTrainer_ShouldThrowResourceNotFoundException_WhenTrainingTypeDoesNotExist() {
        when(trainingTypeRepository.findById(1L)).thenReturn(Optional.empty());

        DatabaseOperationException exception = assertThrows(DatabaseOperationException.class,
                () -> dataSeedService.seedTrainer(trainerSeedRequest));
        assertTrue(exception.getCause() instanceof ResourceNotFoundException);
    }

    @Test
    void seedTrainer_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(trainingTypeRepository.findById(1L)).thenReturn(Optional.of(trainingType));
        when(trainerSeedMapper.toEntity(trainerSeedRequest, trainingType)).thenReturn(trainer);
        when(trainerRepository.save(trainer)).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> dataSeedService.seedTrainer(trainerSeedRequest));
    }

    @Test
    void seedTrainer_ShouldThrowResourceAlreadyExistsException_WhenConstraintViolation() {
        when(trainingTypeRepository.findById(1L)).thenReturn(Optional.of(trainingType));
        when(trainerSeedMapper.toEntity(trainerSeedRequest, trainingType)).thenReturn(trainer);
        when(trainerRepository.save(trainer)).thenThrow(ConstraintViolationException.class);

        assertThrows(ResourceAlreadyExistsException.class, () -> dataSeedService.seedTrainer(trainerSeedRequest));
    }

    @Test
    void seedTraining_ShouldSaveTraining_WhenValidInput() {
        trainee.addTrainer(trainer);

        when(traineeRepository.findById(1L)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findById(1L)).thenReturn(Optional.of(trainingType));
        when(trainingRepository.existsByTraineeAndTrainerAndTrainingDate(trainee, trainer,
                trainingSeedRequest.trainingDate()))
                .thenReturn(false);
        when(trainingSeedMapper.toEntity(trainingSeedRequest, trainee, trainer, trainingType)).thenReturn(training);
        when(trainingRepository.save(training)).thenReturn(training);
        doNothing().when(trainingMetrics).recordNewTraining();
        doNothing().when(trainingMetrics).recordTrainingDuration(60);
        doNothing().when(traineeTrainingMetrics).recordTraineeSession();
        doNothing().when(traineeTrainingMetrics).recordTraineeTrainingDuration(60);
        doNothing().when(trainerTrainingMetrics).recordTrainerSession();
        doNothing().when(trainerTrainingMetrics).recordTrainerTrainingDuration(60);

        assertDoesNotThrow(() -> dataSeedService.seedTraining(trainingSeedRequest));

        verify(trainingRepository).save(training);
        verify(trainingMetrics).recordNewTraining();
        verify(trainingMetrics).recordTrainingDuration(60);
        verify(traineeTrainingMetrics).recordTraineeSession();
        verify(traineeTrainingMetrics).recordTraineeTrainingDuration(60);
        verify(trainerTrainingMetrics).recordTrainerSession();
        verify(trainerTrainingMetrics).recordTrainerTrainingDuration(60);
    }

    @Test
    void seedTraining_ShouldThrowIllegalArgumentException_WhenTrainingIsNull() {
        assertThrows(IllegalArgumentException.class, () -> dataSeedService.seedTraining(null));
    }

    @Test
    void seedTraining_ShouldThrowResourceNotFoundException_WhenTraineeDoesNotExist() {
        when(traineeRepository.findById(1L)).thenReturn(Optional.empty());

        DatabaseOperationException exception = assertThrows(DatabaseOperationException.class,
                () -> dataSeedService.seedTraining(trainingSeedRequest));
        assertTrue(exception.getCause() instanceof ResourceNotFoundException);
    }

    @Test
    void seedTraining_ShouldThrowResourceNotFoundException_WhenTrainerDoesNotExist() {
        when(traineeRepository.findById(1L)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findById(1L)).thenReturn(Optional.empty());

        DatabaseOperationException exception = assertThrows(DatabaseOperationException.class,
                () -> dataSeedService.seedTraining(trainingSeedRequest));
        assertTrue(exception.getCause() instanceof ResourceNotFoundException);
    }

    @Test
    void seedTraining_ShouldThrowResourceNotFoundException_WhenTrainerNotActive() {
        trainer.setActive(false);
        when(traineeRepository.findById(1L)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));

        DatabaseOperationException exception = assertThrows(DatabaseOperationException.class,
                () -> dataSeedService.seedTraining(trainingSeedRequest));
        assertTrue(exception.getCause() instanceof ResourceNotFoundException);
    }

    @Test
    void seedTraining_ShouldThrowResourceNotFoundException_WhenTraineeNotActive() {
        trainee.setActive(false);
        when(traineeRepository.findById(1L)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));

        DatabaseOperationException exception = assertThrows(DatabaseOperationException.class,
                () -> dataSeedService.seedTraining(trainingSeedRequest));
        assertTrue(exception.getCause() instanceof ResourceNotFoundException);
    }

    @Test
    void seedTraining_ShouldThrowIllegalArgumentException_WhenTrainerNotAssignedToTrainee() {
        when(traineeRepository.findById(1L)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));

        DatabaseOperationException exception = assertThrows(DatabaseOperationException.class,
                () -> dataSeedService.seedTraining(trainingSeedRequest));
        assertTrue(exception.getCause() instanceof IllegalArgumentException);
    }

    @Test
    void seedTraining_ShouldThrowResourceAlreadyExistsException_WhenDuplicateTraining() {
        trainee.addTrainer(trainer);

        when(traineeRepository.findById(1L)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));
        when(trainingRepository.existsByTraineeAndTrainerAndTrainingDate(trainee, trainer,
                trainingSeedRequest.trainingDate()))
                .thenReturn(true);

        DatabaseOperationException exception = assertThrows(DatabaseOperationException.class,
                () -> dataSeedService.seedTraining(trainingSeedRequest));
        assertTrue(exception.getCause() instanceof ResourceAlreadyExistsException);
    }

    @Test
    void seedTraining_ShouldThrowResourceNotFoundException_WhenTrainingTypeDoesNotExist() {
        trainee.addTrainer(trainer);

        when(traineeRepository.findById(1L)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));
        when(trainingRepository.existsByTraineeAndTrainerAndTrainingDate(trainee, trainer,
                trainingSeedRequest.trainingDate()))
                .thenReturn(false);
        when(trainingTypeRepository.findById(1L)).thenReturn(Optional.empty());

        DatabaseOperationException exception = assertThrows(DatabaseOperationException.class,
                () -> dataSeedService.seedTraining(trainingSeedRequest));
        assertTrue(exception.getCause() instanceof ResourceNotFoundException);
    }

    @Test
    void seedTraining_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        trainee.addTrainer(trainer);

        when(traineeRepository.findById(1L)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));
        when(trainingRepository.existsByTraineeAndTrainerAndTrainingDate(trainee, trainer,
                trainingSeedRequest.trainingDate()))
                .thenReturn(false);
        when(trainingTypeRepository.findById(1L)).thenReturn(Optional.of(trainingType));
        when(trainingSeedMapper.toEntity(trainingSeedRequest, trainee, trainer, trainingType)).thenReturn(training);
        when(trainingRepository.save(training)).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> dataSeedService.seedTraining(trainingSeedRequest));
    }

    @Test
    void seedTrainingType_ShouldSaveTrainingType_WhenValidInput() {
        when(trainingTypeSeedMapper.toEntity(trainingTypeSeedRequest)).thenReturn(trainingType);
        when(trainingTypeRepository.save(trainingType)).thenReturn(trainingType);

        assertDoesNotThrow(() -> dataSeedService.seedTrainingType(trainingTypeSeedRequest));

        verify(trainingTypeRepository).save(trainingType);
    }

    @Test
    void seedTrainingType_ShouldThrowIllegalArgumentException_WhenTrainingTypeIsNull() {
        assertThrows(IllegalArgumentException.class, () -> dataSeedService.seedTrainingType(null));
    }

    @Test
    void seedTrainingType_ShouldThrowResourceAlreadyExistsException_WhenConstraintViolation() {
        when(trainingTypeSeedMapper.toEntity(trainingTypeSeedRequest)).thenReturn(trainingType);
        when(trainingTypeRepository.save(trainingType)).thenThrow(ConstraintViolationException.class);

        assertThrows(ResourceAlreadyExistsException.class,
                () -> dataSeedService.seedTrainingType(trainingTypeSeedRequest));
    }

    @Test
    void seedTrainingType_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(trainingTypeSeedMapper.toEntity(trainingTypeSeedRequest)).thenReturn(trainingType);
        when(trainingTypeRepository.save(trainingType)).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> dataSeedService.seedTrainingType(trainingTypeSeedRequest));
    }
}