package com.sro.SpringCoreTask1.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sro.SpringCoreTask1.dtos.v1.request.trainer.RegisterTrainerRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.trainer.UpdateTrainerProfileRequest;
import com.sro.SpringCoreTask1.dtos.v1.response.trainer.RegisterTrainerResponse;
import com.sro.SpringCoreTask1.dtos.v1.response.trainer.TrainerProfileResponse;
import com.sro.SpringCoreTask1.dtos.v1.response.trainer.UnassignedTrainerResponse;
import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.entity.TrainingType;
import com.sro.SpringCoreTask1.exception.DatabaseOperationException;
import com.sro.SpringCoreTask1.exception.ResourceAlreadyExistsException;
import com.sro.SpringCoreTask1.exception.ResourceNotFoundException;
import com.sro.SpringCoreTask1.mappers.trainer.TrainerCreateMapper;
import com.sro.SpringCoreTask1.mappers.trainer.TrainerResponseMapper;
import com.sro.SpringCoreTask1.mappers.trainer.TrainerUpdateMapper;
import com.sro.SpringCoreTask1.repository.TrainerRepository;
import com.sro.SpringCoreTask1.repository.TrainingTypeRepository;
import com.sro.SpringCoreTask1.service.impl.TrainerServiceImpl;

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
    private TrainerCreateMapper trainerCreateMapper;

    @Mock
    private TrainerUpdateMapper trainerUpdateMapper;

    @Mock
    private TrainerResponseMapper trainerResponseMapper;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    private Trainer trainer;
    private TrainingType trainingType;
    private RegisterTrainerRequest registerTrainerRequest;
    private RegisterTrainerResponse registerTrainerResponse;
    private TrainerProfileResponse trainerProfileResponse;
    private UpdateTrainerProfileRequest updateTrainerProfileRequest;
    private UnassignedTrainerResponse unassignedTrainerResponse;

    @BeforeEach
    void setUp() {
        trainingType = new TrainingType();
        trainingType.setId(1L);
        trainingType.setTrainingTypeName("Fitness");
    
        trainer = new Trainer();
        trainer.setId(1L);
        trainer.setFirstName("John");
        trainer.setLastName("Doe");
        trainer.setUsername("johndoe");
        trainer.setPassword("password");
        trainer.setActive(true);
        trainer.setTrainingType(trainingType);
    
        registerTrainerRequest = new RegisterTrainerRequest(
            "John", "Doe", 1L
        );
    
        registerTrainerResponse = new RegisterTrainerResponse(
            "johndoe", "generatedPassword"
        );
    
        trainerProfileResponse = new TrainerProfileResponse(
            "John", 
            "Doe", 
            1L, 
            true, 
            List.of() 
        );
    
        updateTrainerProfileRequest = new UpdateTrainerProfileRequest(
            "John", "Doe", 1L, true
        );
    
        unassignedTrainerResponse = new UnassignedTrainerResponse(
            "johndoe", 
            "John", 
            "Doe", 
            1L  
        );
    }

    @Test
    void save_ShouldReturnRegisterTrainerResponse_WhenValidInput() {
        when(trainingTypeRepository.findById(1L)).thenReturn(Optional.of(trainingType));
        when(trainerCreateMapper.toEntity(registerTrainerRequest, trainingType)).thenReturn(trainer);
        when(trainerRepository.save(trainer)).thenReturn(trainer);
        when(trainerCreateMapper.toRegisterResponse(trainer)).thenReturn(registerTrainerResponse);

        RegisterTrainerResponse result = trainerService.save(registerTrainerRequest);

        assertNotNull(result);
        assertEquals(registerTrainerResponse, result);
        verify(trainerRepository).save(trainer);
    }

    @Test
    void save_ShouldThrowIllegalArgumentException_WhenTrainerIsNull() {
        assertThrows(IllegalArgumentException.class, () -> trainerService.save(null));
    }

    @Test
    void save_ShouldThrowIllegalArgumentException_WhenRequiredFieldsAreMissing() {
        RegisterTrainerRequest invalidRequest = new RegisterTrainerRequest(null, null, null);
        assertThrows(IllegalArgumentException.class, () -> trainerService.save(invalidRequest));
    }

    @Test
    void save_ShouldThrowResourceNotFoundException_WhenTrainingTypeDoesNotExist() {
        when(trainingTypeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> trainerService.save(registerTrainerRequest));
    }

    @Test
    void save_ShouldThrowResourceAlreadyExistsException_WhenUsernameExists() {
        when(trainingTypeRepository.findById(1L)).thenReturn(Optional.of(trainingType));
        when(trainerCreateMapper.toEntity(registerTrainerRequest, trainingType)).thenReturn(trainer);
        when(trainerRepository.save(trainer)).thenThrow(new ConstraintViolationException("Username already exists", null));

        assertThrows(ResourceAlreadyExistsException.class, () -> trainerService.save(registerTrainerRequest));
    }

    @Test
    void save_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(trainingTypeRepository.findById(1L)).thenReturn(Optional.of(trainingType));
        when(trainerCreateMapper.toEntity(registerTrainerRequest, trainingType)).thenReturn(trainer);
        when(trainerRepository.save(trainer)).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> trainerService.save(registerTrainerRequest));
    }

    @Test
    void findById_ShouldReturnTrainerProfileResponse_WhenTrainerExists() {
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));
        when(trainerResponseMapper.toTrainerProfileResponse(trainer)).thenReturn(trainerProfileResponse);

        TrainerProfileResponse result = trainerService.findById(1L);

        assertNotNull(result);
        assertEquals(trainerProfileResponse, result);
    }

    @Test
    void findById_ShouldThrowIllegalArgumentException_WhenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> trainerService.findById(null));
    }

    @Test
    void findById_ShouldThrowResourceNotFoundException_WhenTrainerDoesNotExist() {
        when(trainerRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(DatabaseOperationException.class, 
            () -> trainerService.findById(1L));
        
        assertTrue(exception.getCause() instanceof ResourceNotFoundException);
    }

    @Test
    void findById_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(trainerRepository.findById(1L)).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> trainerService.findById(1L));
    }

    @Test
    void findAll_ShouldReturnListOfTrainerProfileResponse_WhenTrainersExist() {
        when(trainerRepository.findAll()).thenReturn(List.of(trainer));
        when(trainerResponseMapper.toTrainerProfileResponse(trainer)).thenReturn(trainerProfileResponse);

        List<TrainerProfileResponse> result = trainerService.findAll();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(trainerProfileResponse, result.get(0));
    }

    @Test
    void findAll_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(trainerRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> trainerService.findAll());
    }

    @Test
    void update_ShouldReturnUpdatedTrainerProfileResponse_WhenTrainerExists() {
        when(trainingTypeRepository.findById(1L)).thenReturn(Optional.of(trainingType));
        when(trainerRepository.findByUsername("johndoe")).thenReturn(Optional.of(trainer));
        when(trainerUpdateMapper.toEntity(updateTrainerProfileRequest, trainer, trainingType))
            .thenReturn(trainer);
        when(trainerRepository.save(trainer)).thenReturn(trainer);
        when(trainerResponseMapper.toTrainerProfileResponse(trainer)).thenReturn(trainerProfileResponse);

        TrainerProfileResponse result = trainerService.update("johndoe", updateTrainerProfileRequest);

        assertNotNull(result);
        assertEquals(trainerProfileResponse, result);
    }

    @Test
    void update_ShouldThrowIllegalArgumentException_WhenTrainerIsNull() {
        assertThrows(IllegalArgumentException.class, () -> trainerService.update("johndoe", null));
    }

    @Test
    void update_ShouldThrowIllegalArgumentException_WhenUsernameIsNull() {
        assertThrows(IllegalArgumentException.class, 
            () -> trainerService.update(null, updateTrainerProfileRequest));
    }

    @Test
    void update_ShouldThrowResourceNotFoundException_WhenTrainingTypeDoesNotExist() {
        when(trainingTypeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
            () -> trainerService.update("johndoe", updateTrainerProfileRequest));
    }

    @Test
    void update_ShouldThrowResourceNotFoundException_WhenTrainerDoesNotExist() {
        when(trainingTypeRepository.findById(1L)).thenReturn(Optional.of(trainingType));
        when(trainerRepository.findByUsername("johndoe")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
            () -> trainerService.update("johndoe", updateTrainerProfileRequest));
    }

    @Test
    void update_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(trainingTypeRepository.findById(1L)).thenReturn(Optional.of(trainingType));
        when(trainerRepository.findByUsername("johndoe")).thenReturn(Optional.of(trainer));
        when(trainerUpdateMapper.toEntity(updateTrainerProfileRequest, trainer, trainingType))
            .thenReturn(trainer);
        when(trainerRepository.save(trainer)).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, 
            () -> trainerService.update("johndoe", updateTrainerProfileRequest));
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
    void findByUsername_ShouldReturnTrainerProfileResponse_WhenTrainerExists() {
        when(trainerRepository.findByUsername("johndoe")).thenReturn(Optional.of(trainer));
        when(trainerResponseMapper.toTrainerProfileResponse(trainer)).thenReturn(trainerProfileResponse);

        TrainerProfileResponse result = trainerService.findByUsername("johndoe");

        assertNotNull(result);
        assertEquals(trainerProfileResponse, result);
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
    void findUnassignedTrainersByTraineeUsername_ShouldReturnList_WhenTrainersExist() {
        when(trainerRepository.findUnassignedTrainersByTraineeUsername("traineeUser"))
            .thenReturn(List.of(trainer));
        when(trainerResponseMapper.toUnassignedTrainerResponse(trainer))
            .thenReturn(unassignedTrainerResponse);

        List<UnassignedTrainerResponse> result = 
            trainerService.findUnassignedTrainersByTraineeUsername("traineeUser");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(unassignedTrainerResponse, result.get(0));
    }

    @Test
    void findUnassignedTrainersByTraineeUsername_ShouldThrowIllegalArgumentException_WhenUsernameIsNull() {
        assertThrows(IllegalArgumentException.class, 
            () -> trainerService.findUnassignedTrainersByTraineeUsername(null));
    }

    @Test
    void findUnassignedTrainersByTraineeUsername_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(trainerRepository.findUnassignedTrainersByTraineeUsername("traineeUser"))
            .thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, 
            () -> trainerService.findUnassignedTrainersByTraineeUsername("traineeUser"));
    }

    @Test
    void updateActivationStatus_ShouldUpdateStatus_WhenTrainerExists() {
        when(trainerRepository.findByUsername("johndoe")).thenReturn(Optional.of(trainer));

        assertDoesNotThrow(() -> trainerService.updateActivationStatus("johndoe", false));
        verify(trainerRepository).save(trainer);
    }

    @Test
    void updateActivationStatus_ShouldThrowIllegalArgumentException_WhenUsernameIsNull() {
        assertThrows(IllegalArgumentException.class, 
            () -> trainerService.updateActivationStatus(null, true));
    }

    @Test
    void updateActivationStatus_ShouldThrowResourceNotFoundException_WhenTrainerDoesNotExist() {
        when(trainerRepository.findByUsername("johndoe")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
            () -> trainerService.updateActivationStatus("johndoe", true));
    }

    @Test
    void updateActivationStatus_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(trainerRepository.findByUsername("johndoe")).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, 
            () -> trainerService.updateActivationStatus("johndoe", true));
    }

    @Test
    void updateTrainerPassword_ShouldUpdatePassword_WhenValidInput() {
        when(trainerRepository.changeTrainerPassword(1L, "newPassword")).thenReturn(true);

        assertTrue(trainerService.updateTrainerPassword(1L, "newPassword"));
    }

    @Test
    void updateTrainerPassword_ShouldThrowIllegalArgumentException_WhenInputIsInvalid() {
        assertThrows(IllegalArgumentException.class, 
            () -> trainerService.updateTrainerPassword(null, "newPassword"));
        assertThrows(IllegalArgumentException.class, 
            () -> trainerService.updateTrainerPassword(1L, null));
        assertThrows(IllegalArgumentException.class, 
            () -> trainerService.updateTrainerPassword(1L, ""));
    }

    @Test
    void updateTrainerPassword_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(trainerRepository.changeTrainerPassword(1L, "newPassword"))
            .thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, 
            () -> trainerService.updateTrainerPassword(1L, "newPassword"));
    }

    @Test
    void findTrainersByTraineeId_ShouldReturnSet_WhenTrainersExist() {
        when(trainerRepository.findTrainersByTraineeId(1L)).thenReturn(Set.of(trainer));
        when(trainerResponseMapper.toTrainerProfileResponse(trainer)).thenReturn(trainerProfileResponse);

        Set<TrainerProfileResponse> result = trainerService.findTrainersByTraineeId(1L);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(trainerProfileResponse, result.iterator().next());
    }

    @Test
    void findTrainersByTraineeId_ShouldThrowIllegalArgumentException_WhenIdIsNull() {
        assertThrows(IllegalArgumentException.class, 
            () -> trainerService.findTrainersByTraineeId(null));
    }

    @Test
    void findTrainersByTraineeId_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(trainerRepository.findTrainersByTraineeId(1L)).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, 
            () -> trainerService.findTrainersByTraineeId(1L));
    }
}