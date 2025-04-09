package com.sro.SpringCoreTask1.repository.impl;

import com.sro.SpringCoreTask1.entity.Trainee;
import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.repository.TrainerRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class TrainerRepositoryImpl implements TrainerRepository {

    @PersistenceContext 
    private EntityManager entityManager;

    @Override
    public Trainer save(Trainer trainer) {
        try {
            entityManager.persist(trainer);
            return trainer;
        } catch (PersistenceException e) {
            throw e;
        }
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Trainer.class, id));
    }

    @Override
    public List<Trainer> findAll() {
        return entityManager.createQuery("SELECT t FROM Trainer t", Trainer.class)
                           .getResultList();
    }

    @Override
    public boolean deleteById(Long id) {
        try {
            Trainer trainer = entityManager.find(Trainer.class, id);
            if (trainer == null) {
                return false;
            }
            entityManager.remove(trainer);
            return true;
        } catch (PersistenceException e) {
            throw e;
        }
    }

    @Override
    public Optional<Trainer> update(Trainer trainer) {
        try {
            Trainer existingTrainer = entityManager.find(Trainer.class, trainer.getId());
            if (existingTrainer == null) {
                return Optional.empty();
            }
            Trainer updatedTrainer = entityManager.merge(trainer);
            return Optional.of(updatedTrainer);
        } catch (PersistenceException e) {
            throw e;
        }
    }

    @Override
    public Optional<Trainer> findByUsername(String username) {
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Trainer> cq = cb.createQuery(Trainer.class);
            Root<Trainer> trainer = cq.from(Trainer.class);

            cq.where(cb.equal(trainer.get("username"), username));

            return Optional.ofNullable(entityManager.createQuery(cq).getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Trainer> findUnassignedTrainersByTraineeUsername(String traineeUsername) {
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Trainer> cq = cb.createQuery(Trainer.class);
            Root<Trainer> trainer = cq.from(Trainer.class);

            var subquery = cq.subquery(Long.class);
            Root<Trainer> subTrainer = subquery.from(Trainer.class);
            Join<Trainer, Trainee> assignedTrainees = subTrainer.join("trainees");

            subquery.select(subTrainer.get("id"))
                    .where(cb.equal(assignedTrainees.get("username"), traineeUsername));

            cq.select(trainer).where(cb.not(trainer.get("id").in(subquery)));

            return entityManager.createQuery(cq).getResultList();
        } catch (PersistenceException e) {
            throw e;
        }
    }

    @Override
    public boolean changeTrainerPassword(Long id, String newPassword) {
        try {
            Trainer trainer = entityManager.find(Trainer.class, id);
            if (trainer == null) {
                return false;
            }
            trainer.setPassword(newPassword);
            return true;
        } catch (PersistenceException e) {
            throw e;
        }
    }

    @Override
    public Set<Trainer> findTrainersByTraineeId(Long traineeId) {
        try {
            return entityManager.find(Trainee.class, traineeId).getTrainers();
        } catch (NoResultException e) {
            throw e;
        }
    }
}