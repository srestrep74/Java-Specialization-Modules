package com.sro.SpringCoreTask1.repository.impl;

import com.sro.SpringCoreTask1.entity.Trainee;
import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.repository.TrainerRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class TrainerRepositoryImpl implements TrainerRepository {

    private final EntityManager entityManager;

    public TrainerRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Trainer save(Trainer trainer) {
        EntityTransaction transaction = null;
        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            entityManager.persist(trainer);
            transaction.commit();
            return trainer;
        } catch (PersistenceException e) {
            rollbackTransaction(transaction);
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
        EntityTransaction transaction = null;
        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            Trainer trainer = entityManager.find(Trainer.class, id);
            if (trainer == null) {
                rollbackTransaction(transaction);
                return false;
            }
            entityManager.remove(trainer);
            transaction.commit();
            return true;
        } catch (PersistenceException e) {
            rollbackTransaction(transaction);
            throw e;
        }
    }

    @Override
    public Optional<Trainer> update(Trainer trainer) {
        EntityTransaction transaction = null;
        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            Trainer existingTrainer = entityManager.find(Trainer.class, trainer.getId());
            if (existingTrainer == null) {
                rollbackTransaction(transaction);
                return Optional.empty();
            }
            Trainer updatedTrainer = entityManager.merge(trainer);
            transaction.commit();
            return Optional.of(updatedTrainer);
        } catch (PersistenceException e) {
            rollbackTransaction(transaction);
            throw e;
        }
    }

    @Override
    public Optional<Trainer> findByUsername(String username) {
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Trainer> cq = cb.createQuery(Trainer.class);
            Root<Trainer> trainer = cq.from(Trainer.class);

            Predicate usernamePredicate = cb.equal(trainer.get("username"), username);
            cq.where(usernamePredicate);
            return Optional.ofNullable(entityManager.createQuery(cq).getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Trainer> findTrainersNotAssignedToTrainee(String traineeUsername) {
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Trainer> cq = cb.createQuery(Trainer.class);
            Root<Trainer> trainer = cq.from(Trainer.class);

            Subquery<Long> subquery = cq.subquery(Long.class);
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
    public boolean updatePassword(Long id, String newPassword) {
        EntityTransaction transaction = null;
        try {
            transaction = entityManager.getTransaction();
            transaction.begin();
            Trainer trainer = entityManager.find(Trainer.class, id);
            if (trainer == null) {
                rollbackTransaction(transaction);
                return false;
            }
            trainer.setPassword(newPassword);
            transaction.commit();
            return true;
        } catch (NoResultException e) {
            System.out.println(e.getMessage());
            rollbackTransaction(transaction);
            return false;
        } catch (PersistenceException e) {
            rollbackTransaction(transaction);
            throw e;
        }
    }

    @Override
    public Set<Trainer> getTraineeTrainers(Long traineeId) {
        try {
            return entityManager.find(Trainee.class, traineeId).getTrainers();
        } catch (NoResultException e) {
            throw e;
        }
    }

    private void rollbackTransaction(EntityTransaction transaction) {
        if (transaction != null && transaction.isActive()) {
            transaction.rollback();
        }
    }
}