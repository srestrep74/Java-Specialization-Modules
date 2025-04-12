package com.sro.SpringCoreTask1.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sro.SpringCoreTask1.dtos.v1.request.trainingType.TrainingTypeRequestDTO;
import com.sro.SpringCoreTask1.dtos.v1.response.trainingType.TrainingTypeResponse;
import com.sro.SpringCoreTask1.entity.TrainingType;
import com.sro.SpringCoreTask1.exception.DatabaseOperationException;
import com.sro.SpringCoreTask1.exception.ResourceAlreadyExistsException;
import com.sro.SpringCoreTask1.exception.ResourceNotFoundException;
import com.sro.SpringCoreTask1.mappers.trainingType.TrainingTypeCreateMapper;
import com.sro.SpringCoreTask1.mappers.trainingType.TrainingTypeResponseMapper;
import com.sro.SpringCoreTask1.repository.TrainingTypeRepository;
import com.sro.SpringCoreTask1.service.impl.TrainingTypeServiceImpl;

import jakarta.validation.ConstraintViolationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class TrainingTypeServiceImplTest {

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @Mock
    private TrainingTypeCreateMapper trainingTypeCreateMapper;

    @Mock
    private TrainingTypeResponseMapper trainingTypeResponseMapper;

    @InjectMocks
    private TrainingTypeServiceImpl trainingTypeService;

    private TrainingType trainingType;
    private TrainingTypeRequestDTO trainingTypeRequestDTO;
    private TrainingTypeResponse trainingTypeResponse;

    @BeforeEach
    void setUp() {
        trainingType = new TrainingType();
        trainingType.setId(1L);
        trainingType.setTrainingTypeName("Fitness");

        trainingTypeRequestDTO = new TrainingTypeRequestDTO("Fitness");
        trainingTypeResponse = new TrainingTypeResponse(1L, "Fitness");
    }

    @Test
    void save_ShouldReturnTrainingTypeResponse_WhenValidInput() {
        when(trainingTypeCreateMapper.toEntity(trainingTypeRequestDTO)).thenReturn(trainingType);
        when(trainingTypeRepository.save(trainingType)).thenReturn(trainingType);
        when(trainingTypeResponseMapper.mapToResponse(trainingType)).thenReturn(trainingTypeResponse);

        TrainingTypeResponse result = trainingTypeService.save(trainingTypeRequestDTO);

        assertNotNull(result);
        assertEquals(trainingTypeResponse, result);
        verify(trainingTypeRepository).save(trainingType);
    }

    @Test
    void save_ShouldThrowIllegalArgumentException_WhenTrainingTypeRequestDTOIsNull() {
        assertThrows(IllegalArgumentException.class, () -> trainingTypeService.save(null));
    }

    @Test
    void save_ShouldThrowResourceAlreadyExistsException_WhenTrainingTypeNameExists() {
        when(trainingTypeCreateMapper.toEntity(trainingTypeRequestDTO)).thenReturn(trainingType);
        when(trainingTypeRepository.save(trainingType)).thenThrow(new ConstraintViolationException("Training Type name already exists", null));

        assertThrows(ResourceAlreadyExistsException.class, () -> trainingTypeService.save(trainingTypeRequestDTO));
    }

    @Test
    void save_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(trainingTypeCreateMapper.toEntity(trainingTypeRequestDTO)).thenReturn(trainingType);
        when(trainingTypeRepository.save(trainingType)).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> trainingTypeService.save(trainingTypeRequestDTO));
    }

    @Test
    void findById_ShouldReturnTrainingTypeResponse_WhenTrainingTypeExists() {
        when(trainingTypeRepository.findById(1L)).thenReturn(Optional.of(trainingType));
        when(trainingTypeResponseMapper.mapToResponse(trainingType)).thenReturn(trainingTypeResponse);

        TrainingTypeResponse result = trainingTypeService.findById(1L);

        assertNotNull(result);
        assertEquals(trainingTypeResponse, result);
    }

    @Test
    void findById_ShouldThrowIllegalArgumentException_WhenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> trainingTypeService.findById(null));
    }

    @Test
    void findById_ShouldThrowResourceNotFoundException_WhenTrainingTypeDoesNotExist() {
        when(trainingTypeRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(DatabaseOperationException.class, 
            () -> trainingTypeService.findById(1L));
        
        assertTrue(exception.getCause() instanceof ResourceNotFoundException);
    }

    @Test
    void findById_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(trainingTypeRepository.findById(1L)).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> trainingTypeService.findById(1L));
    }

    @Test
    void findAll_ShouldReturnListOfTrainingTypeResponse_WhenTrainingTypesExist() {
        when(trainingTypeRepository.findAll()).thenReturn(List.of(trainingType));
        when(trainingTypeResponseMapper.mapToResponse(trainingType)).thenReturn(trainingTypeResponse);

        List<TrainingTypeResponse> result = trainingTypeService.findAll();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(trainingTypeResponse, result.get(0));
    }

    @Test
    void findAll_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(trainingTypeRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> trainingTypeService.findAll());
    }

    @Test
    void update_ShouldReturnUpdatedTrainingTypeResponse_WhenTrainingTypeExists() {
        when(trainingTypeCreateMapper.toEntity(trainingTypeRequestDTO)).thenReturn(trainingType);
        when(trainingTypeRepository.update(trainingType)).thenReturn(Optional.of(trainingType));
        when(trainingTypeResponseMapper.mapToResponse(trainingType)).thenReturn(trainingTypeResponse);

        TrainingTypeResponse result = trainingTypeService.update(trainingTypeRequestDTO);

        assertNotNull(result);
        assertEquals(trainingTypeResponse, result);
    }

    @Test
    void update_ShouldThrowIllegalArgumentException_WhenTrainingTypeRequestDTOIsNull() {
        assertThrows(IllegalArgumentException.class, () -> trainingTypeService.update(null));
    }

    @Test
    void update_ShouldThrowResourceNotFoundException_WhenTrainingTypeDoesNotExist() {
        when(trainingTypeCreateMapper.toEntity(trainingTypeRequestDTO)).thenReturn(trainingType);
        when(trainingTypeRepository.update(trainingType)).thenReturn(Optional.empty());

        Exception exception = assertThrows(DatabaseOperationException.class, 
            () -> trainingTypeService.update(trainingTypeRequestDTO));
        
        assertTrue(exception.getCause() instanceof ResourceNotFoundException);
    }

    @Test
    void update_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(trainingTypeCreateMapper.toEntity(trainingTypeRequestDTO)).thenReturn(trainingType);
        when(trainingTypeRepository.update(trainingType)).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> trainingTypeService.update(trainingTypeRequestDTO));
    }

    @Test
    void deleteById_ShouldDeleteTrainingType_WhenTrainingTypeExists() {
        when(trainingTypeRepository.deleteById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> trainingTypeService.deleteById(1L));
    }

    @Test
    void deleteById_ShouldThrowIllegalArgumentException_WhenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> trainingTypeService.deleteById(null));
    }

    @Test
    void deleteById_ShouldThrowResourceNotFoundException_WhenTrainingTypeDoesNotExist() {
        when(trainingTypeRepository.deleteById(1L)).thenReturn(false);

        Exception exception = assertThrows(DatabaseOperationException.class, 
            () -> trainingTypeService.deleteById(1L));
        
        assertTrue(exception.getCause() instanceof ResourceNotFoundException);
    }

    @Test
    void deleteById_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(trainingTypeRepository.deleteById(1L)).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> trainingTypeService.deleteById(1L));
    }
}