package com.sro.SpringCoreTask1.service;

import com.sro.SpringCoreTask1.dtos.v1.response.auth.LoginResponse;
import com.sro.SpringCoreTask1.entity.Trainee;
import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.exception.AuthenticationFailedException;
import com.sro.SpringCoreTask1.exception.DatabaseOperationException;
import com.sro.SpringCoreTask1.repository.TraineeRepository;
import com.sro.SpringCoreTask1.repository.TrainerRepository;
import com.sro.SpringCoreTask1.service.impl.auth.AuthServiceImpl;

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
    void authenticate_ShouldReturnLoginResponseForTrainee_WhenCredentialsAreValid() {
        when(traineeRepository.findByUsername("trainee1")).thenReturn(Optional.of(trainee));

        LoginResponse response = authService.authenticate("trainee1", "password1");

        assertTrue(response.success());
        assertEquals("trainee1", response.username());
        assertTrue(authService.isAuthenticated());
        assertTrue(authService.isCurrentUserTrainee());
        assertFalse(authService.isCurrentUserTrainer());
        assertEquals(trainee, authService.getCurrentUser());
    }

    @Test
    void authenticate_ShouldReturnLoginResponseForTrainer_WhenCredentialsAreValid() {
        when(trainerRepository.findByUsername("trainer1")).thenReturn(Optional.of(trainer));

        LoginResponse response = authService.authenticate("trainer1", "password1");

        assertTrue(response.success());
        assertEquals("trainer1", response.username());
        assertTrue(authService.isAuthenticated());
        assertFalse(authService.isCurrentUserTrainee());
        assertTrue(authService.isCurrentUserTrainer());
        assertEquals(trainer, authService.getCurrentUser());
    }

    @Test
    void authenticate_ShouldThrowIllegalArgumentException_WhenCredentialsAreNull() {
        assertThrows(IllegalArgumentException.class, () -> authService.authenticate(null, null));
        assertThrows(IllegalArgumentException.class, () -> authService.authenticate("username", null));
        assertThrows(IllegalArgumentException.class, () -> authService.authenticate(null, "password"));
    }

    @Test
    void authenticate_ShouldThrowAuthenticationFailedException_WhenUserNotFound() {
        when(traineeRepository.findByUsername("trainee1")).thenReturn(Optional.empty());
        when(trainerRepository.findByUsername("trainee1")).thenReturn(Optional.empty());

        assertThrows(AuthenticationFailedException.class, () -> authService.authenticate("trainee1", "password1"));
    }

    @Test
    void authenticate_ShouldThrowAuthenticationFailedException_WhenUserNotActive() {
        trainee.setActive(false);
        when(traineeRepository.findByUsername("trainee1")).thenReturn(Optional.of(trainee));

        assertThrows(AuthenticationFailedException.class, () -> authService.authenticate("trainee1", "password1"));
    }

    @Test
    void authenticate_ShouldThrowAuthenticationFailedException_WhenPasswordIncorrect() {
        when(traineeRepository.findByUsername("trainee1")).thenReturn(Optional.of(trainee));

        assertThrows(AuthenticationFailedException.class, () -> authService.authenticate("trainee1", "wrongpassword"));
    }

    @Test
    void authenticate_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(traineeRepository.findByUsername("trainee1")).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> authService.authenticate("trainee1", "password1"));
    }

    @Test
    void changePassword_ShouldUpdatePasswordForTrainee_WhenCredentialsAreValid() {
        when(traineeRepository.findByUsername("trainee1")).thenReturn(Optional.of(trainee));

        authService.changePassword("trainee1", "password1", "newpassword");

        assertEquals("newpassword", trainee.getPassword());
        verify(traineeRepository).save(trainee);
    }

    @Test
    void changePassword_ShouldUpdatePasswordForTrainer_WhenCredentialsAreValid() {
        when(trainerRepository.findByUsername("trainer1")).thenReturn(Optional.of(trainer));

        authService.changePassword("trainer1", "password1", "newpassword");

        assertEquals("newpassword", trainer.getPassword());
        verify(trainerRepository).save(trainer);
    }

    @Test
    void changePassword_ShouldThrowIllegalArgumentException_WhenParametersAreNull() {
        assertThrows(IllegalArgumentException.class, () -> authService.changePassword(null, null, null));
        assertThrows(IllegalArgumentException.class, () -> authService.changePassword("user", null, null));
        assertThrows(IllegalArgumentException.class, () -> authService.changePassword(null, "pass", null));
        assertThrows(IllegalArgumentException.class, () -> authService.changePassword(null, null, "newpass"));
    }

    @Test
    void changePassword_ShouldThrowAuthenticationFailedException_WhenCurrentPasswordIncorrect() {
        when(traineeRepository.findByUsername("trainee1")).thenReturn(Optional.of(trainee));

        assertThrows(AuthenticationFailedException.class, 
            () -> authService.changePassword("trainee1", "wrongpassword", "newpassword"));
    }

    @Test
    void changePassword_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(traineeRepository.findByUsername("trainee1")).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, 
            () -> authService.changePassword("trainee1", "password1", "newpassword"));
    }

    @Test
    void logout_ShouldClearCurrentUser() {
        when(traineeRepository.findByUsername("trainee1")).thenReturn(Optional.of(trainee));
        authService.authenticate("trainee1", "password1");

        authService.logout();

        assertNull(authService.getCurrentUser());
        assertFalse(authService.isAuthenticated());
    }

    @Test
    void getCurrentUser_ShouldReturnAuthenticatedUser() {
        when(traineeRepository.findByUsername("trainee1")).thenReturn(Optional.of(trainee));
        authService.authenticate("trainee1", "password1");

        assertEquals(trainee, authService.getCurrentUser());
    }

    @Test
    void isAuthenticated_ShouldReturnFalse_WhenNoUserLoggedIn() {
        assertFalse(authService.isAuthenticated());
    }

    @Test
    void isCurrentUserTrainee_ShouldReturnFalse_WhenNoUserLoggedIn() {
        assertFalse(authService.isCurrentUserTrainee());
    }

    @Test
    void isCurrentUserTrainer_ShouldReturnFalse_WhenNoUserLoggedIn() {
        assertFalse(authService.isCurrentUserTrainer());
    }
}