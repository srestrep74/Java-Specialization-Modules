package com.sro.SpringCoreTask1.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sro.SpringCoreTask1.dto.request.TraineeRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TraineeResponseDTO;
import com.sro.SpringCoreTask1.entity.Trainee;
import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.exception.DatabaseOperationException;
import com.sro.SpringCoreTask1.exception.ResourceNotFoundException;
import com.sro.SpringCoreTask1.exception.ResourceAlreadyExistsException;
import com.sro.SpringCoreTask1.mappers.TraineeMapper;
import com.sro.SpringCoreTask1.repository.TraineeRepository;
import com.sro.SpringCoreTask1.repository.TrainerRepository;
import com.sro.SpringCoreTask1.service.impl.TraineeServiceImpl;

import jakarta.validation.ConstraintViolationException;

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
class TraineeServiceImplTest {

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TraineeMapper traineeMapper;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    private Trainee trainee;
    private TraineeRequestDTO traineeRequestDTO;
    private TraineeResponseDTO traineeResponseDTO;

    @BeforeEach
    void setUp() {
        trainee = new Trainee();
        trainee.setId(1L);
        trainee.setFirstName("John");
        trainee.setLastName("Doe");
        trainee.setUsername("johndoe");
        trainee.setPassword("password");
        trainee.setActive(true);
        trainee.setAddress("123 Street");
        trainee.setDateOfBirth(LocalDate.of(2000, 1, 1));

        traineeRequestDTO = new TraineeRequestDTO("John", "Doe", "johndoe", "password", true, "123 Street", LocalDate.of(2000, 1, 1), null);
        traineeResponseDTO = new TraineeResponseDTO(1L, "John", "Doe", "johndoe", true, "123 Street", LocalDate.of(2000, 1, 1));
    }

    @Test
    void save_ShouldReturnTraineeResponseDTO_WhenValidInput() {
        when(traineeMapper.toEntity(traineeRequestDTO)).thenReturn(trainee);
        when(traineeRepository.save(trainee)).thenReturn(trainee);
        when(traineeMapper.toDTO(trainee)).thenReturn(traineeResponseDTO);

        TraineeResponseDTO result = traineeService.save(traineeRequestDTO);

        assertNotNull(result);
        assertEquals(traineeResponseDTO, result);
        verify(traineeRepository).save(trainee);
    }

    @Test
    void save_ShouldThrowIllegalArgumentException_WhenTraineeIsNull() {
        assertThrows(IllegalArgumentException.class, () -> traineeService.save(null));
    }

    @Test
    void save_ShouldThrowIllegalArgumentException_WhenFirstNameIsNull() {
        TraineeRequestDTO invalidRequest = new TraineeRequestDTO(null, "Doe", "johndoe", "password", true, "123 Street", LocalDate.of(2000, 1, 1), List.of(1L));
        assertThrows(IllegalArgumentException.class, () -> traineeService.save(invalidRequest));
    }

    @Test
    void save_ShouldThrowResourceAlreadyExistsException_WhenUsernameExists() {
        when(traineeMapper.toEntity(traineeRequestDTO)).thenReturn(trainee);
        when(traineeRepository.save(trainee)).thenThrow(new ConstraintViolationException("Username already exists", null));

        assertThrows(ResourceAlreadyExistsException.class, () -> traineeService.save(traineeRequestDTO));
    }

    @Test
    void save_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(traineeMapper.toEntity(traineeRequestDTO)).thenReturn(trainee);
        when(traineeRepository.save(trainee)).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> traineeService.save(traineeRequestDTO));
    }

    @Test
    void findById_ShouldReturnTraineeResponseDTO_WhenTraineeExists() {
        when(traineeRepository.findById(1L)).thenReturn(Optional.of(trainee));
        when(traineeMapper.toDTO(trainee)).thenReturn(traineeResponseDTO);

        TraineeResponseDTO result = traineeService.findById(1L);

        assertNotNull(result);
        assertEquals(traineeResponseDTO, result);
    }

    @Test
    void findById_ShouldThrowIllegalArgumentException_WhenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> traineeService.findById(null));
    }

    @Test
    void findById_ShouldThrowResourceNotFoundException_WhenTraineeDoesNotExist() {
        when(traineeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> traineeService.findById(1L));
    }

    @Test
    void findById_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(traineeRepository.findById(1L)).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> traineeService.findById(1L));
    }

    @Test
    void findAll_ShouldReturnListOfTraineeResponseDTO_WhenTraineesExist() {
        when(traineeRepository.findAll()).thenReturn(List.of(trainee));
        when(traineeMapper.toDTO(trainee)).thenReturn(traineeResponseDTO);

        List<TraineeResponseDTO> result = traineeService.findAll();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(traineeResponseDTO, result.get(0));
    }

    @Test
    void findAll_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(traineeRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> traineeService.findAll());
    }

    @Test
    void update_ShouldReturnUpdatedTraineeResponseDTO_WhenTraineeExists() {
        when(traineeRepository.findByUsername("johndoe")).thenReturn(Optional.of(trainee));
        when(traineeMapper.toEntity(traineeRequestDTO)).thenReturn(trainee);
        when(traineeRepository.update(trainee)).thenReturn(Optional.of(trainee));
        when(traineeMapper.toDTO(trainee)).thenReturn(traineeResponseDTO);

        TraineeResponseDTO result = traineeService.update(traineeRequestDTO);

        assertNotNull(result);
        assertEquals(traineeResponseDTO, result);
    }

    @Test
    void update_ShouldThrowIllegalArgumentException_WhenTraineeIsNull() {
        assertThrows(IllegalArgumentException.class, () -> traineeService.update(null));
    }

    @Test
    void update_ShouldThrowResourceNotFoundException_WhenTraineeDoesNotExist() {
        when(traineeRepository.findByUsername("johndoe")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> traineeService.update(traineeRequestDTO));
    }

    @Test
    void update_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(traineeRepository.findByUsername("johndoe")).thenReturn(Optional.of(trainee));
        when(traineeMapper.toEntity(traineeRequestDTO)).thenReturn(trainee);
        when(traineeRepository.update(trainee)).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> traineeService.update(traineeRequestDTO));
    }

    @Test
    void deleteById_ShouldDeleteTrainee_WhenTraineeExists() {
        when(traineeRepository.deleteById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> traineeService.deleteById(1L));
    }

    @Test
    void deleteById_ShouldThrowIllegalArgumentException_WhenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> traineeService.deleteById(null));
    }

    @Test
    void deleteById_ShouldThrowResourceNotFoundException_WhenTraineeDoesNotExist() {
        when(traineeRepository.deleteById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> traineeService.deleteById(1L));
    }

    @Test
    void deleteById_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(traineeRepository.deleteById(1L)).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> traineeService.deleteById(1L));
    }

    @Test
    void findByUsername_ShouldReturnTraineeResponseDTO_WhenTraineeExists() {
        when(traineeRepository.findByUsername("johndoe")).thenReturn(Optional.of(trainee));
        when(traineeMapper.toDTO(trainee)).thenReturn(traineeResponseDTO);

        TraineeResponseDTO result = traineeService.findByUsername("johndoe");

        assertNotNull(result);
        assertEquals(traineeResponseDTO, result);
    }

    @Test
    void findByUsername_ShouldThrowIllegalArgumentException_WhenUsernameIsNull() {
        assertThrows(IllegalArgumentException.class, () -> traineeService.findByUsername(null));
    }

    @Test
    void findByUsername_ShouldThrowResourceNotFoundException_WhenTraineeDoesNotExist() {
        when(traineeRepository.findByUsername("johndoe")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> traineeService.findByUsername("johndoe"));
    }

    @Test
    void findByUsername_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(traineeRepository.findByUsername("johndoe")).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> traineeService.findByUsername("johndoe"));
    }

    @Test
    void deleteByUsername_ShouldDeleteTrainee_WhenTraineeExists() {
        when(traineeRepository.deleteByUsername("johndoe")).thenReturn(true);

        assertDoesNotThrow(() -> traineeService.deleteByUsername("johndoe"));
    }

    @Test
    void deleteByUsername_ShouldThrowIllegalArgumentException_WhenUsernameIsNull() {
        assertThrows(IllegalArgumentException.class, () -> traineeService.deleteByUsername(null));
    }

    @Test
    void deleteByUsername_ShouldThrowResourceNotFoundException_WhenTraineeDoesNotExist() {
        when(traineeRepository.deleteByUsername("johndoe")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> traineeService.deleteByUsername("johndoe"));
    }

    @Test
    void deleteByUsername_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(traineeRepository.deleteByUsername("johndoe")).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> traineeService.deleteByUsername("johndoe"));
    }

    @Test
    void addTrainerToTrainee_ShouldAddTrainer_WhenValidInput() {
        Trainer trainer = new Trainer();
        trainer.setId(1L);
        trainer.setActive(true);

        when(traineeRepository.findById(1L)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));

        assertDoesNotThrow(() -> traineeService.addTrainerToTrainee(1L, 1L));
        verify(traineeRepository).save(trainee);
    }

    @Test
    void addTrainerToTrainee_ShouldThrowIllegalArgumentException_WhenIdsAreNull() {
        assertThrows(IllegalArgumentException.class, () -> traineeService.addTrainerToTrainee(null, null));
    }

    @Test
    void addTrainerToTrainee_ShouldThrowResourceNotFoundException_WhenTraineeDoesNotExist() {
        when(traineeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> traineeService.addTrainerToTrainee(1L, 1L));
    }

    @Test
    void addTrainerToTrainee_ShouldThrowResourceNotFoundException_WhenTrainerDoesNotExist() {
        when(traineeRepository.findById(1L)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> traineeService.addTrainerToTrainee(1L, 1L));
    }

    @Test
    void addTrainerToTrainee_ShouldThrowResourceAlreadyExistsException_WhenTrainerAlreadyAssigned() {
        Trainer trainer = new Trainer();
        trainer.setId(1L);
        trainer.setActive(true);
        trainee.addTrainer(trainer);

        when(traineeRepository.findById(1L)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));

        assertThrows(ResourceAlreadyExistsException.class, () -> traineeService.addTrainerToTrainee(1L, 1L));
    }

    @Test
    void addTrainerToTrainee_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(traineeRepository.findById(1L)).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> traineeService.addTrainerToTrainee(1L, 1L));
    }

    @Test
    void removeTrainerFromTrainee_ShouldRemoveTrainer_WhenValidInput() {
        Trainer trainer = new Trainer();
        trainer.setId(1L);
        trainer.setActive(true);
        trainee.addTrainer(trainer);

        when(traineeRepository.findById(1L)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));

        assertDoesNotThrow(() -> traineeService.removeTrainerFromTrainee(1L, 1L));
        verify(trainerRepository).save(trainer);
    }

    @Test
    void removeTrainerFromTrainee_ShouldThrowIllegalArgumentException_WhenIdsAreNull() {
        assertThrows(IllegalArgumentException.class, () -> traineeService.removeTrainerFromTrainee(null, null));
    }

    @Test
    void removeTrainerFromTrainee_ShouldThrowResourceNotFoundException_WhenTraineeDoesNotExist() {
        when(traineeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> traineeService.removeTrainerFromTrainee(1L, 1L));
    }

    @Test
    void removeTrainerFromTrainee_ShouldThrowResourceNotFoundException_WhenTrainerDoesNotExist() {
        when(traineeRepository.findById(1L)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> traineeService.removeTrainerFromTrainee(1L, 1L));
    }

    @Test
    void removeTrainerFromTrainee_ShouldThrowResourceNotFoundException_WhenTrainerNotAssigned() {
        Trainer trainer = new Trainer();
        trainer.setId(1L);
        trainer.setActive(true);

        when(traineeRepository.findById(1L)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));

        assertThrows(ResourceNotFoundException.class, () -> traineeService.removeTrainerFromTrainee(1L, 1L));
    }

    @Test
    void removeTrainerFromTrainee_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(traineeRepository.findById(1L)).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> traineeService.removeTrainerFromTrainee(1L, 1L));
    }

    @Test
    void setTraineeStatus_ShouldToggleStatus_WhenTraineeExists() {
        when(traineeRepository.findById(1L)).thenReturn(Optional.of(trainee));

        assertDoesNotThrow(() -> traineeService.setTraineeStatus(1L));
        verify(traineeRepository).save(trainee);
    }

    @Test
    void setTraineeStatus_ShouldThrowIllegalArgumentException_WhenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> traineeService.setTraineeStatus(null));
    }

    @Test
    void setTraineeStatus_ShouldThrowResourceNotFoundException_WhenTraineeDoesNotExist() {
        when(traineeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> traineeService.setTraineeStatus(1L));
    }

    @Test
    void setTraineeStatus_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(traineeRepository.findById(1L)).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> traineeService.setTraineeStatus(1L));
    }

    @Test
    void updateTraineePassword_ShouldUpdatePassword_WhenValidInput() {
        when(traineeRepository.updatePassword(1L, "newPassword")).thenReturn(true);

        assertTrue(traineeService.updateTraineePassword(1L, "newPassword"));
    }

    @Test
    void updateTraineePassword_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(traineeRepository.updatePassword(1L, "newPassword")).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> traineeService.updateTraineePassword(1L, "newPassword"));
    }
}