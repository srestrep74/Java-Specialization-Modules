package com.sro.SpringCoreTask1.service;

import com.sro.SpringCoreTask1.entity.Trainee;
import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.exception.DatabaseOperationException;
import com.sro.SpringCoreTask1.exception.ResourceNotFoundException;
import com.sro.SpringCoreTask1.repository.TraineeRepository;
import com.sro.SpringCoreTask1.repository.TrainerRepository;
import com.sro.SpringCoreTask1.service.impl.AuthServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    private Trainee trainee;
    private Trainer trainer;

    @BeforeEach
    void setUp() {
        trainee = new Trainee();
        trainee.setId(1L);
        trainee.setUsername("trainee1");
        trainee.setPassword("password1");
        trainee.setActive(true);

        trainer = new Trainer();
        trainer.setId(1L);
        trainer.setUsername("trainer1");
        trainer.setPassword("password1");
        trainer.setActive(true);
    }

    @Test
    void authenticateTrainee_ShouldReturnTrue_WhenCredentialsAreValid() {
        when(traineeRepository.findByUsername("trainee1")).thenReturn(Optional.of(trainee));

        boolean result = authService.authenticateTrainee("trainee1", "password1");

        assertTrue(result);
        assertEquals(1L, authService.getCurrentTraineeId());
        assertNull(authService.getCurrentTrainerId());
        verify(traineeRepository).findByUsername("trainee1");
    }

    @Test
    void authenticateTrainee_ShouldThrowResourceNotFoundException_WhenTraineeNotFound() {
        when(traineeRepository.findByUsername("trainee1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authService.authenticateTrainee("trainee1", "password1"));
    }

    @Test
    void authenticateTrainee_ShouldThrowResourceNotFoundException_WhenTraineeIsNotActive() {
        trainee.setActive(false);
        when(traineeRepository.findByUsername("trainee1")).thenReturn(Optional.of(trainee));

        assertThrows(ResourceNotFoundException.class, () -> authService.authenticateTrainee("trainee1", "password1"));
    }

    @Test
    void authenticateTrainee_ShouldReturnFalse_WhenPasswordIsIncorrect() {
        when(traineeRepository.findByUsername("trainee1")).thenReturn(Optional.of(trainee));

        boolean result = authService.authenticateTrainee("trainee1", "wrongpassword");

        assertFalse(result);
        assertNull(authService.getCurrentTraineeId());
        assertNull(authService.getCurrentTrainerId());
    }

    @Test
    void authenticateTrainee_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(traineeRepository.findByUsername("trainee1")).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> authService.authenticateTrainee("trainee1", "password1"));
    }

    @Test
    void authenticateTrainer_ShouldReturnTrue_WhenCredentialsAreValid() {
        when(trainerRepository.findByUsername("trainer1")).thenReturn(Optional.of(trainer));

        boolean result = authService.authenticateTrainer("trainer1", "password1");

        assertTrue(result);
        assertEquals(1L, authService.getCurrentTrainerId());
        assertNull(authService.getCurrentTraineeId());
        verify(trainerRepository).findByUsername("trainer1");
    }

    @Test
    void authenticateTrainer_ShouldThrowResourceNotFoundException_WhenTrainerNotFound() {
        when(trainerRepository.findByUsername("trainer1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authService.authenticateTrainer("trainer1", "password1"));
    }

    @Test
    void authenticateTrainer_ShouldThrowResourceNotFoundException_WhenTrainerIsNotActive() {
        trainer.setActive(false);
        when(trainerRepository.findByUsername("trainer1")).thenReturn(Optional.of(trainer));

        assertThrows(ResourceNotFoundException.class, () -> authService.authenticateTrainer("trainer1", "password1"));
    }

    @Test
    void authenticateTrainer_ShouldReturnFalse_WhenPasswordIsIncorrect() {
        when(trainerRepository.findByUsername("trainer1")).thenReturn(Optional.of(trainer));

        boolean result = authService.authenticateTrainer("trainer1", "wrongpassword");

        assertFalse(result);
        assertNull(authService.getCurrentTrainerId());
        assertNull(authService.getCurrentTraineeId());
    }

    @Test
    void authenticateTrainer_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(trainerRepository.findByUsername("trainer1")).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> authService.authenticateTrainer("trainer1", "password1"));
    }
    @Test
    void getCurrentTraineeId_ShouldReturnAuthenticatedTraineeId() {
        when(traineeRepository.findByUsername("trainee1")).thenReturn(Optional.of(trainee));
        authService.authenticateTrainee("trainee1", "password1");
    
        Long result = authService.getCurrentTraineeId();
    
        assertEquals(1L, result);

        verify(traineeRepository).findByUsername("trainee1");
    }

    @Test
    void getCurrentTrainerId_ShouldReturnAuthenticatedTrainerId() {
        when(trainerRepository.findByUsername("trainer1")).thenReturn(Optional.of(trainer));
        authService.authenticateTrainer("trainer1", "password1");

        Long result = authService.getCurrentTrainerId();
    
        assertEquals(1L, result);
    
        verify(trainerRepository).findByUsername("trainer1");
    }

    @Test
    void isTraineeAuthenticated_ShouldReturnTrue_WhenTraineeIsAuthenticated() {
        when(traineeRepository.findByUsername("trainee1")).thenReturn(Optional.of(trainee));
        authService.authenticateTrainee("trainee1", "password1");

        boolean result = authService.isTraineeAuthenticated();
    
        assertTrue(result);
    
        verify(traineeRepository).findByUsername("trainee1");
    }

    @Test
    void isTrainerAuthenticated_ShouldReturnTrue_WhenTrainerIsAuthenticated() {
        when(trainerRepository.findByUsername("trainer1")).thenReturn(Optional.of(trainer));
        authService.authenticateTrainer("trainer1", "password1");
    
        boolean result = authService.isTrainerAuthenticated();

        assertTrue(result);
    
        verify(trainerRepository).findByUsername("trainer1");
    }

    @Test
    void logout_ShouldClearAuthenticatedIds() {
        when(traineeRepository.findByUsername("trainee1")).thenReturn(Optional.of(trainee));
        authService.authenticateTrainee("trainee1", "password1");
        authService.logout();
    
        assertNull(authService.getCurrentTraineeId());
        assertNull(authService.getCurrentTrainerId());
    
        verify(traineeRepository).findByUsername("trainee1");
    }

    @Test
    void changeTraineePassword_ShouldUpdatePassword_WhenTraineeExists() {
        when(traineeRepository.findByUsername("trainee1")).thenReturn(Optional.of(trainee));

        authService.changeTraineePassword("trainee1", "newpassword");

        verify(traineeRepository).save(trainee);
        assertEquals("newpassword", trainee.getPassword());
    }

    @Test
    void changeTraineePassword_ShouldThrowResourceNotFoundException_WhenTraineeNotFound() {
        when(traineeRepository.findByUsername("trainee1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authService.changeTraineePassword("trainee1", "newpassword"));
    }

    @Test
    void changeTraineePassword_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(traineeRepository.findByUsername("trainee1")).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> authService.changeTraineePassword("trainee1", "newpassword"));
    }

    @Test
    void changeTrainerPassword_ShouldUpdatePassword_WhenTrainerExists() {
        when(trainerRepository.findByUsername("trainer1")).thenReturn(Optional.of(trainer));

        authService.changeTrainerPassword("trainer1", "newpassword");

        verify(trainerRepository).save(trainer);
        assertEquals("newpassword", trainer.getPassword());
    }

    @Test
    void changeTrainerPassword_ShouldThrowResourceNotFoundException_WhenTrainerNotFound() {
        when(trainerRepository.findByUsername("trainer1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authService.changeTrainerPassword("trainer1", "newpassword"));
    }

    @Test
    void changeTrainerPassword_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(trainerRepository.findByUsername("trainer1")).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> authService.changeTrainerPassword("trainer1", "newpassword"));
    }
}