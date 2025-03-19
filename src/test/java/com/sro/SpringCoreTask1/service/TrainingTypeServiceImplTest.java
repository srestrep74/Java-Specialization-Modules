package com.sro.SpringCoreTask1.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sro.SpringCoreTask1.dto.request.TrainingTypeRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TrainingTypeResponseDTO;
import com.sro.SpringCoreTask1.service.impl.TrainingTypeServiceImpl;
import com.sro.SpringCoreTask1.entity.TrainingType;
import com.sro.SpringCoreTask1.exception.DatabaseOperationException;
import com.sro.SpringCoreTask1.exception.ResourceAlreadyExistsException;
import com.sro.SpringCoreTask1.exception.ResourceNotFoundException;
import com.sro.SpringCoreTask1.mappers.TrainingTypeMapper;
import com.sro.SpringCoreTask1.repository.TrainingTypeRepository;

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
    private TrainingTypeMapper trainingTypeMapper;

    @InjectMocks
    private TrainingTypeServiceImpl trainingTypeService;

    private TrainingType trainingType;
    private TrainingTypeRequestDTO trainingTypeRequestDTO;
    private TrainingTypeResponseDTO trainingTypeResponseDTO;

    @BeforeEach
    void setUp() {
        trainingType = new TrainingType();
        trainingType.setId(1L);
        trainingType.setTrainingTypeName("Fitness");

        trainingTypeRequestDTO = new TrainingTypeRequestDTO("Fitness");
        trainingTypeResponseDTO = new TrainingTypeResponseDTO(1L, "Fitness");
    }

    @Test
    void save_ShouldReturnTrainingTypeResponseDTO_WhenValidInput() {
        when(trainingTypeMapper.toEntity(trainingTypeRequestDTO)).thenReturn(trainingType);
        when(trainingTypeRepository.save(trainingType)).thenReturn(trainingType);
        when(trainingTypeMapper.toDTO(trainingType)).thenReturn(trainingTypeResponseDTO);

        TrainingTypeResponseDTO result = trainingTypeService.save(trainingTypeRequestDTO);

        assertNotNull(result);
        assertEquals(trainingTypeResponseDTO, result);
        verify(trainingTypeRepository).save(trainingType);
    }

    @Test
    void save_ShouldThrowIllegalArgumentException_WhenTrainingTypeRequestDTOIsNull() {
        assertThrows(IllegalArgumentException.class, () -> trainingTypeService.save(null));
    }

    @Test
    void save_ShouldThrowResourceAlreadyExistsException_WhenTrainingTypeNameExists() {
        when(trainingTypeMapper.toEntity(trainingTypeRequestDTO)).thenReturn(trainingType);
        when(trainingTypeRepository.save(trainingType)).thenThrow(new ConstraintViolationException("Training Type name already exists", null));

        assertThrows(ResourceAlreadyExistsException.class, () -> trainingTypeService.save(trainingTypeRequestDTO));
    }

    @Test
    void save_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(trainingTypeMapper.toEntity(trainingTypeRequestDTO)).thenReturn(trainingType);
        when(trainingTypeRepository.save(trainingType)).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> trainingTypeService.save(trainingTypeRequestDTO));
    }

    @Test
    void findById_ShouldReturnTrainingTypeResponseDTO_WhenTrainingTypeExists() {
        when(trainingTypeRepository.findById(1L)).thenReturn(Optional.of(trainingType));
        when(trainingTypeMapper.toDTO(trainingType)).thenReturn(trainingTypeResponseDTO);

        TrainingTypeResponseDTO result = trainingTypeService.findById(1L);

        assertNotNull(result);
        assertEquals(trainingTypeResponseDTO, result);
    }

    @Test
    void findById_ShouldThrowIllegalArgumentException_WhenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> trainingTypeService.findById(null));
    }

    @Test
    void findById_ShouldThrowResourceNotFoundException_WhenTrainingTypeDoesNotExist() {
        when(trainingTypeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> trainingTypeService.findById(1L));
    }

    @Test
    void findById_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(trainingTypeRepository.findById(1L)).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> trainingTypeService.findById(1L));
    }

    @Test
    void findAll_ShouldReturnListOfTrainingTypeResponseDTO_WhenTrainingTypesExist() {
        when(trainingTypeRepository.findAll()).thenReturn(List.of(trainingType));
        when(trainingTypeMapper.toDTO(trainingType)).thenReturn(trainingTypeResponseDTO);

        List<TrainingTypeResponseDTO> result = trainingTypeService.findAll();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(trainingTypeResponseDTO, result.get(0));
    }

    @Test
    void findAll_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(trainingTypeRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> trainingTypeService.findAll());
    }

    @Test
    void update_ShouldReturnUpdatedTrainingTypeResponseDTO_WhenTrainingTypeExists() {
        when(trainingTypeMapper.toEntity(trainingTypeRequestDTO)).thenReturn(trainingType);
        when(trainingTypeRepository.update(trainingType)).thenReturn(Optional.of(trainingType));
        when(trainingTypeMapper.toDTO(trainingType)).thenReturn(trainingTypeResponseDTO);

        TrainingTypeResponseDTO result = trainingTypeService.update(trainingTypeRequestDTO);

        assertNotNull(result);
        assertEquals(trainingTypeResponseDTO, result);
    }

    @Test
    void update_ShouldThrowIllegalArgumentException_WhenTrainingTypeRequestDTOIsNull() {
        assertThrows(IllegalArgumentException.class, () -> trainingTypeService.update(null));
    }

    @Test
    void update_ShouldThrowResourceNotFoundException_WhenTrainingTypeDoesNotExist() {
        when(trainingTypeMapper.toEntity(trainingTypeRequestDTO)).thenReturn(trainingType);
        when(trainingTypeRepository.update(trainingType)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> trainingTypeService.update(trainingTypeRequestDTO));
    }

    @Test
    void update_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(trainingTypeMapper.toEntity(trainingTypeRequestDTO)).thenReturn(trainingType);
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

        assertThrows(ResourceNotFoundException.class, () -> trainingTypeService.deleteById(1L));
    }

    @Test
    void deleteById_ShouldThrowDatabaseOperationException_WhenErrorOccurs() {
        when(trainingTypeRepository.deleteById(1L)).thenThrow(new RuntimeException("Database error"));

        assertThrows(DatabaseOperationException.class, () -> trainingTypeService.deleteById(1L));
    }
}