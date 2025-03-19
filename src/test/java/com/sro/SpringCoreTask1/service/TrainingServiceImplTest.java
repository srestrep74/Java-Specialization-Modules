package com.sro.SpringCoreTask1.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sro.SpringCoreTask1.dto.TraineeTrainingFilterDTO;
import com.sro.SpringCoreTask1.dto.TrainerTrainingFilterDTO;
import com.sro.SpringCoreTask1.dto.request.TrainingRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TraineeResponseDTO;
import com.sro.SpringCoreTask1.dto.response.TrainerResponseDTO;
import com.sro.SpringCoreTask1.dto.response.TrainingResponseDTO;
import com.sro.SpringCoreTask1.dto.response.TrainingTypeResponseDTO;
import com.sro.SpringCoreTask1.service.impl.TrainingServiceImpl;
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private TrainingTypeRepository trainingTypeRepository;

    @Mock
    private TrainingMapper trainingMapper;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    private Training training;
    private TrainingRequestDTO trainingRequestDTO;
    private TrainingResponseDTO trainingResponseDTO;
    private Trainee trainee;
    private TraineeResponseDTO traineeResponseDTO;
    private Trainer trainer;
    private TrainerResponseDTO trainerResponseDTO;
    private TrainingType trainingType;
    private TrainingTypeResponseDTO trainingTypeResponseDTO;

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
        trainer.setFirstName("John");
        trainer.setLastName("Doe");
        trainer.setUsername("johndoe");
        trainer.setActive(true);

        trainingType = new TrainingType();
        trainingType.setId(1L);
        trainingType.setTrainingTypeName("Fitness");

        training = new Training();
        training.setId(1L);
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(trainingType);

        trainingTypeResponseDTO = new TrainingTypeResponseDTO(1L, "Fitness");
        trainerResponseDTO = new TrainerResponseDTO(1L, "John", "Doe", "johndoe", true, trainingTypeResponseDTO);
        traineeResponseDTO = new TraineeResponseDTO(1L, "John", "Doe", "johndoe", true, "123 Main St", LocalDate.now());

        trainingRequestDTO = new TrainingRequestDTO("Training 1", LocalDate.now(), 10, 1L, 1L, 1L);
        trainingResponseDTO = new TrainingResponseDTO(1L, "Training 1", LocalDate.now(), 60, traineeResponseDTO, trainerResponseDTO, trainingTypeResponseDTO);
    }

    @Test
    void save_ShouldReturnTrainingResponseDTO_WhenValidInput() {
        when(traineeRepository.findById(1L)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findById(1L)).thenReturn(Optional.of(trainingType));
        when(trainingMapper.toEntity(trainingRequestDTO, trainee, trainer, trainingType)).thenReturn(training);
        when(trainingRepository.save(training)).thenReturn(training);
        when(trainingMapper.toDTO(training)).thenReturn(trainingResponseDTO);

        trainee.getTrainers().add(trainer);

        TrainingResponseDTO result = trainingService.save(trainingRequestDTO);

        assertNotNull(result);
        assertEquals(trainingResponseDTO, result);
        verify(trainingRepository).save(training);
    }

    @Test
    void save_ShouldThrowIllegalArgumentException_WhenTrainingRequestDTOIsNull() {
        assertThrows(IllegalArgumentException.class, () -> trainingService.save(null));
    }

    @Test
    void save_ShouldThrowResourceNotFoundException_WhenTraineeDoesNotExist() {
        when(traineeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> trainingService.save(trainingRequestDTO));
    }

    @Test
    void save_ShouldThrowResourceNotFoundException_WhenTrainerDoesNotExist() {
        when(traineeRepository.findById(1L)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> trainingService.save(trainingRequestDTO));
    }

    @Test
    void save_ShouldThrowResourceNotFoundException_WhenTrainerIsNotActive() {
        trainer.setActive(false);
        when(traineeRepository.findById(1L)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));

        assertThrows(ResourceNotFoundException.class, () -> trainingService.save(trainingRequestDTO));
    }

    @Test
    void save_ShouldThrowResourceNotFoundException_WhenTraineeIsNotActive() {
        trainee.setActive(false);
        when(traineeRepository.findById(1L)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));

        assertThrows(ResourceNotFoundException.class, () -> trainingService.save(trainingRequestDTO));
    }

    @Test
    void findById_ShouldReturnTrainingResponseDTO_WhenTrainingExists() {
        when(trainingRepository.findById(1L)).thenReturn(Optional.of(training));
        when(trainingMapper.toDTO(training)).thenReturn(trainingResponseDTO);

        TrainingResponseDTO result = trainingService.findById(1L);

        assertNotNull(result);
        assertEquals(trainingResponseDTO, result);
    }

    @Test
    void findById_ShouldThrowIllegalArgumentException_WhenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> trainingService.findById(null));
    }

    @Test
    void findById_ShouldThrowResourceNotFoundException_WhenTrainingDoesNotExist() {
        when(trainingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> trainingService.findById(1L));
    }

    @Test
    void findById_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(trainingRepository.findById(1L)).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> trainingService.findById(1L));
    }

    @Test
    void findAll_ShouldReturnListOfTrainingResponseDTO_WhenTrainingsExist() {
        when(trainingRepository.findAll()).thenReturn(List.of(training));
        when(trainingMapper.toDTO(training)).thenReturn(trainingResponseDTO);

        List<TrainingResponseDTO> result = trainingService.findAll();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(trainingResponseDTO, result.get(0));
    }

    @Test
    void findAll_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(trainingRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> trainingService.findAll());
    }

    @Test
    void update_ShouldReturnUpdatedTrainingResponseDTO_WhenTrainingExists() {
        when(traineeRepository.findById(1L)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findById(1L)).thenReturn(Optional.of(trainingType));
        when(trainingMapper.toEntity(trainingRequestDTO, trainee, trainer, trainingType)).thenReturn(training);
        when(trainingRepository.update(training)).thenReturn(Optional.of(training));
        when(trainingMapper.toDTO(training)).thenReturn(trainingResponseDTO);

        TrainingResponseDTO result = trainingService.update(trainingRequestDTO);

        assertNotNull(result);
        assertEquals(trainingResponseDTO, result);
    }

    @Test
    void update_ShouldThrowIllegalArgumentException_WhenTrainingRequestDTOIsNull() {
        assertThrows(IllegalArgumentException.class, () -> trainingService.update(null));
    }

    @Test
    void update_ShouldThrowResourceNotFoundException_WhenTraineeDoesNotExist() {
        when(traineeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> trainingService.update(trainingRequestDTO));
    }

    @Test
    void update_ShouldThrowResourceNotFoundException_WhenTrainerDoesNotExist() {
        when(traineeRepository.findById(1L)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> trainingService.update(trainingRequestDTO));
    }

    @Test
    void update_ShouldThrowResourceNotFoundException_WhenTrainingTypeDoesNotExist() {
        when(traineeRepository.findById(1L)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> trainingService.update(trainingRequestDTO));
    }

    @Test
    void update_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(traineeRepository.findById(1L)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findById(1L)).thenReturn(Optional.of(trainingType));
        when(trainingMapper.toEntity(trainingRequestDTO, trainee, trainer, trainingType)).thenReturn(training);
        when(trainingRepository.update(training)).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> trainingService.update(trainingRequestDTO));
    }

    @Test
    void deleteById_ShouldDeleteTraining_WhenTrainingExists() {
        when(trainingRepository.deleteById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> trainingService.deleteById(1L));
    }

    @Test
    void deleteById_ShouldThrowIllegalArgumentException_WhenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> trainingService.deleteById(null));
    }

    @Test
    void deleteById_ShouldThrowResourceNotFoundException_WhenTrainingDoesNotExist() {
        when(trainingRepository.deleteById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> trainingService.deleteById(1L));
    }

    @Test
    void deleteById_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(trainingRepository.deleteById(1L)).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> trainingService.deleteById(1L));
    }

    @Test
    void findTrainingsByTraineeWithFilters_ShouldReturnListOfTrainingResponseDTO_WhenTrainingsExist() {
        TraineeTrainingFilterDTO filterDTO = new TraineeTrainingFilterDTO(1L, LocalDate.now(), LocalDate.now(), "", "");
        when(trainingRepository.findTrainingsByTraineeWithFilters(filterDTO)).thenReturn(List.of(training));
        when(trainingMapper.toDTO(training)).thenReturn(trainingResponseDTO);

        List<TrainingResponseDTO> result = trainingService.findTrainingsByTraineeWithFilters(filterDTO);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(trainingResponseDTO, result.get(0));
    }

    @Test
    void findTrainingsByTraineeWithFilters_ShouldThrowIllegalArgumentException_WhenFilterDTOIsNull() {
        assertThrows(IllegalArgumentException.class, () -> trainingService.findTrainingsByTraineeWithFilters(null));
    }

    @Test
    void findTrainingsByTraineeWithFilters_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        TraineeTrainingFilterDTO filterDTO = new TraineeTrainingFilterDTO(1L, LocalDate.now(), LocalDate.now(), "johndoe", "Fitness");
        when(trainingRepository.findTrainingsByTraineeWithFilters(filterDTO)).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> trainingService.findTrainingsByTraineeWithFilters(filterDTO));
    }

    @Test
    void findTrainingsByTrainerWithFilters_ShouldReturnListOfTrainingResponseDTO_WhenTrainingsExist() {
        TrainerTrainingFilterDTO filterDTO = new TrainerTrainingFilterDTO(1L, LocalDate.now(), LocalDate.now(), "johndoe","Fitness");
        when(trainingRepository.findTrainingsByTrainerWithFilters(filterDTO)).thenReturn(List.of(training));
        when(trainingMapper.toDTO(training)).thenReturn(trainingResponseDTO);

        List<TrainingResponseDTO> result = trainingService.findTrainingsByTrainerWithFilters(filterDTO);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(trainingResponseDTO, result.get(0));
    }

    @Test
    void findTrainingsByTrainerWithFilters_ShouldThrowIllegalArgumentException_WhenFilterDTOIsNull() {
        assertThrows(IllegalArgumentException.class, () -> trainingService.findTrainingsByTrainerWithFilters(null));
    }

    @Test
    void findTrainingsByTrainerWithFilters_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        TrainerTrainingFilterDTO filterDTO = new TrainerTrainingFilterDTO(1L, LocalDate.now(), LocalDate.now(), "johndoe", "Fitness");
        when(trainingRepository.findTrainingsByTrainerWithFilters(filterDTO)).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> trainingService.findTrainingsByTrainerWithFilters(filterDTO));
    }
}