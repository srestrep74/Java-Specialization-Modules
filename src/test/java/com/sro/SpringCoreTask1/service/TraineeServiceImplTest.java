package com.sro.SpringCoreTask1.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sro.SpringCoreTask1.dtos.v1.request.trainee.RegisterTraineeRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.trainee.UpdateTraineeProfileRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.trainee.UpdateTraineeTrainerListRequest;
import com.sro.SpringCoreTask1.dtos.v1.response.trainee.RegisterTraineeResponse;
import com.sro.SpringCoreTask1.dtos.v1.response.trainee.TraineeProfileResponse;
import com.sro.SpringCoreTask1.entity.Trainee;
import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.exception.DatabaseOperationException;
import com.sro.SpringCoreTask1.exception.ResourceNotFoundException;
import com.sro.SpringCoreTask1.exception.ResourceAlreadyExistsException;
import com.sro.SpringCoreTask1.mappers.trainee.TraineeCreateMapper;
import com.sro.SpringCoreTask1.mappers.trainee.TraineeResponseMapper;
import com.sro.SpringCoreTask1.mappers.trainee.TraineeUpdateMapper;
import com.sro.SpringCoreTask1.mappers.trainer.TrainerResponseMapper;
import com.sro.SpringCoreTask1.repository.TraineeRepository;
import com.sro.SpringCoreTask1.repository.TrainerRepository;
import com.sro.SpringCoreTask1.service.impl.TraineeServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TraineeCreateMapper traineeCreateMapper;

    @Mock
    private TraineeUpdateMapper traineeUpdateMapper;

    @Mock
    private TraineeResponseMapper traineeResponseMapper;

    @Mock
    private TrainerResponseMapper trainerResponseMapper;

    @Mock
    private AuthService authService;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    private Trainee trainee;
    private Trainer trainer;
    private RegisterTraineeRequest registerTraineeRequest;
    private RegisterTraineeResponse registerTraineeResponse;
    private TraineeProfileResponse traineeProfileResponse;
    private UpdateTraineeProfileRequest updateTraineeProfileRequest;
    private UpdateTraineeTrainerListRequest updateTraineeTrainerListRequest;

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

        trainer = new Trainer();
        trainer.setId(1L);
        trainer.setFirstName("Trainer");
        trainer.setLastName("One");
        trainer.setUsername("trainer1");
        trainer.setActive(true);

        registerTraineeRequest = new RegisterTraineeRequest(
            "John", 
            "Doe", 
            LocalDate.of(2000, 1, 1), 
            "123 Street"
        );

        registerTraineeResponse = new RegisterTraineeResponse(
            "johndoe", 
            "generatedPassword",
            "TRAINEE"
        );

        traineeProfileResponse = new TraineeProfileResponse(
            "John", 
            "Doe", 
            LocalDate.of(2000, 1, 1),
            "123 Street",
            true,
            List.of(new TraineeProfileResponse.TrainerInfo(
                "trainer1",
                "Trainer",
                "One",
                1L
            ))
        );

        updateTraineeProfileRequest = new UpdateTraineeProfileRequest(
            "John", 
            "Updated", 
            LocalDate.of(2000, 1, 1),
            "123 Updated Street", 
            true
        );

        updateTraineeTrainerListRequest = new UpdateTraineeTrainerListRequest(
            List.of("trainer1")
        );
    }

    @Test
    void save_ShouldReturnRegisterTraineeResponse_WhenValidInput() {
        when(traineeCreateMapper.toEntity(registerTraineeRequest)).thenReturn(trainee);
        when(traineeRepository.save(trainee)).thenReturn(trainee);
        when(traineeCreateMapper.toRegisterResponse(trainee, "TRAINEE")).thenReturn(registerTraineeResponse);

        RegisterTraineeResponse result = traineeService.save(registerTraineeRequest);

        assertNotNull(result);
        assertEquals(registerTraineeResponse, result);
        verify(traineeRepository).save(trainee);
    }

    @Test
    void save_ShouldThrowIllegalArgumentException_WhenTraineeIsNull() {
        assertThrows(IllegalArgumentException.class, () -> traineeService.save(null));
    }

    @Test
    void save_ShouldThrowIllegalArgumentException_WhenRequiredFieldsAreMissing() {
        RegisterTraineeRequest invalidRequest = new RegisterTraineeRequest(null, null, null, null);
        assertThrows(IllegalArgumentException.class, () -> traineeService.save(invalidRequest));
    }

    @Test
    void save_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(traineeCreateMapper.toEntity(registerTraineeRequest)).thenReturn(trainee);
        when(traineeRepository.save(trainee)).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> traineeService.save(registerTraineeRequest));
    }

    @Test
    void findById_ShouldReturnTraineeProfileResponse_WhenTraineeExists() {
        when(traineeRepository.findById(1L)).thenReturn(Optional.of(trainee));
        when(traineeResponseMapper.toProfileResponse(trainee)).thenReturn(traineeProfileResponse);

        TraineeProfileResponse result = traineeService.findById(1L);

        assertNotNull(result);
        assertEquals(traineeProfileResponse, result);
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
    void findAll_ShouldReturnListOfTraineeProfileResponse_WhenTraineesExist() {
        when(traineeRepository.findAll()).thenReturn(List.of(trainee));
        when(traineeResponseMapper.toProfileResponse(trainee)).thenReturn(traineeProfileResponse);

        List<TraineeProfileResponse> result = traineeService.findAll();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(traineeProfileResponse, result.get(0));
    }

    @Test
    void findAll_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(traineeRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> traineeService.findAll());
    }

    @Test
    void update_ShouldReturnUpdatedTraineeProfileResponse_WhenTraineeExists() {
        when(traineeRepository.findByUsername("johndoe")).thenReturn(Optional.of(trainee));
        when(traineeUpdateMapper.toEntity(updateTraineeProfileRequest, trainee)).thenReturn(trainee);
        when(traineeRepository.save(trainee)).thenReturn(trainee);
        when(traineeResponseMapper.toProfileResponse(trainee)).thenReturn(traineeProfileResponse);

        TraineeProfileResponse result = traineeService.update("johndoe", updateTraineeProfileRequest);

        assertNotNull(result);
        assertEquals(traineeProfileResponse, result);
    }

    @Test
    void update_ShouldThrowIllegalArgumentException_WhenTraineeIsNull() {
        assertThrows(IllegalArgumentException.class, () -> traineeService.update("johndoe", null));
    }

    @Test
    void update_ShouldThrowResourceNotFoundException_WhenTraineeDoesNotExist() {
        when(traineeRepository.findByUsername("johndoe")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
            () -> traineeService.update("johndoe", updateTraineeProfileRequest));
    }

    @Test
    void update_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(traineeRepository.findByUsername("johndoe")).thenReturn(Optional.of(trainee));
        when(traineeUpdateMapper.toEntity(updateTraineeProfileRequest, trainee)).thenReturn(trainee);
        when(traineeRepository.save(trainee)).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, 
            () -> traineeService.update("johndoe", updateTraineeProfileRequest));
    }

    @Test
    void deleteById_ShouldDeleteTrainee_WhenTraineeExists() {
        when(traineeRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> traineeService.deleteById(1L));
    }

    @Test
    void deleteById_ShouldThrowIllegalArgumentException_WhenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> traineeService.deleteById(null));
    }

    @Test
    void deleteById_ShouldThrowResourceNotFoundException_WhenTraineeDoesNotExist() {
        when(traineeRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> traineeService.deleteById(1L));
    }

    @Test
    void deleteById_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(traineeRepository.existsById(1L)).thenReturn(true);
        doThrow(new RuntimeException("Database error")).when(traineeRepository).deleteById(1L);

        assertThrows(DatabaseOperationException.class, () -> traineeService.deleteById(1L));
    }

    @Test
    void findByUsername_ShouldReturnTraineeProfileResponse_WhenTraineeExists() {
        when(traineeRepository.findByUsername("johndoe")).thenReturn(Optional.of(trainee));
        when(traineeResponseMapper.toProfileResponse(trainee)).thenReturn(traineeProfileResponse);

        TraineeProfileResponse result = traineeService.findByUsername("johndoe");

        assertNotNull(result);
        assertEquals(traineeProfileResponse, result);
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
        when(traineeRepository.existsByUsername("johndoe")).thenReturn(true);

        assertDoesNotThrow(() -> traineeService.deleteByUsername("johndoe"));
    }

    @Test
    void deleteByUsername_ShouldThrowIllegalArgumentException_WhenUsernameIsNull() {
        assertThrows(IllegalArgumentException.class, () -> traineeService.deleteByUsername(null));
    }

    @Test
    void deleteByUsername_ShouldThrowResourceNotFoundException_WhenTraineeDoesNotExist() {
        when(traineeRepository.existsByUsername("johndoe")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> traineeService.deleteByUsername("johndoe"));
    }

    @Test
    void deleteByUsername_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(traineeRepository.existsByUsername("johndoe")).thenReturn(true);
        when(traineeRepository.deleteByUsername("johndoe")).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> traineeService.deleteByUsername("johndoe"));
    }

    @Test
    void addTrainerToTrainee_ShouldAddTrainer_WhenValidInput() {
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
    void updateTraineeTrainers_ShouldThrowIllegalArgumentException_WhenUsernameIsNull() {
        assertThrows(IllegalArgumentException.class, 
            () -> traineeService.updateTraineeTrainers(null, updateTraineeTrainerListRequest));
    }

    @Test
    void updateTraineeTrainers_ShouldThrowResourceNotFoundException_WhenTraineeDoesNotExist() {
        when(traineeRepository.findByUsername("johndoe")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
            () -> traineeService.updateTraineeTrainers("johndoe", updateTraineeTrainerListRequest));
    }

    @Test
    void updateTraineeTrainers_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(traineeRepository.findByUsername("johndoe")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsername("trainer1")).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, 
            () -> traineeService.updateTraineeTrainers("johndoe", updateTraineeTrainerListRequest));
    }

    @Test
    void updateActivationStatus_ShouldUpdateStatus_WhenValidInput() {
        when(traineeRepository.findByUsername("johndoe")).thenReturn(Optional.of(trainee));

        assertDoesNotThrow(() -> traineeService.updateActivationStatus("johndoe", false));
        verify(traineeRepository).save(trainee);
    }

    @Test
    void updateActivationStatus_ShouldThrowIllegalArgumentException_WhenUsernameIsNull() {
        assertThrows(IllegalArgumentException.class, () -> traineeService.updateActivationStatus(null, true));
    }

    @Test
    void updateActivationStatus_ShouldThrowResourceNotFoundException_WhenTraineeDoesNotExist() {
        when(traineeRepository.findByUsername("johndoe")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> traineeService.updateActivationStatus("johndoe", true));
    }

    @Test
    void updateActivationStatus_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(traineeRepository.findByUsername("johndoe")).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> traineeService.updateActivationStatus("johndoe", true));
    }

    @Test
    void updateTraineePassword_ShouldUpdatePassword_WhenValidInput() {
        when(traineeRepository.updatePassword(1L, "newPassword")).thenReturn(1);

        assertTrue(traineeService.updateTraineePassword(1L, "newPassword"));
    }

    @Test
    void updateTraineePassword_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(traineeRepository.updatePassword(1L, "newPassword")).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> traineeService.updateTraineePassword(1L, "newPassword"));
    }

    @Test
    void findTrainersByTraineeId_ShouldReturnTrainers_WhenTraineeExists() {
        when(traineeRepository.findTrainersByTraineeId(1L)).thenReturn(Set.of(trainer));

        Set<Trainer> result = traineeService.findTrainersByTraineeId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(trainer));
    }

    @Test
    void findTrainersByTraineeId_ShouldThrowIllegalArgumentException_WhenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> traineeService.findTrainersByTraineeId(null));
    }

    @Test
    void findTrainersByTraineeId_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(traineeRepository.findTrainersByTraineeId(1L)).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> traineeService.findTrainersByTraineeId(1L));
    }
}