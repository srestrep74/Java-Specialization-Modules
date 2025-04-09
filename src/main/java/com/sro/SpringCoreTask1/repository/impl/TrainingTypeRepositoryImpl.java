package com.sro.SpringCoreTask1.repository.impl;

import com.sro.SpringCoreTask1.entity.TrainingType;
import com.sro.SpringCoreTask1.repository.TrainingTypeRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TrainingTypeRepositoryImpl implements TrainingTypeRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public TrainingType save(TrainingType trainingType) {
        try {
            entityManager.persist(trainingType);
            return trainingType;
        } catch (PersistenceException e) {
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
        try {
            TrainingType trainingType = entityManager.find(TrainingType.class, id);
            if (trainingType == null) {
                return false;
            }
            entityManager.remove(trainingType);
            return true;
        } catch (PersistenceException e) {
            throw e;
        }
    }

    @Override
    public Optional<TrainingType> update(TrainingType trainingType) {
        try {
            TrainingType existingTrainingType = entityManager.find(TrainingType.class, trainingType.getId());
            if (existingTrainingType == null) {
                return Optional.empty();
            }
            TrainingType updatedTrainingType = entityManager.merge(trainingType);
            return Optional.of(updatedTrainingType);
        } catch (PersistenceException e) {
            throw e;
        }
    }
}