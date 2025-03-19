package com.sro.SpringCoreTask1.facade;

import com.sro.SpringCoreTask1.exception.DatabaseOperationException;
import com.sro.SpringCoreTask1.facade.impl.AuthServiceFacadeImpl;
import com.sro.SpringCoreTask1.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceFacadeImplTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthServiceFacadeImpl authServiceFacade;

    private final String username = "testUser";
    private final String password = "testPassword";
    private final Long traineeId = 1L;
    private final Long trainerId = 2L;

    @Test
    void authenticateTrainee_Success() {
        when(authService.authenticateTrainee(username, password)).thenReturn(true);

        boolean result = authServiceFacade.authenticateTrainee(username, password);

        assertTrue(result);
        verify(authService).authenticateTrainee(username, password);
    }

    @Test
    void authenticateTrainee_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> authServiceFacade.authenticateTrainee(null, password));
        assertThrows(IllegalArgumentException.class, () -> authServiceFacade.authenticateTrainee(username, null));
    }

    @Test
    void authenticateTrainee_ThrowsDatabaseOperationException() {
        when(authService.authenticateTrainee(username, password)).thenThrow(new RuntimeException());

        assertThrows(DatabaseOperationException.class, () -> authServiceFacade.authenticateTrainee(username, password));
    }

    @Test
    void authenticateTrainer_Success() {
        when(authService.authenticateTrainer(username, password)).thenReturn(true);

        boolean result = authServiceFacade.authenticateTrainer(username, password);

        assertTrue(result);
        verify(authService).authenticateTrainer(username, password);
    }

    @Test
    void authenticateTrainer_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> authServiceFacade.authenticateTrainer(null, password));
        assertThrows(IllegalArgumentException.class, () -> authServiceFacade.authenticateTrainer(username, null));
    }

    @Test
    void authenticateTrainer_ThrowsDatabaseOperationException() {
        when(authService.authenticateTrainer(username, password)).thenThrow(new RuntimeException());

        assertThrows(DatabaseOperationException.class, () -> authServiceFacade.authenticateTrainer(username, password));
    }

    @Test
    void getAuthenticatedTraineeId_Success() {
        when(authService.getCurrentTraineeId()).thenReturn(traineeId);

        Long result = authServiceFacade.getAuthenticatedTraineeId();

        assertEquals(traineeId, result);
        verify(authService).getCurrentTraineeId();
    }

    @Test
    void getAuthenticatedTraineeId_ThrowsDatabaseOperationException() {
        when(authService.getCurrentTraineeId()).thenThrow(new RuntimeException());

        assertThrows(DatabaseOperationException.class, () -> authServiceFacade.getAuthenticatedTraineeId());
    }

    @Test
    void getAuthenticatedTrainerId_Success() {
        when(authService.getCurrentTrainerId()).thenReturn(trainerId);

        Long result = authServiceFacade.getAuthenticatedTrainerId();

        assertEquals(trainerId, result);
        verify(authService).getCurrentTrainerId();
    }

    @Test
    void getAuthenticatedTrainerId_ThrowsDatabaseOperationException() {
        when(authService.getCurrentTrainerId()).thenThrow(new RuntimeException());

        assertThrows(DatabaseOperationException.class, () -> authServiceFacade.getAuthenticatedTrainerId());
    }

    @Test
    void isTraineeAuthenticated_Success() {
        when(authService.isTraineeAuthenticated()).thenReturn(true);

        boolean result = authServiceFacade.isTraineeAuthenticated();

        assertTrue(result);
        verify(authService).isTraineeAuthenticated();
    }

    @Test
    void isTraineeAuthenticated_ThrowsDatabaseOperationException() {
        when(authService.isTraineeAuthenticated()).thenThrow(new RuntimeException());

        assertThrows(DatabaseOperationException.class, () -> authServiceFacade.isTraineeAuthenticated());
    }

    @Test
    void isTrainerAuthenticated_Success() {
        when(authService.isTrainerAuthenticated()).thenReturn(true);

        boolean result = authServiceFacade.isTrainerAuthenticated();

        assertTrue(result);
        verify(authService).isTrainerAuthenticated();
    }

    @Test
    void isTrainerAuthenticated_ThrowsDatabaseOperationException() {
        when(authService.isTrainerAuthenticated()).thenThrow(new RuntimeException());

        assertThrows(DatabaseOperationException.class, () -> authServiceFacade.isTrainerAuthenticated());
    }

    @Test
    void performLogout_Success() {
        doNothing().when(authService).logout();

        assertDoesNotThrow(() -> authServiceFacade.perfomLogout());
        verify(authService).logout();
    }

    @Test
    void performLogout_ThrowsDatabaseOperationException() {
        doThrow(new RuntimeException()).when(authService).logout();

        assertThrows(DatabaseOperationException.class, () -> authServiceFacade.perfomLogout());
    }

    @Test
    void verifyTraineeCredentials_Success() {
        when(authService.authenticateTrainee(username, password)).thenReturn(true);

        boolean result = authServiceFacade.verifyTraineeCredentials(username, password);

        assertTrue(result);
        verify(authService).authenticateTrainee(username, password);
    }

    @Test
    void verifyTraineeCredentials_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> authServiceFacade.verifyTraineeCredentials(null, password));
        assertThrows(IllegalArgumentException.class, () -> authServiceFacade.verifyTraineeCredentials(username, null));
    }

    @Test
    void verifyTraineeCredentials_ThrowsDatabaseOperationException() {
        when(authService.authenticateTrainee(username, password)).thenThrow(new RuntimeException());

        assertThrows(DatabaseOperationException.class, () -> authServiceFacade.verifyTraineeCredentials(username, password));
    }

    @Test
    void verifyTrainerCredentials_Success() {
        when(authService.authenticateTrainer(username, password)).thenReturn(true);

        boolean result = authServiceFacade.verifyTrainerCredentials(username, password);

        assertTrue(result);
        verify(authService).authenticateTrainer(username, password);
    }

    @Test
    void verifyTrainerCredentials_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> authServiceFacade.verifyTrainerCredentials(null, password));
        assertThrows(IllegalArgumentException.class, () -> authServiceFacade.verifyTrainerCredentials(username, null));
    }

    @Test
    void verifyTrainerCredentials_ThrowsDatabaseOperationException() {
        when(authService.authenticateTrainer(username, password)).thenThrow(new RuntimeException());

        assertThrows(DatabaseOperationException.class, () -> authServiceFacade.verifyTrainerCredentials(username, password));
    }
}