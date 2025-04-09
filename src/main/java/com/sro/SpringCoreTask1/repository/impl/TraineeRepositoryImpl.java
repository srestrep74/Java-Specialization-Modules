package com.sro.SpringCoreTask1.repository.impl;

import com.sro.SpringCoreTask1.entity.Trainee;
import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.repository.TraineeRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;

import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class TraineeRepositoryImpl implements TraineeRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Trainee save(Trainee trainee) {
        try {
            entityManager.persist(trainee);
            return trainee;
        } catch (PersistenceException e) {
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
        try {
            Trainee trainee = entityManager.find(Trainee.class, id);
            if (trainee == null) {
                return false;
            }
            entityManager.remove(trainee);
            return true;
        } catch (PersistenceException e) {
            throw e;
        }
    }

    @Override
    public Optional<Trainee> update(Trainee trainee) {
        try {
            Trainee existingTrainee = entityManager.find(Trainee.class, trainee.getId());
            if (existingTrainee == null) {
                return Optional.empty();
            }
            Trainee updatedTrainee = entityManager.merge(trainee);
            return Optional.of(updatedTrainee);
        } catch (PersistenceException e) {
            throw e;
        }
    }

    @Override
    public Optional<Trainee> findByUsername(String username) {
        try {
            return Optional.ofNullable(entityManager.createQuery("SELECT t FROM Trainee t LEFT JOIN FETCH t.trainers WHERE t.username = :username", Trainee.class)
                                                   .setParameter("username", username)
                                                   .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean deleteByUsername(String username) {
        try {
            Trainee trainee = entityManager.createQuery("SELECT t FROM Trainee t LEFT JOIN FETCH t.trainers WHERE t.username = :username", Trainee.class)
                                            .setParameter("username", username)
                                            .getSingleResult();
            if (trainee == null) {
                return false;
            }

            for (Trainer trainer : new HashSet<>(trainee.getTrainers())) {
                trainee.removeTrainer(trainer);
            }

            entityManager.remove(entityManager.contains(trainee) ? trainee : entityManager.merge(trainee));
            return true;
        } catch (PersistenceException e) {
            throw e;
        }
    }

    @Override
    public boolean updatePassword(Long id, String newPassword) {
        try {
            Trainee trainee = entityManager.find(Trainee.class, id);
            if (trainee == null) {
                return false;
            }
            trainee.setPassword(newPassword);
            return true;
        } catch (PersistenceException e) {
            throw e;
        }
    }

    @Override
    public Set<Trainer> findTrainersByTraineeId(Long traineeId) {
        Trainee trainee = entityManager.find(Trainee.class, traineeId);
        return trainee != null ? trainee.getTrainers() : new HashSet<>();
    }
}