package com.sro.SpringCoreTask1.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.sro.SpringCoreTask1.entity.Trainee;
import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.entity.Training;
import com.sro.SpringCoreTask1.entity.TrainingType;
import com.sro.SpringCoreTask1.repository.impl.TrainingRepositoryImpl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class TrainingRepositoryImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private EntityTransaction transaction;

    @Mock
    private TypedQuery<Training> query;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private CriteriaQuery<Training> criteriaQuery;

    @Mock
    private Root<Training> root;

    @Mock
    private TypedQuery<Long> countQuery;

    @InjectMocks
    private TrainingRepositoryImpl trainingRepository;

    private Training training;
    private Trainee trainee;
    private Trainer trainer;
    private TrainingType trainingType;

    @BeforeEach
    void setUp() {
        trainee = new Trainee();
        trainee.setUsername("trainee1");
        
        trainer = new Trainer();
        trainer.setUsername("trainer1");
        
        trainingType = new TrainingType();
        trainingType.setTrainingTypeName("type1");

        training = new Training();
        training.setId(1L);
        training.setTrainingDate(LocalDate.now());
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(trainingType);
    }

    @Test
    void save_ShouldPersistTraining_WhenValid() {
        Training result = trainingRepository.save(training);
        verify(entityManager).persist(training);
        assertEquals(training, result);
    }

    @Test
    void save_ShouldThrowException_WhenPersistenceFails() {
        doThrow(new PersistenceException("DB error")).when(entityManager).persist(any(Training.class));
        assertThrows(PersistenceException.class, () -> trainingRepository.save(training));
    }

    @Test
    void findById_ShouldReturnTraining_WhenExists() {
        when(entityManager.find(Training.class, 1L)).thenReturn(training);
        Optional<Training> result = trainingRepository.findById(1L);
        assertTrue(result.isPresent());
        assertEquals(training, result.get());
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotExists() {
        when(entityManager.find(Training.class, 1L)).thenReturn(null);
        Optional<Training> result = trainingRepository.findById(1L);
        assertFalse(result.isPresent());
    }

    @Test
    void findAll_ShouldReturnList_WhenTrainingsExist() {
        when(entityManager.createQuery("SELECT t FROM Training t", Training.class)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(training));
        List<Training> result = trainingRepository.findAll();
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(training, result.get(0));
    }

    @Test
    void findAll_ShouldReturnEmptyList_WhenNoTrainingsExist() {
        when(entityManager.createQuery("SELECT t FROM Training t", Training.class)).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.emptyList());
        List<Training> result = trainingRepository.findAll();
        assertTrue(result.isEmpty());
    }

    @Test
    void deleteById_ShouldRemoveTraining_WhenExists() {
        when(entityManager.find(Training.class, 1L)).thenReturn(training);
        boolean result = trainingRepository.deleteById(1L);
        verify(entityManager).remove(training);
        assertTrue(result);
    }

    @Test
    void deleteById_ShouldReturnFalse_WhenNotExists() {
        when(entityManager.find(Training.class, 1L)).thenReturn(null);
        boolean result = trainingRepository.deleteById(1L);
        assertFalse(result);
    }

    @Test
    void deleteById_ShouldRollbackAndRethrow_WhenExceptionOccurs() {
        when(entityManager.find(Training.class, 1L)).thenReturn(training);
        doThrow(new PersistenceException("Delete error")).when(entityManager).remove(any(Training.class));
        assertThrows(PersistenceException.class, () -> trainingRepository.deleteById(1L));
    }

    @Test
    void update_ShouldMergeAndReturnUpdatedTraining_WhenExists() {
        when(entityManager.find(Training.class, 1L)).thenReturn(training);
        when(entityManager.merge(training)).thenReturn(training);
        Optional<Training> result = trainingRepository.update(training);
        assertTrue(result.isPresent());
        assertEquals(training, result.get());
    }

    @Test
    void update_ShouldReturnEmpty_WhenTrainingDoesNotExist() {
        when(entityManager.find(Training.class, 1L)).thenReturn(null);
        Optional<Training> result = trainingRepository.update(training);
        assertFalse(result.isPresent());
    }

    @Test
    void existsByTraineeIdAndTrainerAndTrainingDate_ShouldReturnTrue_WhenExists() {
        String jpql = "SELECT COUNT(t) FROM Training t " +
                      "WHERE t.trainee = :trainee " +
                      "AND t.trainer = :trainer " +
                      "AND t.trainingDate = :trainingDate";

        when(entityManager.createQuery(jpql, Long.class)).thenReturn(countQuery);
        when(countQuery.setParameter("trainee", trainee)).thenReturn(countQuery);
        when(countQuery.setParameter("trainer", trainer)).thenReturn(countQuery);
        when(countQuery.setParameter("trainingDate", training.getTrainingDate())).thenReturn(countQuery);
        when(countQuery.getSingleResult()).thenReturn(1L);
        
        boolean result = trainingRepository.existsByTraineeIdAndTrainerAndTrainingDate(
            trainee, trainer, training.getTrainingDate());
        
        assertTrue(result);
    }

    @Test
    void existsByTraineeIdAndTrainerAndTrainingDate_ShouldReturnFalse_WhenNotExists() {
        String jpql = "SELECT COUNT(t) FROM Training t " +
                      "WHERE t.trainee = :trainee " +
                      "AND t.trainer = :trainer " +
                      "AND t.trainingDate = :trainingDate";
        
        when(entityManager.createQuery(jpql, Long.class)).thenReturn(countQuery);
        when(countQuery.setParameter("trainee", trainee)).thenReturn(countQuery);
        when(countQuery.setParameter("trainer", trainer)).thenReturn(countQuery);
        when(countQuery.setParameter("trainingDate", training.getTrainingDate())).thenReturn(countQuery);
        when(countQuery.getSingleResult()).thenReturn(0L);
        
        boolean result = trainingRepository.existsByTraineeIdAndTrainerAndTrainingDate(
            trainee, trainer, training.getTrainingDate());
        
        assertFalse(result);
    }
}