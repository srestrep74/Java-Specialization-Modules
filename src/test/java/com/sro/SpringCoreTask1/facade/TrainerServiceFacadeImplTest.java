package com.sro.SpringCoreTask1.facade;

import com.sro.SpringCoreTask1.dto.TrainerTrainingFilterDTO;
import com.sro.SpringCoreTask1.dto.request.TrainerRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TrainerResponseDTO;
import com.sro.SpringCoreTask1.dto.response.TrainingResponseDTO;
import com.sro.SpringCoreTask1.exception.DatabaseOperationException;
import com.sro.SpringCoreTask1.exception.ResourceAlreadyExistsException;
import com.sro.SpringCoreTask1.exception.ResourceNotFoundException;
import com.sro.SpringCoreTask1.facade.impl.TrainerServiceFacadeImpl;
import com.sro.SpringCoreTask1.service.TrainerService;
import com.sro.SpringCoreTask1.service.TrainingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceFacadeImplTest {

    @Mock
    private TrainerService trainerService;

    @Mock
    private TrainingService trainingService;

    @InjectMocks
    private TrainerServiceFacadeImpl trainerServiceFacade;

    private final TrainerRequestDTO trainerRequestDTO = new TrainerRequestDTO("John", "Doe", "john.doe", "password", true, 1L);
    private final TrainerResponseDTO trainerResponseDTO = new TrainerResponseDTO(1L, "John", "Doe", "john.doe", true, null);
    private final TrainingResponseDTO trainingResponseDTO = new TrainingResponseDTO(1L, "Training 1", LocalDate.now(), 60, null, trainerResponseDTO, null);
    private final String username = "john.doe";
    private final Long trainerId = 1L;
    private final String newPassword = "newPassword";

    @Test
    void createTrainer_Success() {
        when(trainerService.save(trainerRequestDTO)).thenReturn(trainerResponseDTO);

        TrainerResponseDTO result = trainerServiceFacade.createTrainer(trainerRequestDTO);

        assertEquals(trainerResponseDTO, result);
        verify(trainerService).save(trainerRequestDTO);
    }

    @Test
    void createTrainer_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> trainerServiceFacade.createTrainer(null));
    }

    @Test
    void createTrainer_ThrowsResourceAlreadyExistsException() {
        when(trainerService.save(trainerRequestDTO)).thenThrow(new ResourceAlreadyExistsException("Trainer already exists"));

        assertThrows(ResourceAlreadyExistsException.class, () -> trainerServiceFacade.createTrainer(trainerRequestDTO));
    }

    @Test
    void createTrainer_ThrowsDatabaseOperationException() {
        when(trainerService.save(trainerRequestDTO)).thenThrow(new RuntimeException());

        assertThrows(DatabaseOperationException.class, () -> trainerServiceFacade.createTrainer(trainerRequestDTO));
    }

    @Test
    void findTrainerByUsername_Success() {
        when(trainerService.findByUsername(username)).thenReturn(trainerResponseDTO);

        TrainerResponseDTO result = trainerServiceFacade.findTrainerByUsername(username);

        assertEquals(trainerResponseDTO, result);
        verify(trainerService).findByUsername(username);
    }

    @Test
    void findTrainerByUsername_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> trainerServiceFacade.findTrainerByUsername(null));
        assertThrows(IllegalArgumentException.class, () -> trainerServiceFacade.findTrainerByUsername(""));
    }

    @Test
    void findTrainerByUsername_ThrowsResourceNotFoundException() {
        when(trainerService.findByUsername(username)).thenThrow(new ResourceNotFoundException("Trainer not found"));

        assertThrows(ResourceNotFoundException.class, () -> trainerServiceFacade.findTrainerByUsername(username));
    }

    @Test
    void findTrainerByUsername_ThrowsDatabaseOperationException() {
        when(trainerService.findByUsername(username)).thenThrow(new RuntimeException());

        assertThrows(DatabaseOperationException.class, () -> trainerServiceFacade.findTrainerByUsername(username));
    }

    @Test
    void findTrainerById_Success() {
        when(trainerService.findById(trainerId)).thenReturn(trainerResponseDTO);

        TrainerResponseDTO result = trainerServiceFacade.findTrainerById(trainerId);

        assertEquals(trainerResponseDTO, result);
        verify(trainerService).findById(trainerId);
    }

    @Test
    void findTrainerById_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> trainerServiceFacade.findTrainerById(null));
    }

    @Test
    void findTrainerById_ThrowsResourceNotFoundException() {
        when(trainerService.findById(trainerId)).thenThrow(new ResourceNotFoundException("Trainer not found"));

        assertThrows(ResourceNotFoundException.class, () -> trainerServiceFacade.findTrainerById(trainerId));
    }

    @Test
    void findTrainerById_ThrowsDatabaseOperationException() {
        when(trainerService.findById(trainerId)).thenThrow(new RuntimeException());

        assertThrows(DatabaseOperationException.class, () -> trainerServiceFacade.findTrainerById(trainerId));
    }

    @Test
    void updateTrainerPassword_Success() {
        when(trainerService.updateTrainerPassword(trainerId, newPassword)).thenReturn(true);

        boolean result = trainerServiceFacade.updateTrainerPassword(trainerId, newPassword);

        assertTrue(result);
        verify(trainerService).updateTrainerPassword(trainerId, newPassword);
    }

    @Test
    void updateTrainerPassword_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> trainerServiceFacade.updateTrainerPassword(null, newPassword));
        assertThrows(IllegalArgumentException.class, () -> trainerServiceFacade.updateTrainerPassword(trainerId, null));
        assertThrows(IllegalArgumentException.class, () -> trainerServiceFacade.updateTrainerPassword(trainerId, ""));
    }

    @Test
    void updateTrainerPassword_ThrowsDatabaseOperationException() {
        when(trainerService.updateTrainerPassword(trainerId, newPassword)).thenThrow(new RuntimeException());

        assertThrows(DatabaseOperationException.class, () -> trainerServiceFacade.updateTrainerPassword(trainerId, newPassword));
    }

    @Test
    void updateTrainer_Success() {
        when(trainerService.update(trainerRequestDTO)).thenReturn(trainerResponseDTO);

        TrainerResponseDTO result = trainerServiceFacade.updateTrainer(trainerRequestDTO);

        assertEquals(trainerResponseDTO, result);
        verify(trainerService).update(trainerRequestDTO);
    }

    @Test
    void updateTrainer_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> trainerServiceFacade.updateTrainer(null));
    }

    @Test
    void updateTrainer_ThrowsResourceNotFoundException() {
        when(trainerService.update(trainerRequestDTO)).thenThrow(new ResourceNotFoundException("Trainer not found"));

        assertThrows(ResourceNotFoundException.class, () -> trainerServiceFacade.updateTrainer(trainerRequestDTO));
    }

    @Test
    void updateTrainer_ThrowsDatabaseOperationException() {
        when(trainerService.update(trainerRequestDTO)).thenThrow(new RuntimeException());

        assertThrows(DatabaseOperationException.class, () -> trainerServiceFacade.updateTrainer(trainerRequestDTO));
    }

    @Test
    void toggleTrainerStatus_Success() {
        doNothing().when(trainerService).toggleTrainerStatus(trainerId);

        assertDoesNotThrow(() -> trainerServiceFacade.toggleTrainerStatus(trainerId));
        verify(trainerService).toggleTrainerStatus(trainerId);
    }

    @Test
    void toggleTrainerStatus_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> trainerServiceFacade.toggleTrainerStatus(null));
    }

    @Test
    void toggleTrainerStatus_ThrowsDatabaseOperationException() {
        doThrow(new RuntimeException()).when(trainerService).toggleTrainerStatus(trainerId);

        assertThrows(DatabaseOperationException.class, () -> trainerServiceFacade.toggleTrainerStatus(trainerId));
    }

    @Test
    void findTrainerTrainings_Success() {
        TrainerTrainingFilterDTO filterDTO = new TrainerTrainingFilterDTO(trainerId, null, null, null, null);
        when(trainingService.findTrainingsByTrainerWithFilters(filterDTO)).thenReturn(List.of(trainingResponseDTO));

        List<TrainingResponseDTO> result = trainerServiceFacade.findTrainerTrainings(username, filterDTO);

        assertEquals(1, result.size());
        assertEquals(trainingResponseDTO, result.get(0));
        verify(trainingService).findTrainingsByTrainerWithFilters(filterDTO);
    }

    @Test
    void findTrainerTrainings_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> trainerServiceFacade.findTrainerTrainings(null, new TrainerTrainingFilterDTO(1L, null, null, null, null)));
        assertThrows(IllegalArgumentException.class, () -> trainerServiceFacade.findTrainerTrainings("", new TrainerTrainingFilterDTO(1L, null, null, null, null)));
    }

    @Test
    void findTrainerTrainings_ThrowsDatabaseOperationException() {
        when(trainingService.findTrainingsByTrainerWithFilters(any())).thenThrow(new RuntimeException());

        assertThrows(DatabaseOperationException.class, () -> trainerServiceFacade.findTrainerTrainings(username, new TrainerTrainingFilterDTO(1L, null, null, null, null)));
    }
}