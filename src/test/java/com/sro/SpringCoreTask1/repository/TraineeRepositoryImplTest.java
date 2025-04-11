package com.sro.SpringCoreTask1.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sro.SpringCoreTask1.entity.Trainee;
import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.repository.impl.TraineeRepositoryImpl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class TraineeRepositoryImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<Trainee> query;

    @InjectMocks
    private TraineeRepositoryImpl traineeRepository;

    private Trainee trainee;

    @BeforeEach
    void setUp() {
        trainee = new Trainee();
        trainee.setId(1L);
        trainee.setFirstName("Drake");
        trainee.setLastName("Malfoy");
        trainee.setUsername("drakemalfoy");
        trainee.setPassword("password");
        trainee.setActive(true);
        trainee.setAddress("123 Street");
        trainee.setDateOfBirth(LocalDate.of(2000, 1, 1));
        trainee.setTrainers(new HashSet<>());
    }

    @Test
    void save_ShouldPersistTrainee_WhenValid() {
        Trainee result = traineeRepository.save(trainee);
        verify(entityManager).persist(trainee);
        assertEquals(trainee, result);
    }

    @Test
    void findById_ShouldReturnTrainee_WhenExists() {
        when(entityManager.find(Trainee.class, 1L)).thenReturn(trainee);
        Optional<Trainee> result = traineeRepository.findById(1L);
        assertTrue(result.isPresent());
        assertEquals(trainee, result.get());
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotExists() {
        when(entityManager.find(Trainee.class, 1L)).thenReturn(null);
        Optional<Trainee> result = traineeRepository.findById(1L);
        assertFalse(result.isPresent());
    }

    @Test
    void findAll_ShouldReturnList_WhenTraineesExist() {
        when(entityManager.createQuery("SELECT t FROM Trainee t", Trainee.class)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(trainee));
        List<Trainee> result = traineeRepository.findAll();
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(trainee, result.get(0));
    }

    @Test
    void deleteById_ShouldRemoveTrainee_WhenExists() {
        when(entityManager.find(Trainee.class, 1L)).thenReturn(trainee);
        boolean result = traineeRepository.deleteById(1L);
        verify(entityManager).remove(trainee);
        assertTrue(result);
    }

    @Test
    void deleteById_ShouldReturnFalse_WhenNotExists() {
        when(entityManager.find(Trainee.class, 1L)).thenReturn(null);
        boolean result = traineeRepository.deleteById(1L);
        assertFalse(result);
    }

    @Test
    void update_ShouldMergeAndReturnUpdatedTrainee_WhenExists() {
        when(entityManager.find(Trainee.class, 1L)).thenReturn(trainee);
        when(entityManager.merge(trainee)).thenReturn(trainee);
        Optional<Trainee> result = traineeRepository.update(trainee);
        assertTrue(result.isPresent());
        assertEquals(trainee, result.get());
    }

    @Test
    void update_ShouldReturnEmpty_WhenTraineeDoesNotExist() {
        when(entityManager.find(Trainee.class, 1L)).thenReturn(null);
        Optional<Trainee> result = traineeRepository.update(trainee);
        assertFalse(result.isPresent());
    }

    @Test
    void findByUsername_ShouldReturnTrainee_WhenExists() {
        when(entityManager.createQuery("SELECT t FROM Trainee t LEFT JOIN FETCH t.trainers WHERE t.username = :username", Trainee.class))
            .thenReturn(query);
        when(query.setParameter("username", "drakemalfoy")).thenReturn(query);
        when(query.getSingleResult()).thenReturn(trainee);
        Optional<Trainee> result = traineeRepository.findByUsername("drakemalfoy");
        assertTrue(result.isPresent());
        assertEquals(trainee, result.get());
    }

    @Test
    void findByUsername_ShouldReturnEmpty_WhenNotExists() {
        when(entityManager.createQuery("SELECT t FROM Trainee t LEFT JOIN FETCH t.trainers WHERE t.username = :username", Trainee.class))
            .thenReturn(query);
        when(query.setParameter("username", "drakemalfoy")).thenReturn(query);
        when(query.getSingleResult()).thenThrow(new NoResultException());
        Optional<Trainee> result = traineeRepository.findByUsername("drakemalfoy");
        assertFalse(result.isPresent());
    }

    @Test
    void deleteByUsername_ShouldRemoveTrainee_WhenExists() {
        when(entityManager.createQuery("SELECT t FROM Trainee t LEFT JOIN FETCH t.trainers WHERE t.username = :username", Trainee.class))
            .thenReturn(query);
        when(query.setParameter("username", "drakemalfoy")).thenReturn(query);
        when(query.getSingleResult()).thenReturn(trainee);
        when(entityManager.contains(trainee)).thenReturn(true);
        
        boolean result = traineeRepository.deleteByUsername("drakemalfoy");
        verify(entityManager).remove(trainee);
        assertTrue(result);
    }

    @Test
    void deleteByUsername_ShouldReturnFalse_WhenNotExists() {
        when(entityManager.createQuery("SELECT t FROM Trainee t LEFT JOIN FETCH t.trainers WHERE t.username = :username", Trainee.class))
            .thenReturn(query);
        when(query.setParameter("username", "drakemalfoy")).thenReturn(query);
        when(query.getSingleResult()).thenThrow(new NoResultException());
        boolean result = traineeRepository.deleteByUsername("drakemalfoy");
        assertFalse(result);
    }

    @Test
    void updatePassword_ShouldUpdate_WhenTraineeExists() {
        when(entityManager.find(Trainee.class, 1L)).thenReturn(trainee);
        boolean result = traineeRepository.updatePassword(1L, "newPassword");
        assertEquals("newPassword", trainee.getPassword());
        assertTrue(result);
    }

    @Test
    void updatePassword_ShouldReturnFalse_WhenTraineeNotExists() {
        when(entityManager.find(Trainee.class, 1L)).thenReturn(null);
        boolean result = traineeRepository.updatePassword(1L, "newPassword");
        assertFalse(result);
    }

    @Test
    void findTrainersByTraineeId_ShouldReturnTrainers_WhenTraineeExists() {
        Trainer trainer = new Trainer();
        trainee.getTrainers().add(trainer);
        
        when(entityManager.find(Trainee.class, 1L)).thenReturn(trainee);
        Set<Trainer> result = traineeRepository.findTrainersByTraineeId(1L);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertTrue(result.contains(trainer));
    }

    @Test
    void findTrainersByTraineeId_ShouldReturnEmptySet_WhenTraineeNotExists() {
        when(entityManager.find(Trainee.class, 1L)).thenReturn(null);
        Set<Trainer> result = traineeRepository.findTrainersByTraineeId(1L);
        assertTrue(result.isEmpty());
    }
}