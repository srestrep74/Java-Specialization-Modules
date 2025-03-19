package com.sro.SpringCoreTask1.facade;

import com.sro.SpringCoreTask1.dto.request.TrainingRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TrainingResponseDTO;
import com.sro.SpringCoreTask1.dto.response.TrainingTypeResponseDTO;
import com.sro.SpringCoreTask1.exception.DatabaseOperationException;
import com.sro.SpringCoreTask1.exception.ResourceAlreadyExistsException;
import com.sro.SpringCoreTask1.facade.impl.TrainingServiceFacadeImpl;
import com.sro.SpringCoreTask1.service.TrainingService;
import com.sro.SpringCoreTask1.service.TrainingTypeService;
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
class TrainingServiceFacadeImplTest {

    @Mock
    private TrainingService trainingService;

    @Mock
    private TrainingTypeService trainingTypeService;

    @InjectMocks
    private TrainingServiceFacadeImpl trainingServiceFacade;

    private final TrainingRequestDTO trainingRequestDTO = new TrainingRequestDTO("Training 1", null, 60, 1L, 2L, 3L);
    private final TrainingResponseDTO trainingResponseDTO = new TrainingResponseDTO(1L, "Training 1", LocalDate.now(), 60, null, null, null);
    private final TrainingTypeResponseDTO trainingTypeResponseDTO = new TrainingTypeResponseDTO(1L, "Fitness");

    @Test
    void createTraining_Success() {
        when(trainingService.save(trainingRequestDTO)).thenReturn(trainingResponseDTO);

        TrainingResponseDTO result = trainingServiceFacade.createTraining(trainingRequestDTO);

        assertEquals(trainingResponseDTO, result);
        verify(trainingService).save(trainingRequestDTO);
    }

    @Test
    void createTraining_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> trainingServiceFacade.createTraining(null));
    }

    @Test
    void createTraining_ThrowsResourceAlreadyExistsException() {
        when(trainingService.save(trainingRequestDTO)).thenThrow(new ResourceAlreadyExistsException("Training already exists"));

        assertThrows(ResourceAlreadyExistsException.class, () -> trainingServiceFacade.createTraining(trainingRequestDTO));
    }

    @Test
    void createTraining_ThrowsDatabaseOperationException() {
        when(trainingService.save(trainingRequestDTO)).thenThrow(new RuntimeException());

        assertThrows(DatabaseOperationException.class, () -> trainingServiceFacade.createTraining(trainingRequestDTO));
    }

    @Test
    void findAllTrainingTypes_Success() {
        when(trainingTypeService.findAll()).thenReturn(List.of(trainingTypeResponseDTO));

        List<TrainingTypeResponseDTO> result = trainingServiceFacade.findAllTrainingTypes();

        assertEquals(1, result.size());
        assertEquals(trainingTypeResponseDTO, result.get(0));
        verify(trainingTypeService).findAll();
    }

    @Test
    void findAllTrainingTypes_ThrowsDatabaseOperationException() {
        when(trainingTypeService.findAll()).thenThrow(new RuntimeException());

        assertThrows(DatabaseOperationException.class, () -> trainingServiceFacade.findAllTrainingTypes());
    }
}