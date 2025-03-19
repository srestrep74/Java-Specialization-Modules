package com.sro.SpringCoreTask1.repository.impl;

import com.sro.SpringCoreTask1.entity.Trainee;
import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.repository.TraineeRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;

import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class TraineeRepositoryImpl implements TraineeRepository {

    private final EntityManager entityManager;

    public TraineeRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Trainee save(Trainee trainee) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(trainee);
            transaction.commit();
            return trainee;
        } catch (PersistenceException e) {
            rollbackTransaction(transaction);
            throw e;
        }
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Trainee.class, id));
    }

    @Override
    public List<Trainee> findAll() {
        return entityManager.createQuery("SELECT t FROM Trainee t", Trainee.class)
                             .getResultList();
    }

    @Override
    public boolean deleteById(Long id) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Trainee trainee = entityManager.find(Trainee.class, id);
            if (trainee == null) {
                rollbackTransaction(transaction);
                return false;
            }
            entityManager.remove(trainee);
            transaction.commit();
            return true;
        } catch (PersistenceException e) {
            rollbackTransaction(transaction);
            throw e;
        }
    }

    @Override
    public Optional<Trainee> update(Trainee trainee) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Trainee existingTrainee = entityManager.find(Trainee.class, trainee.getId());
            if (existingTrainee == null) {
                rollbackTransaction(transaction);
                return Optional.empty();
            }
            Trainee updatedTrainee = entityManager.merge(trainee);
            transaction.commit();
            return Optional.of(updatedTrainee);
        } catch (PersistenceException e) {
            rollbackTransaction(transaction);
            throw e;
        }
    }

    @Override
    public Optional<Trainee> findByUsername(String username) {
        try {
            return Optional.ofNullable(entityManager.createQuery("SELECT t FROM Trainee t WHERE t.username = :username", Trainee.class)
                                                   .setParameter("username", username)
                                                   .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean deleteByUsername(String username) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Trainee trainee = findByUsername(username).orElse(null);
            if (trainee == null) {
                rollbackTransaction(transaction);
                return false;
            }

            for (Trainer trainer : new HashSet<>(trainee.getTrainers())) {
                trainee.removeTrainer(trainer);
            }

            entityManager.remove(trainee);
            transaction.commit();
            return true;
        } catch (PersistenceException e) {
            rollbackTransaction(transaction);
            throw e;
        }
    }

    @Override
    public boolean updatePassword(Long id, String newPassword) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Trainee trainee = entityManager.find(Trainee.class, id);
            if (trainee == null) {
                rollbackTransaction(transaction);
                return false;
            }
            trainee.setPassword(newPassword);
            transaction.commit();
            return true;
        } catch (PersistenceException e) {
            rollbackTransaction(transaction);
            throw e;
        }
    }

    @Override
    public Set<Trainer> findTrainersByTraineeId(Long traineeId) {
        Trainee trainee = entityManager.find(Trainee.class, traineeId);
        return trainee != null ? trainee.getTrainers() : new HashSet<>();
    }

    private void rollbackTransaction(EntityTransaction transaction) {
        if (transaction != null && transaction.isActive()) {
            transaction.rollback();
        }
    }
}