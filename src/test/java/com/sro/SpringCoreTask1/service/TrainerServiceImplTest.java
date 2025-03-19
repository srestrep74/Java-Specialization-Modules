package com.sro.SpringCoreTask1.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sro.SpringCoreTask1.dto.request.TrainerRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TrainerResponseDTO;
import com.sro.SpringCoreTask1.dto.response.TrainingTypeResponseDTO;
import com.sro.SpringCoreTask1.service.impl.TrainerServiceImpl;
import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.entity.TrainingType;
import com.sro.SpringCoreTask1.exception.DatabaseOperationException;
import com.sro.SpringCoreTask1.exception.ResourceAlreadyExistsException;
import com.sro.SpringCoreTask1.exception.ResourceNotFoundException;
import com.sro.SpringCoreTask1.mappers.TrainerMapper;
import com.sro.SpringCoreTask1.repository.TrainerRepository;
import com.sro.SpringCoreTask1.repository.TrainingTypeRepository;

import jakarta.validation.ConstraintViolationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @Mock
    private TrainerMapper trainerMapper;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    private Trainer trainer;
    private TrainerRequestDTO trainerRequestDTO;
    private TrainerResponseDTO trainerResponseDTO;
    private TrainingType trainingType;
    private TrainingTypeResponseDTO trainingTypeResponseDTO;

    @BeforeEach
    void setUp() {
        trainingType = new TrainingType();
        trainingType.setId(1L);
        trainingType.setTrainingTypeName("Fitness");

        trainingTypeResponseDTO = new TrainingTypeResponseDTO(1L, "Fitness");

        trainer = new Trainer();
        trainer.setId(1L);
        trainer.setFirstName("John");
        trainer.setLastName("Doe");
        trainer.setUsername("johndoe");
        trainer.setPassword("password");
        trainer.setActive(true);
        trainer.setTrainingType(trainingType);

        trainerRequestDTO = new TrainerRequestDTO("John", "Doe", "johndoe", "password", true, 1L);
        trainerResponseDTO = new TrainerResponseDTO(1L, "John", "Doe", "johndoe", true, trainingTypeResponseDTO);
    }

    @Test
    void save_ShouldReturnTrainerResponseDTO_WhenValidInput() {
        when(trainingTypeRepository.findById(1L)).thenReturn(Optional.of(trainingType));
        when(trainerMapper.toEntity(trainerRequestDTO, trainingType)).thenReturn(trainer);
        when(trainerRepository.save(trainer)).thenReturn(trainer);
        when(trainerMapper.toDTO(trainer)).thenReturn(trainerResponseDTO);

        TrainerResponseDTO result = trainerService.save(trainerRequestDTO);

        assertNotNull(result);
        assertEquals(trainerResponseDTO, result);
        verify(trainerRepository).save(trainer);
    }

    @Test
    void save_ShouldThrowIllegalArgumentException_WhenTrainerIsNull() {
        assertThrows(IllegalArgumentException.class, () -> trainerService.save(null));
    }

    @Test
    void save_ShouldThrowResourceNotFoundException_WhenTrainingTypeDoesNotExist() {
        when(trainingTypeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> trainerService.save(trainerRequestDTO));
    }

    @Test
    void save_ShouldThrowResourceAlreadyExistsException_WhenUsernameExists() {
        when(trainingTypeRepository.findById(1L)).thenReturn(Optional.of(trainingType));
        when(trainerMapper.toEntity(trainerRequestDTO, trainingType)).thenReturn(trainer);
        when(trainerRepository.save(trainer)).thenThrow(new ConstraintViolationException("Username already exists", null));

        assertThrows(ResourceAlreadyExistsException.class, () -> trainerService.save(trainerRequestDTO));
    }

    @Test
    void save_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(trainingTypeRepository.findById(1L)).thenReturn(Optional.of(trainingType));
        when(trainerMapper.toEntity(trainerRequestDTO, trainingType)).thenReturn(trainer);
        when(trainerRepository.save(trainer)).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> trainerService.save(trainerRequestDTO));
    }

    @Test
    void findById_ShouldReturnTrainerResponseDTO_WhenTrainerExists() {
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));
        when(trainerMapper.toDTO(trainer)).thenReturn(trainerResponseDTO);

        TrainerResponseDTO result = trainerService.findById(1L);

        assertNotNull(result);
        assertEquals(trainerResponseDTO, result);
    }

    @Test
    void findById_ShouldThrowIllegalArgumentException_WhenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> trainerService.findById(null));
    }

    @Test
    void findById_ShouldThrowResourceNotFoundException_WhenTrainerDoesNotExist() {
        when(trainerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> trainerService.findById(1L));
    }

    @Test
    void findById_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(trainerRepository.findById(1L)).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> trainerService.findById(1L));
    }

    @Test
    void findAll_ShouldReturnListOfTrainerResponseDTO_WhenTrainersExist() {
        when(trainerRepository.findAll()).thenReturn(List.of(trainer));
        when(trainerMapper.toDTO(trainer)).thenReturn(trainerResponseDTO);

        List<TrainerResponseDTO> result = trainerService.findAll();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(trainerResponseDTO, result.get(0));
    }

    @Test
    void findAll_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(trainerRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> trainerService.findAll());
    }

    @Test
    void update_ShouldReturnUpdatedTrainerResponseDTO_WhenTrainerExists() {
        when(trainingTypeRepository.findById(1L)).thenReturn(Optional.of(trainingType));
        when(trainerRepository.findByUsername("johndoe")).thenReturn(Optional.of(trainer));
        when(trainerMapper.toEntity(trainerRequestDTO, trainingType)).thenReturn(trainer);
        when(trainerRepository.update(trainer)).thenReturn(Optional.of(trainer));
        when(trainerMapper.toDTO(trainer)).thenReturn(trainerResponseDTO);

        TrainerResponseDTO result = trainerService.update(trainerRequestDTO);

        assertNotNull(result);
        assertEquals(trainerResponseDTO, result);
    }

    @Test
    void update_ShouldThrowIllegalArgumentException_WhenTrainerIsNull() {
        assertThrows(IllegalArgumentException.class, () -> trainerService.update(null));
    }

    @Test
    void update_ShouldThrowResourceNotFoundException_WhenTrainingTypeDoesNotExist() {
        when(trainingTypeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> trainerService.update(trainerRequestDTO));
    }

    @Test
    void update_ShouldThrowResourceNotFoundException_WhenTrainerDoesNotExist() {
        when(trainingTypeRepository.findById(1L)).thenReturn(Optional.of(trainingType));
        when(trainerRepository.findByUsername("johndoe")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> trainerService.update(trainerRequestDTO));
    }

    @Test
    void update_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(trainingTypeRepository.findById(1L)).thenReturn(Optional.of(trainingType));
        when(trainerRepository.findByUsername("johndoe")).thenReturn(Optional.of(trainer));
        when(trainerMapper.toEntity(trainerRequestDTO, trainingType)).thenReturn(trainer);
        when(trainerRepository.update(trainer)).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> trainerService.update(trainerRequestDTO));
    }

    @Test
    void deleteById_ShouldDeleteTrainer_WhenTrainerExists() {
        when(trainerRepository.deleteById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> trainerService.deleteById(1L));
    }

    @Test
    void deleteById_ShouldThrowIllegalArgumentException_WhenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> trainerService.deleteById(null));
    }

    @Test
    void deleteById_ShouldThrowResourceNotFoundException_WhenTrainerDoesNotExist() {
        when(trainerRepository.deleteById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> trainerService.deleteById(1L));
    }

    @Test
    void deleteById_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(trainerRepository.deleteById(1L)).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> trainerService.deleteById(1L));
    }

    @Test
    void findByUsername_ShouldReturnTrainerResponseDTO_WhenTrainerExists() {
        when(trainerRepository.findByUsername("johndoe")).thenReturn(Optional.of(trainer));
        when(trainerMapper.toDTO(trainer)).thenReturn(trainerResponseDTO);

        TrainerResponseDTO result = trainerService.findByUsername("johndoe");

        assertNotNull(result);
        assertEquals(trainerResponseDTO, result);
    }

    @Test
    void findByUsername_ShouldThrowIllegalArgumentException_WhenUsernameIsNull() {
        assertThrows(IllegalArgumentException.class, () -> trainerService.findByUsername(null));
    }

    @Test
    void findByUsername_ShouldThrowResourceNotFoundException_WhenTrainerDoesNotExist() {
        when(trainerRepository.findByUsername("johndoe")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> trainerService.findByUsername("johndoe"));
    }

    @Test
    void findByUsername_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(trainerRepository.findByUsername("johndoe")).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> trainerService.findByUsername("johndoe"));
    }

    @Test
    void getTrainersNotAssignedToTrainee_ShouldReturnListOfTrainerResponseDTO_WhenTrainersExist() {
        when(trainerRepository.findUnassignedTrainersByTraineeUsername("traineeUsername")).thenReturn(List.of(trainer));
        when(trainerMapper.toDTO(trainer)).thenReturn(trainerResponseDTO);

        List<TrainerResponseDTO> result = trainerService.findUnassignedTrainersByTraineeUsername("traineeUsername");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(trainerResponseDTO, result.get(0));
    }

    @Test
    void getTrainersNotAssignedToTrainee_ShouldThrowIllegalArgumentException_WhenUsernameIsNull() {
        assertThrows(IllegalArgumentException.class, () -> trainerService.findUnassignedTrainersByTraineeUsername(null));
    }

    @Test
    void getTrainersNotAssignedToTrainee_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(trainerRepository.findUnassignedTrainersByTraineeUsername("traineeUsername")).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> trainerService.findUnassignedTrainersByTraineeUsername("traineeUsername"));
    }

    @Test
    void setTrainerStatus_ShouldToggleStatus_WhenTrainerExists() {
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));

        assertDoesNotThrow(() -> trainerService.toggleTrainerStatus(1L));
        verify(trainerRepository).save(trainer);
    }

    @Test
    void setTrainerStatus_ShouldThrowIllegalArgumentException_WhenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> trainerService.toggleTrainerStatus(null));
    }

    @Test
    void setTrainerStatus_ShouldThrowResourceNotFoundException_WhenTrainerDoesNotExist() {
        when(trainerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> trainerService.toggleTrainerStatus(1L));
    }

    @Test
    void setTrainerStatus_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(trainerRepository.findById(1L)).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> trainerService.toggleTrainerStatus(1L));
    }

    @Test
    void updateTrainerPassword_ShouldUpdatePassword_WhenValidInput() {
        when(trainerRepository.changeTrainerPassword(1L, "newPassword")).thenReturn(true);

        assertTrue(trainerService.updateTrainerPassword(1L, "newPassword"));
    }

    @Test
    void updateTrainerPassword_ShouldThrowIllegalArgumentException_WhenInputIsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> trainerService.updateTrainerPassword(null, "newPassword"));
        assertThrows(IllegalArgumentException.class, () -> trainerService.updateTrainerPassword(1L, null));
        assertThrows(IllegalArgumentException.class, () -> trainerService.updateTrainerPassword(1L, ""));
    }

    @Test
    void updateTrainerPassword_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(trainerRepository.changeTrainerPassword(1L, "newPassword")).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> trainerService.updateTrainerPassword(1L, "newPassword"));
    }

    @Test
    void getTraineeTrainers_ShouldReturnSetOfTrainerResponseDTO_WhenTrainersExist() {
        when(trainerRepository.findTrainersByTraineeId(1L)).thenReturn(Set.of(trainer));
        when(trainerMapper.toDTO(trainer)).thenReturn(trainerResponseDTO);

        Set<TrainerResponseDTO> result = trainerService.findTrainersByTraineeId(1L);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(trainerResponseDTO, result.iterator().next());
    }

    @Test
    void getTraineeTrainers_ShouldThrowIllegalArgumentException_WhenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> trainerService.findTrainersByTraineeId(null));
    }

    @Test
    void getTraineeTrainers_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(trainerRepository.findTrainersByTraineeId(1L)).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> trainerService.findTrainersByTraineeId(1L));
    }
}