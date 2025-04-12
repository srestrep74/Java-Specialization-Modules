package com.sro.SpringCoreTask1.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sro.SpringCoreTask1.entity.TrainingType;
import com.sro.SpringCoreTask1.repository.impl.TrainingTypeRepositoryImpl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class TrainingTypeRepositoryImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private EntityTransaction transaction;

    @Mock
    private TypedQuery<TrainingType> query;

    @InjectMocks
    private TrainingTypeRepositoryImpl trainingTypeRepository;

    private TrainingType trainingType;

    @BeforeEach
    void setUp() {
        trainingType = new TrainingType();
        trainingType.setId(1L);
        trainingType.setTrainingTypeName("Cardio");
    }

    @Test
    void save_ShouldPersistTrainingType_WhenValid() {
        TrainingType result = trainingTypeRepository.save(trainingType);
        verify(entityManager).persist(trainingType);
        assertEquals(trainingType, result);
    }

    @Test
    void findById_ShouldReturnTrainingType_WhenExists() {
        when(entityManager.find(TrainingType.class, 1L)).thenReturn(trainingType);
        Optional<TrainingType> result = trainingTypeRepository.findById(1L);
        assertTrue(result.isPresent());
        assertEquals(trainingType, result.get());
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotExists() {
        when(entityManager.find(TrainingType.class, 1L)).thenReturn(null);
        Optional<TrainingType> result = trainingTypeRepository.findById(1L);
        assertFalse(result.isPresent());
    }

    @Test
    void findAll_ShouldReturnList_WhenTrainingTypesExist() {
        when(entityManager.createQuery("SELECT t FROM TrainingType t", TrainingType.class)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(trainingType));
        List<TrainingType> result = trainingTypeRepository.findAll();
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(trainingType, result.get(0));
    }

    @Test
    void deleteById_ShouldRemoveTrainingType_WhenExists() {
        when(entityManager.find(TrainingType.class, 1L)).thenReturn(trainingType);
        boolean result = trainingTypeRepository.deleteById(1L);
        verify(entityManager).remove(trainingType);
        assertTrue(result);
    }

    @Test
    void deleteById_ShouldReturnFalse_WhenNotExists() {
        when(entityManager.find(TrainingType.class, 1L)).thenReturn(null);
        boolean result = trainingTypeRepository.deleteById(1L);
        assertFalse(result);
    }

    @Test
    void update_ShouldMergeAndReturnUpdatedTrainingType_WhenExists() {
        when(entityManager.find(TrainingType.class, 1L)).thenReturn(trainingType);
        when(entityManager.merge(trainingType)).thenReturn(trainingType);
        Optional<TrainingType> result = trainingTypeRepository.update(trainingType);
        assertTrue(result.isPresent());
        assertEquals(trainingType, result.get());
    }

    @Test
    void update_ShouldReturnEmpty_WhenTrainingTypeDoesNotExist() {
        when(entityManager.find(TrainingType.class, 1L)).thenReturn(null);
        Optional<TrainingType> result = trainingTypeRepository.update(trainingType);
        assertFalse(result.isPresent());
    }
}