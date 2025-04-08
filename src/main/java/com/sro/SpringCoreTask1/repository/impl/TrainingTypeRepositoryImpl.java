package com.sro.SpringCoreTask1.repository.impl;

import com.sro.SpringCoreTask1.entity.TrainingType;
import com.sro.SpringCoreTask1.repository.TrainingTypeRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TrainingTypeRepositoryImpl implements TrainingTypeRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /* 
    public TrainingTypeRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }*/

    @Override
    public TrainingType save(TrainingType trainingType) {
        //EntityTransaction transaction = null;
        try {
            //transaction = entityManager.getTransaction();
            //transaction.begin();
            entityManager.persist(trainingType);
            //transaction.commit();
            return trainingType;
        } catch (PersistenceException e) {
            //rollbackTransaction(transaction);
            throw e;
        }
    }

    @Override
    public Optional<TrainingType> findById(Long id) {
        return Optional.ofNullable(entityManager.find(TrainingType.class, id));
    }

    @Override
    public List<TrainingType> findAll() {
        return entityManager.createQuery("SELECT t FROM TrainingType t", TrainingType.class)
                           .getResultList();
    }

    @Override
    public boolean deleteById(Long id) {
        EntityTransaction transaction = null;
        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TrainingType trainingType = entityManager.find(TrainingType.class, id);
            if (trainingType == null) {
                rollbackTransaction(transaction);
                return false;
            }
            entityManager.remove(trainingType);
            transaction.commit();
            return true;
        } catch (PersistenceException e) {
            rollbackTransaction(transaction);
            throw e;
        }
    }

    @Override
    public Optional<TrainingType> update(TrainingType trainingType) {
        EntityTransaction transaction = null;
        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            TrainingType existingTrainingType = entityManager.find(TrainingType.class, trainingType.getId());
            if (existingTrainingType == null) {
                rollbackTransaction(transaction);
                return Optional.empty();
            }
            TrainingType updatedTrainingType = entityManager.merge(trainingType);
            transaction.commit();
            return Optional.of(updatedTrainingType);
        } catch (PersistenceException e) {
            rollbackTransaction(transaction);
            throw e;
        }
    }

    private void rollbackTransaction(EntityTransaction transaction) {
        if (transaction != null && transaction.isActive()) {
            transaction.rollback();
        }
    }
}