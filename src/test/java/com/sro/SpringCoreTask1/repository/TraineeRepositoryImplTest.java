package com.sro.SpringCoreTask1.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sro.SpringCoreTask1.entity.Trainee;
import com.sro.SpringCoreTask1.repository.impl.TraineeRepositoryImpl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class TraineeRepositoryImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private EntityTransaction transaction;

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
    }

    @Test
    void save_ShouldPersistTrainee_WhenValid() {
        when(entityManager.getTransaction()).thenReturn(transaction);
        Trainee result = traineeRepository.save(trainee);
        verify(entityManager).persist(trainee);
        verify(transaction).begin();
        verify(transaction).commit();
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
        when(entityManager.getTransaction()).thenReturn(transaction);
        when(entityManager.find(Trainee.class, 1L)).thenReturn(trainee);
        boolean result = traineeRepository.deleteById(1L);
        verify(entityManager).remove(trainee);
        verify(transaction).begin();
        verify(transaction).commit();
        assertTrue(result);
    }

    @Test
    void deleteById_ShouldReturnFalse_WhenNotExists() {
        when(entityManager.getTransaction()).thenReturn(transaction);
        when(entityManager.find(Trainee.class, 1L)).thenReturn(null);
        boolean result = traineeRepository.deleteById(1L);
        assertFalse(result);
    }

    @Test
    void update_ShouldMergeAndReturnUpdatedTrainee_WhenExists() {
        when(entityManager.getTransaction()).thenReturn(transaction);
        when(entityManager.find(Trainee.class, 1L)).thenReturn(trainee);
        when(entityManager.merge(trainee)).thenReturn(trainee);
        Optional<Trainee> result = traineeRepository.update(trainee);
        assertTrue(result.isPresent());
        assertEquals(trainee, result.get());
    }

    @Test
    void update_ShouldReturnEmpty_WhenTraineeDoesNotExist() {
        when(entityManager.getTransaction()).thenReturn(transaction);
        when(entityManager.find(Trainee.class, 1L)).thenReturn(null);
        Optional<Trainee> result = traineeRepository.update(trainee);
        assertFalse(result.isPresent());
    }

    @Test
    void findByUsername_ShouldReturnTrainee_WhenExists() {
        when(entityManager.createQuery("SELECT t FROM Trainee t WHERE t.username = :username", Trainee.class)).thenReturn(query);
        when(query.setParameter("username", "johndoe")).thenReturn(query);
        when(query.getSingleResult()).thenReturn(trainee);
        Optional<Trainee> result = traineeRepository.findByUsername("johndoe");
        assertTrue(result.isPresent());
        assertEquals(trainee, result.get());
    }

    @Test
    void findByUsername_ShouldReturnEmpty_WhenNotExists() {
        when(entityManager.createQuery("SELECT t FROM Trainee t WHERE t.username = :username", Trainee.class)).thenReturn(query);
        when(query.setParameter("username", "johndoe")).thenReturn(query);
        when(query.getSingleResult()).thenThrow(new NoResultException());
        Optional<Trainee> result = traineeRepository.findByUsername("johndoe");
        assertFalse(result.isPresent());
    }

    @Test
    void deleteByUsername_ShouldRemoveTrainee_WhenExists() {
        when(entityManager.getTransaction()).thenReturn(transaction);
        when(entityManager.createQuery("SELECT t FROM Trainee t WHERE t.username = :username", Trainee.class)).thenReturn(query);
        when(query.setParameter("username", "johndoe")).thenReturn(query);
        when(query.getSingleResult()).thenReturn(trainee);
        boolean result = traineeRepository.deleteByUsername("johndoe");
        verify(entityManager).remove(trainee);
        verify(transaction).begin();
        verify(transaction).commit();
        assertTrue(result);
    }

    @Test
    void deleteByUsername_ShouldReturnFalse_WhenNotExists() {
        when(entityManager.getTransaction()).thenReturn(transaction);
        when(entityManager.createQuery("SELECT t FROM Trainee t WHERE t.username = :username", Trainee.class)).thenReturn(query);
        when(query.setParameter("username", "johndoe")).thenReturn(query);
        when(query.getSingleResult()).thenThrow(new NoResultException());
        boolean result = traineeRepository.deleteByUsername("johndoe");
        assertFalse(result);
    }
}