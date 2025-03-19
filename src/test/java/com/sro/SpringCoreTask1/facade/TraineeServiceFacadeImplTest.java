package com.sro.SpringCoreTask1.facade;

import com.sro.SpringCoreTask1.dto.TraineeTrainingFilterDTO;
import com.sro.SpringCoreTask1.dto.request.TraineeRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TraineeResponseDTO;
import com.sro.SpringCoreTask1.dto.response.TrainerResponseDTO;
import com.sro.SpringCoreTask1.dto.response.TrainingResponseDTO;
import com.sro.SpringCoreTask1.exception.DatabaseOperationException;
import com.sro.SpringCoreTask1.exception.ResourceAlreadyExistsException;
import com.sro.SpringCoreTask1.exception.ResourceNotFoundException;
import com.sro.SpringCoreTask1.facade.impl.TraineeServiceFacadeImpl;
import com.sro.SpringCoreTask1.service.TraineeService;
import com.sro.SpringCoreTask1.service.TrainerService;
import com.sro.SpringCoreTask1.service.TrainingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceFacadeImplTest {

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private TrainingService trainingService;

    @InjectMocks
    private TraineeServiceFacadeImpl traineeServiceFacade;

    private final TraineeRequestDTO traineeRequestDTO = new TraineeRequestDTO("John", "Doe", "john.doe", "password", true, "123 Street", null, List.of(1L));
    private final TraineeResponseDTO traineeResponseDTO = new TraineeResponseDTO(1L, "John", "Doe", "john.doe", true, "123 Street", LocalDate.of(2004, 01, 14));
    private final TrainerResponseDTO trainerResponseDTO = new TrainerResponseDTO(1L, "Trainer", "One", "trainer.one", true, null);
    private final TrainingResponseDTO trainingResponseDTO = new TrainingResponseDTO(1L, "Training 1", LocalDate.now(), 60, traineeResponseDTO, trainerResponseDTO, null);
    private final String username = "john.doe";
    private final Long traineeId = 1L;
    private final Long trainerId = 2L;
    private final String newPassword = "newPassword";
    private final String action = "add";

    @Test
    void createTrainee_Success() {
        when(traineeService.save(traineeRequestDTO)).thenReturn(traineeResponseDTO);

        TraineeResponseDTO result = traineeServiceFacade.createTrainee(traineeRequestDTO);

        assertEquals(traineeResponseDTO, result);
        verify(traineeService).save(traineeRequestDTO);
    }

    @Test
    void createTrainee_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> traineeServiceFacade.createTrainee(null));
    }

    @Test
    void createTrainee_ThrowsResourceAlreadyExistsException() {
        when(traineeService.save(traineeRequestDTO)).thenThrow(new ResourceAlreadyExistsException("Trainee already exists"));

        assertThrows(ResourceAlreadyExistsException.class, () -> traineeServiceFacade.createTrainee(traineeRequestDTO));
    }

    @Test
    void createTrainee_ThrowsDatabaseOperationException() {
        when(traineeService.save(traineeRequestDTO)).thenThrow(new RuntimeException());

        assertThrows(DatabaseOperationException.class, () -> traineeServiceFacade.createTrainee(traineeRequestDTO));
    }

    @Test
    void findTraineeByUsername_Success() {
        when(traineeService.findByUsername(username)).thenReturn(traineeResponseDTO);

        TraineeResponseDTO result = traineeServiceFacade.findTraineeByUsername(username);

        assertEquals(traineeResponseDTO, result);
        verify(traineeService).findByUsername(username);
    }

    @Test
    void findTraineeByUsername_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> traineeServiceFacade.findTraineeByUsername(null));
        assertThrows(IllegalArgumentException.class, () -> traineeServiceFacade.findTraineeByUsername(""));
    }

    @Test
    void findTraineeByUsername_ThrowsResourceNotFoundException() {
        when(traineeService.findByUsername(username)).thenThrow(new ResourceNotFoundException("Trainee not found"));

        assertThrows(ResourceNotFoundException.class, () -> traineeServiceFacade.findTraineeByUsername(username));
    }

    @Test
    void findTraineeByUsername_ThrowsDatabaseOperationException() {
        when(traineeService.findByUsername(username)).thenThrow(new RuntimeException());

        assertThrows(DatabaseOperationException.class, () -> traineeServiceFacade.findTraineeByUsername(username));
    }

    @Test
    void findTraineeById_Success() {
        when(traineeService.findById(traineeId)).thenReturn(traineeResponseDTO);

        TraineeResponseDTO result = traineeServiceFacade.findTraineeById(traineeId);

        assertEquals(traineeResponseDTO, result);
        verify(traineeService).findById(traineeId);
    }

    @Test
    void findTraineeById_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> traineeServiceFacade.findTraineeById(null));
    }

    @Test
    void findTraineeById_ThrowsResourceNotFoundException() {
        when(traineeService.findById(traineeId)).thenThrow(new ResourceNotFoundException("Trainee not found"));

        assertThrows(ResourceNotFoundException.class, () -> traineeServiceFacade.findTraineeById(traineeId));
    }

    @Test
    void findTraineeById_ThrowsDatabaseOperationException() {
        when(traineeService.findById(traineeId)).thenThrow(new RuntimeException());

        assertThrows(DatabaseOperationException.class, () -> traineeServiceFacade.findTraineeById(traineeId));
    }

    @Test
    void updateTraineePassword_Success() {
        when(traineeService.updateTraineePassword(traineeId, newPassword)).thenReturn(true);

        boolean result = traineeServiceFacade.updateTraineePassword(traineeId, newPassword);

        assertTrue(result);
        verify(traineeService).updateTraineePassword(traineeId, newPassword);
    }

    @Test
    void updateTraineePassword_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> traineeServiceFacade.updateTraineePassword(null, newPassword));
        assertThrows(IllegalArgumentException.class, () -> traineeServiceFacade.updateTraineePassword(traineeId, null));
        assertThrows(IllegalArgumentException.class, () -> traineeServiceFacade.updateTraineePassword(traineeId, ""));
    }

    @Test
    void updateTraineePassword_ThrowsDatabaseOperationException() {
        when(traineeService.updateTraineePassword(traineeId, newPassword)).thenThrow(new RuntimeException());

        assertThrows(DatabaseOperationException.class, () -> traineeServiceFacade.updateTraineePassword(traineeId, newPassword));
    }

    @Test
    void updateTrainee_Success() {
        when(traineeService.update(traineeRequestDTO)).thenReturn(traineeResponseDTO);

        TraineeResponseDTO result = traineeServiceFacade.updateTrainee(traineeRequestDTO);

        assertEquals(traineeResponseDTO, result);
        verify(traineeService).update(traineeRequestDTO);
    }

    @Test
    void updateTrainee_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> traineeServiceFacade.updateTrainee(null));
    }

    @Test
    void updateTrainee_ThrowsResourceNotFoundException() {
        when(traineeService.update(traineeRequestDTO)).thenThrow(new ResourceNotFoundException("Trainee not found"));

        assertThrows(ResourceNotFoundException.class, () -> traineeServiceFacade.updateTrainee(traineeRequestDTO));
    }

    @Test
    void updateTrainee_ThrowsDatabaseOperationException() {
        when(traineeService.update(traineeRequestDTO)).thenThrow(new RuntimeException());

        assertThrows(DatabaseOperationException.class, () -> traineeServiceFacade.updateTrainee(traineeRequestDTO));
    }

    @Test
    void toggleTraineeStatus_Success() {
        doNothing().when(traineeService).setTraineeStatus(traineeId);

        assertDoesNotThrow(() -> traineeServiceFacade.toggleTraineeStatus(traineeId));
        verify(traineeService).setTraineeStatus(traineeId);
    }

    @Test
    void toggleTraineeStatus_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> traineeServiceFacade.toggleTraineeStatus(null));
    }

    @Test
    void toggleTraineeStatus_ThrowsDatabaseOperationException() {
        doThrow(new RuntimeException()).when(traineeService).setTraineeStatus(traineeId);

        assertThrows(DatabaseOperationException.class, () -> traineeServiceFacade.toggleTraineeStatus(traineeId));
    }

    @Test
    void deleteTraineeByUsername_Success() {
        doNothing().when(traineeService).deleteByUsername(username);

        assertDoesNotThrow(() -> traineeServiceFacade.deleteTraineeByUsername(username));
        verify(traineeService).deleteByUsername(username);
    }

    @Test
    void deleteTraineeByUsername_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> traineeServiceFacade.deleteTraineeByUsername(null));
        assertThrows(IllegalArgumentException.class, () -> traineeServiceFacade.deleteTraineeByUsername(""));
    }

    @Test
    void deleteTraineeByUsername_ThrowsResourceNotFoundException() {
        doThrow(new ResourceNotFoundException("Trainee not found")).when(traineeService).deleteByUsername(username);

        assertThrows(ResourceNotFoundException.class, () -> traineeServiceFacade.deleteTraineeByUsername(username));
    }

    @Test
    void deleteTraineeByUsername_ThrowsDatabaseOperationException() {
        doThrow(new RuntimeException()).when(traineeService).deleteByUsername(username);

        assertThrows(DatabaseOperationException.class, () -> traineeServiceFacade.deleteTraineeByUsername(username));
    }

    @Test
    void findTraineeTrainings_Success() {
        TraineeTrainingFilterDTO filterDTO = new TraineeTrainingFilterDTO(traineeId, null, null, null, null);
        when(trainingService.findTrainingsByTraineeWithFilters(filterDTO)).thenReturn(List.of(trainingResponseDTO));

        List<TrainingResponseDTO> result = traineeServiceFacade.findTraineeTrainings(username, filterDTO);

        assertEquals(1, result.size());
        assertEquals(trainingResponseDTO, result.get(0));
        verify(trainingService).findTrainingsByTraineeWithFilters(filterDTO);
    }

    @Test
    void findTraineeTrainings_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> traineeServiceFacade.findTraineeTrainings(null, new TraineeTrainingFilterDTO(1L, null, null, null, null)));
        assertThrows(IllegalArgumentException.class, () -> traineeServiceFacade.findTraineeTrainings("", new TraineeTrainingFilterDTO(1L, null, null, null, null)));
    }

    @Test
    void findTraineeTrainings_ThrowsDatabaseOperationException() {
        when(trainingService.findTrainingsByTraineeWithFilters(any())).thenThrow(new RuntimeException());

        assertThrows(DatabaseOperationException.class, () -> traineeServiceFacade.findTraineeTrainings(username, new TraineeTrainingFilterDTO(1L, null, null, null, null)));
    }

    @Test
    void findUnassignedTrainers_Success() {
        when(trainerService.findUnassignedTrainersByTraineeUsername(username)).thenReturn(List.of(trainerResponseDTO));

        List<TrainerResponseDTO> result = traineeServiceFacade.findUnassignedTrainers(username);

        assertEquals(1, result.size());
        assertEquals(trainerResponseDTO, result.get(0));
        verify(trainerService).findUnassignedTrainersByTraineeUsername(username);
    }

    @Test
    void findUnassignedTrainers_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> traineeServiceFacade.findUnassignedTrainers(null));
        assertThrows(IllegalArgumentException.class, () -> traineeServiceFacade.findUnassignedTrainers(""));
    }

    @Test
    void findUnassignedTrainers_ThrowsDatabaseOperationException() {
        when(trainerService.findUnassignedTrainersByTraineeUsername(username)).thenThrow(new RuntimeException());

        assertThrows(DatabaseOperationException.class, () -> traineeServiceFacade.findUnassignedTrainers(username));
    }

    @Test
    void updateTraineeTrainers_Success() {
        doNothing().when(traineeService).addTrainerToTrainee(traineeId, trainerId);

        assertDoesNotThrow(() -> traineeServiceFacade.updateTraineeTrainers(traineeId, trainerId, action));
        verify(traineeService).addTrainerToTrainee(traineeId, trainerId);
    }

    @Test
    void updateTraineeTrainers_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> traineeServiceFacade.updateTraineeTrainers(null, trainerId, action));
        assertThrows(IllegalArgumentException.class, () -> traineeServiceFacade.updateTraineeTrainers(traineeId, null, action));
        assertThrows(IllegalArgumentException.class, () -> traineeServiceFacade.updateTraineeTrainers(traineeId, trainerId, null));
        assertThrows(IllegalArgumentException.class, () -> traineeServiceFacade.updateTraineeTrainers(traineeId, trainerId, "invalid"));
    }

    @Test
    void updateTraineeTrainers_ThrowsResourceNotFoundException() {
        doThrow(new ResourceNotFoundException("Trainee not found")).when(traineeService).addTrainerToTrainee(traineeId, trainerId);

        assertThrows(ResourceNotFoundException.class, () -> traineeServiceFacade.updateTraineeTrainers(traineeId, trainerId, action));
    }

    @Test
    void updateTraineeTrainers_ThrowsResourceAlreadyExistsException() {
        doThrow(new ResourceAlreadyExistsException("Trainer already assigned")).when(traineeService).addTrainerToTrainee(traineeId, trainerId);

        assertThrows(ResourceAlreadyExistsException.class, () -> traineeServiceFacade.updateTraineeTrainers(traineeId, trainerId, action));
    }

    @Test
    void updateTraineeTrainers_ThrowsDatabaseOperationException() {
        doThrow(new RuntimeException()).when(traineeService).addTrainerToTrainee(traineeId, trainerId);

        assertThrows(DatabaseOperationException.class, () -> traineeServiceFacade.updateTraineeTrainers(traineeId, trainerId, action));
    }

    @Test
    void findTraineeTrainers_Success() {
        when(trainerService.findTrainersByTraineeId(traineeId)).thenReturn(Set.of(trainerResponseDTO));

        Set<TrainerResponseDTO> result = traineeServiceFacade.findTraineeTrainers(traineeId);

        assertEquals(1, result.size());
        assertTrue(result.contains(trainerResponseDTO));
        verify(trainerService).findTrainersByTraineeId(traineeId);
    }

    @Test
    void findTraineeTrainers_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> traineeServiceFacade.findTraineeTrainers(null));
    }

    @Test
    void findTraineeTrainers_ThrowsDatabaseOperationException() {
        when(trainerService.findTrainersByTraineeId(traineeId)).thenThrow(new RuntimeException());

        assertThrows(DatabaseOperationException.class, () -> traineeServiceFacade.findTraineeTrainers(traineeId));
    }
}