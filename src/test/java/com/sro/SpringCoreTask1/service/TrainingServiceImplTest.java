package com.sro.SpringCoreTask1.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sro.SpringCoreTask1.dtos.v1.request.training.CreateTrainingRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.training.TraineeTrainingFilter;
import com.sro.SpringCoreTask1.dtos.v1.request.training.TraineeTrainingResponse;
import com.sro.SpringCoreTask1.dtos.v1.request.training.TrainerTrainingFilter;
import com.sro.SpringCoreTask1.dtos.v1.request.training.UpdateTrainingRequest;
import com.sro.SpringCoreTask1.dtos.v1.response.training.TrainingSummaryResponse;
import com.sro.SpringCoreTask1.entity.Trainee;
import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.entity.Training;
import com.sro.SpringCoreTask1.entity.TrainingType;
import com.sro.SpringCoreTask1.exception.DatabaseOperationException;
import com.sro.SpringCoreTask1.exception.ResourceNotFoundException;
import com.sro.SpringCoreTask1.mappers.training.TrainingCreateMapper;
import com.sro.SpringCoreTask1.mappers.training.TrainingResponseMapper;
import com.sro.SpringCoreTask1.mappers.training.TrainingTraineeMapper;
import com.sro.SpringCoreTask1.mappers.training.TrainingUpdateMapper;
import com.sro.SpringCoreTask1.mappers.training.TraininigTrainerMapper;
import com.sro.SpringCoreTask1.metrics.TraineeTrainingMetrics;
import com.sro.SpringCoreTask1.metrics.TrainerTrainingMetrics;
import com.sro.SpringCoreTask1.metrics.TrainingMetrics;
import com.sro.SpringCoreTask1.repository.TraineeRepository;
import com.sro.SpringCoreTask1.repository.TrainerRepository;
import com.sro.SpringCoreTask1.repository.TrainingRepository;
import com.sro.SpringCoreTask1.service.impl.TrainingServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainingCreateMapper trainingCreateMapper;

    @Mock
    private TrainingResponseMapper trainingResponseMapper;

    @Mock
    private TrainingUpdateMapper trainingUpdateMapper;

    @Mock
    private TrainingTraineeMapper trainingTraineeMapper;

    @Mock
    private TraininigTrainerMapper traininigTrainerMapper;

    @Mock
    private TrainingMetrics trainingMetrics;

    @Mock
    private TraineeTrainingMetrics traineeTrainingMetrics;

    @Mock
    private TrainerTrainingMetrics trainerTrainingMetrics;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    private Training training;
    private Trainee trainee;
    private Trainer trainer;
    private TrainingType trainingType;
    private CreateTrainingRequest createTrainingRequest;
    private UpdateTrainingRequest updateTrainingRequest;
    private TrainingSummaryResponse trainingSummaryResponse;
    private TraineeTrainingResponse traineeTrainingResponse;

    @BeforeEach
    void setUp() {
        trainee = new Trainee();
        trainee.setId(1L);
        trainee.setFirstName("John");
        trainee.setLastName("Doe");
        trainee.setUsername("johndoe");
        trainee.setActive(true);

        trainer = new Trainer();
        trainer.setId(1L);
        trainer.setFirstName("Trainer");
        trainer.setLastName("One");
        trainer.setUsername("trainer1");
        trainer.setActive(true);

        trainingType = new TrainingType();
        trainingType.setId(1L);
        trainingType.setTrainingTypeName("Fitness");
        trainer.setTrainingType(trainingType);

        training = new Training();
        training.setId(1L);
        training.setTrainingName("Morning Session");
        training.setTrainingDate(LocalDate.now());
        training.setDuration(60);
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(trainingType);

        createTrainingRequest = new CreateTrainingRequest(
                "johndoe",
                "trainer1",
                "Morning Session",
                LocalDate.now(),
                60);

        updateTrainingRequest = new UpdateTrainingRequest(
                "Updated Session",
                LocalDate.now().plusDays(1),
                90,
                "trainer1",
                "johndoe",
                "Fitness");

        trainingSummaryResponse = new TrainingSummaryResponse(
                "Morning Session",
                "Trainer One",
                "John Doe",
                "Fitness",
                LocalDate.now(),
                60);

        traineeTrainingResponse = new TraineeTrainingResponse(
                "Morning Session",
                LocalDate.now(),
                "Fitness",
                60,
                "Trainer One");
    }

    @Test
    void save_ShouldNotThrowException_WhenValidInput() {
        when(traineeRepository.findByUsername("johndoe")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsername("trainer1")).thenReturn(Optional.of(trainer));
        when(trainingCreateMapper.toEntity(createTrainingRequest, trainer, trainee, trainingType))
                .thenReturn(training);
        doNothing().when(trainingMetrics).recordNewTraining();
        doNothing().when(trainingMetrics).recordTrainingDuration(anyLong());
        doNothing().when(traineeTrainingMetrics).recordTraineeSession();
        doNothing().when(traineeTrainingMetrics).recordTraineeTrainingDuration(anyLong());
        doNothing().when(trainerTrainingMetrics).recordTrainerSession();
        doNothing().when(trainerTrainingMetrics).recordTrainerTrainingDuration(anyLong());

        assertDoesNotThrow(() -> trainingService.save(createTrainingRequest));
        verify(trainingRepository).save(training);
        verify(trainingMetrics).recordNewTraining();
        verify(trainingMetrics).recordTrainingDuration(60L);
        verify(traineeTrainingMetrics).recordTraineeSession();
        verify(traineeTrainingMetrics).recordTraineeTrainingDuration(60L);
        verify(trainerTrainingMetrics).recordTrainerSession();
        verify(trainerTrainingMetrics).recordTrainerTrainingDuration(60L);
    }

    @Test
    void save_ShouldThrowIllegalArgumentException_WhenRequestIsNull() {
        assertThrows(IllegalArgumentException.class, () -> trainingService.save(null));
    }

    @Test
    void save_ShouldThrowResourceNotFoundException_WhenTraineeNotFound() {
        when(traineeRepository.findByUsername("johndoe")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> trainingService.save(createTrainingRequest));

        assertTrue(exception.getMessage().contains("Trainee not found"));
    }

    @Test
    void save_ShouldThrowResourceNotFoundException_WhenTrainerNotFound() {
        when(traineeRepository.findByUsername("johndoe")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsername("trainer1")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> trainingService.save(createTrainingRequest));

        assertTrue(exception.getMessage().contains("Trainer not found"));
    }

    @Test
    void save_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(traineeRepository.findByUsername("johndoe")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsername("trainer1")).thenReturn(Optional.of(trainer));
        when(trainingCreateMapper.toEntity(createTrainingRequest, trainer, trainee, trainingType))
                .thenReturn(training);
        when(trainingRepository.save(training)).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class,
                () -> trainingService.save(createTrainingRequest));
    }

    @Test
    void findById_ShouldReturnTrainingSummaryResponse_WhenTrainingExists() {
        when(trainingRepository.findById(1L)).thenReturn(Optional.of(training));
        when(trainingResponseMapper.toTrainingSummaryResponse(training))
                .thenReturn(trainingSummaryResponse);

        TrainingSummaryResponse result = trainingService.findById(1L);

        assertNotNull(result);
        assertEquals(trainingSummaryResponse, result);
    }

    @Test
    void findById_ShouldThrowIllegalArgumentException_WhenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> trainingService.findById(null));
    }

    @Test
    void findById_ShouldThrowResourceNotFoundException_WhenTrainingDoesNotExist() {
        when(trainingRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(DatabaseOperationException.class,
                () -> trainingService.findById(1L));

        assertTrue(exception.getCause() instanceof ResourceNotFoundException);
    }

    @Test
    void findById_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(trainingRepository.findById(1L)).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> trainingService.findById(1L));
    }

    @Test
    void findAll_ShouldReturnListOfTrainingSummaryResponse_WhenTrainingsExist() {
        when(trainingRepository.findAll()).thenReturn(List.of(training));
        when(trainingResponseMapper.toTrainingSummaryResponse(training))
                .thenReturn(trainingSummaryResponse);

        List<TrainingSummaryResponse> result = trainingService.findAll();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(trainingSummaryResponse, result.get(0));
    }

    @Test
    void findAll_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(trainingRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> trainingService.findAll());
    }

    @Test
    void update_ShouldReturnUpdatedTrainingSummaryResponse_WhenTrainingExists() {
        when(traineeRepository.findByUsername("johndoe")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsername("trainer1")).thenReturn(Optional.of(trainer));
        when(trainingUpdateMapper.toEntity(updateTrainingRequest, trainer, trainee, trainingType))
                .thenReturn(training);
        when(trainingRepository.save(training)).thenReturn(training);
        when(trainingResponseMapper.toTrainingSummaryResponse(training))
                .thenReturn(trainingSummaryResponse);
        doNothing().when(trainingMetrics).recordTrainingDuration(anyLong());
        doNothing().when(traineeTrainingMetrics).recordTraineeTrainingDuration(anyLong());
        doNothing().when(trainerTrainingMetrics).recordTrainerTrainingDuration(anyLong());

        TrainingSummaryResponse result = trainingService.update(updateTrainingRequest);

        assertNotNull(result);
        assertEquals(trainingSummaryResponse, result);
        verify(trainingMetrics).recordTrainingDuration(60L);
        verify(traineeTrainingMetrics).recordTraineeTrainingDuration(60L);
        verify(trainerTrainingMetrics).recordTrainerTrainingDuration(60L);
    }

    @Test
    void update_ShouldThrowIllegalArgumentException_WhenRequestIsNull() {
        assertThrows(IllegalArgumentException.class, () -> trainingService.update(null));
    }

    @Test
    void update_ShouldThrowResourceNotFoundException_WhenTraineeNotFound() {
        when(traineeRepository.findByUsername("johndoe")).thenReturn(Optional.empty());

        Exception exception = assertThrows(DatabaseOperationException.class,
                () -> trainingService.update(updateTrainingRequest));

        assertTrue(exception.getCause() instanceof ResourceNotFoundException);
    }

    @Test
    void update_ShouldThrowResourceNotFoundException_WhenTrainerNotFound() {
        when(traineeRepository.findByUsername("johndoe")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsername("trainer1")).thenReturn(Optional.empty());

        Exception exception = assertThrows(DatabaseOperationException.class,
                () -> trainingService.update(updateTrainingRequest));

        assertTrue(exception.getCause() instanceof ResourceNotFoundException);
    }

    @Test
    void update_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(traineeRepository.findByUsername("johndoe")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsername("trainer1")).thenReturn(Optional.of(trainer));
        when(trainingUpdateMapper.toEntity(updateTrainingRequest, trainer, trainee, trainingType))
                .thenReturn(training);
        when(trainingRepository.save(training)).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class,
                () -> trainingService.update(updateTrainingRequest));
    }

    @Test
    void deleteById_ShouldDeleteTraining_WhenTrainingExists() {
        doNothing().when(trainingRepository).deleteById(1L);

        assertDoesNotThrow(() -> trainingService.deleteById(1L));
    }

    @Test
    void deleteById_ShouldThrowIllegalArgumentException_WhenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> trainingService.deleteById(null));
    }

    @Test
    void deleteById_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        doThrow(new RuntimeException("Database error")).when(trainingRepository).deleteById(1L);

        assertThrows(DatabaseOperationException.class, () -> trainingService.deleteById(1L));
    }

    @Test
    void findTrainingsByTraineeWithFilters_ShouldReturnListOfTraineeTrainingResponse() {
        TraineeTrainingFilter filter = new TraineeTrainingFilter(
                "johndoe",
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                "trainer1",
                "Fitness");

        Specification<Training> anyTrainingSpec = any();
        Sort anySort = any();
        when(trainingRepository.findAll(anyTrainingSpec, anySort))
                .thenReturn(List.of(training));
        when(trainingTraineeMapper.toTraineeTrainingResponse(training))
                .thenReturn(traineeTrainingResponse);

        List<TraineeTrainingResponse> result = trainingService
                .findTrainingsByTraineeWithFilters(filter, "date", "asc");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(traineeTrainingResponse, result.get(0));
    }

    @Test
    void findTrainingsByTraineeWithFilters_ShouldThrowIllegalArgumentException_WhenFilterIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> trainingService.findTrainingsByTraineeWithFilters(null, "date", "asc"));
    }

    @Test
    void findTrainingsByTraineeWithFilters_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        TraineeTrainingFilter filter = new TraineeTrainingFilter(
                "johndoe",
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                "trainer1",
                "Fitness");

        Specification<Training> anyTrainingSpec = any();
        Sort anySort = any();
        when(trainingRepository.findAll(anyTrainingSpec, anySort))
                .thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class,
                () -> trainingService.findTrainingsByTraineeWithFilters(filter, "date", "asc"));
    }

    @Test
    void findTrainingsByTrainerWithFilters_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        TrainerTrainingFilter filter = new TrainerTrainingFilter(
                "trainee1",
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                "johndoe");

        Specification<Training> anyTrainingSpec = any();
        when(trainingRepository.findAll(anyTrainingSpec))
                .thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class,
                () -> trainingService.findTrainingsByTrainerWithFilters(filter));
    }

    @Test
    void findTrainingsByTrainerWithFilters_ShouldThrowIllegalArgumentException_WhenFilterIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> trainingService.findTrainingsByTrainerWithFilters(null));
    }
}