package com.sro.SpringCoreTask1.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.repository.impl.TrainerRepositoryImpl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class TrainerRepositoryImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private EntityTransaction transaction;

    @Mock
    private TypedQuery<Trainer> query;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private CriteriaQuery<Trainer> criteriaQuery;

    @Mock
    private Root<Trainer> root;

    @Mock
    private Predicate predicate;

    @InjectMocks
    private TrainerRepositoryImpl trainerRepository;

    private Trainer trainer;

    @BeforeEach
    void setUp() {
        trainer = new Trainer();
        trainer.setId(1L);
        trainer.setFirstName("John");
        trainer.setLastName("Doe");
        trainer.setUsername("johndoe");
        trainer.setPassword("password");
    }

    @Test
    void save_ShouldPersistTrainer_WhenValid() {
        when(entityManager.getTransaction()).thenReturn(transaction);

        Trainer result = trainerRepository.save(trainer);

        verify(entityManager).persist(trainer);
        verify(transaction).begin();
        verify(transaction).commit();
        assertEquals(trainer, result);
    }

    @Test
    void findById_ShouldReturnTrainer_WhenExists() {
        when(entityManager.find(Trainer.class, 1L)).thenReturn(trainer);

        Optional<Trainer> result = trainerRepository.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(trainer, result.get());
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotExists() {
        when(entityManager.find(Trainer.class, 1L)).thenReturn(null);

        Optional<Trainer> result = trainerRepository.findById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    void findAll_ShouldReturnList_WhenTrainersExist() {
        when(entityManager.createQuery("SELECT t FROM Trainer t", Trainer.class)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(trainer));

        List<Trainer> result = trainerRepository.findAll();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(trainer, result.get(0));
    }

    @Test
    void deleteById_ShouldRemoveTrainer_WhenExists() {
        when(entityManager.getTransaction()).thenReturn(transaction);
        when(entityManager.find(Trainer.class, 1L)).thenReturn(trainer);

        boolean result = trainerRepository.deleteById(1L);

        verify(entityManager).remove(trainer);
        verify(transaction).begin();
        verify(transaction).commit();
        assertTrue(result);
    }

    @Test
    void deleteById_ShouldReturnFalse_WhenNotExists() {
        when(entityManager.getTransaction()).thenReturn(transaction);
        when(entityManager.find(Trainer.class, 1L)).thenReturn(null);

        boolean result = trainerRepository.deleteById(1L);

        assertFalse(result);
    }

    @Test
    void update_ShouldMergeAndReturnUpdatedTrainer_WhenExists() {
        when(entityManager.getTransaction()).thenReturn(transaction);
        when(entityManager.find(Trainer.class, 1L)).thenReturn(trainer);
        when(entityManager.merge(trainer)).thenReturn(trainer);

        Optional<Trainer> result = trainerRepository.update(trainer);

        assertTrue(result.isPresent());
        assertEquals(trainer, result.get());
    }

    @Test
    void update_ShouldReturnEmpty_WhenTrainerDoesNotExist() {
        when(entityManager.getTransaction()).thenReturn(transaction);
        when(entityManager.find(Trainer.class, 1L)).thenReturn(null);

        Optional<Trainer> result = trainerRepository.update(trainer);

        assertFalse(result.isPresent());
    }

   @Test
    void findByUsername_ShouldReturnTrainer_WhenExists() {
        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Trainer.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Trainer.class)).thenReturn(root);
        when(criteriaBuilder.equal(root.get("username"), "johndoe")).thenReturn(mock(Predicate.class));

        when(criteriaQuery.where(any(Predicate.class))).thenReturn(criteriaQuery);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(query);
        when(query.getSingleResult()).thenReturn(trainer);

        Optional<Trainer> result = trainerRepository.findByUsername("johndoe");

        assertTrue(result.isPresent());
        assertEquals(trainer, result.get());
    }


    @Test
    void findByUsername_ShouldReturnEmpty_WhenNotExists() {
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        CriteriaQuery<Trainer> cq = mock(CriteriaQuery.class);
        Root<Trainer> trainerRoot = mock(Root.class);
        TypedQuery<Trainer> typedQuery = mock(TypedQuery.class);
    
        when(entityManager.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Trainer.class)).thenReturn(cq);
        when(cq.from(Trainer.class)).thenReturn(trainerRoot);
        when(entityManager.createQuery(cq)).thenReturn(typedQuery);

        when(typedQuery.getSingleResult()).thenThrow(new NoResultException());
    
        Optional<Trainer> result = trainerRepository.findByUsername("johndoe");
    
        assertFalse(result.isPresent());
    }
    
    
    
}
