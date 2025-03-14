package com.sro.SpringCoreTask1.repository.impl;

import com.sro.SpringCoreTask1.entity.Trainee;
import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.repository.TrainerRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TrainerRepositoryImpl implements TrainerRepository {

    private final EntityManager em;

    public TrainerRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public Trainer save(Trainer entity) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(entity);
            tx.commit();
            return entity;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e; 
        }
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        return Optional.ofNullable(em.find(Trainer.class, id)); 
    }

    @Override
    public List<Trainer> findAll() {
        TypedQuery<Trainer> query = em.createQuery("SELECT t FROM Trainer t", Trainer.class);
        return query.getResultList(); 
    }

    @Override
    public void deleteById(Long id) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Trainer entity = em.find(Trainer.class, id);
            if (entity != null) {
                em.remove(entity);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e; 
        }
    }

    @Override
    public Trainer update(Trainer entity) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Trainer mergedEntity = em.merge(entity);
            tx.commit();
            return mergedEntity;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e; 
        }
    }

    @Override
    public Optional<Trainer> findByUsername(String username) {
        try {
            CriteriaBuilder cb = this.em.getCriteriaBuilder();
            CriteriaQuery<Trainer> cq = cb.createQuery(Trainer.class);
            Root<Trainer> trainer = cq.from(Trainer.class);

            Predicate usernamePredicate = cb.equal(trainer.get("username"), username);
            cq.where(usernamePredicate);

            TypedQuery<Trainer> query = this.em.createQuery(cq);
            return Optional.ofNullable(query.getSingleResult());
        } catch (Exception e) {
            throw e; 
        }
    }

    @Override
    public List<Trainer> findTrainersNotAssignedToTrainee(String traineeUsername) {
        try {
            CriteriaBuilder cb = this.em.getCriteriaBuilder();
            CriteriaQuery<Trainer> cq = cb.createQuery(Trainer.class);
            Root<Trainer> trainer = cq.from(Trainer.class);

            Subquery<Long> subquery = cq.subquery(Long.class);
            Root<Trainer> subTrainer = subquery.from(Trainer.class);
            Join<Trainer, Trainee> assignedTrainees = subTrainer.join("trainees");

            subquery.select(subTrainer.get("id"))
                    .where(cb.equal(assignedTrainees.get("username"), traineeUsername));

            cq.select(trainer).where(cb.not(trainer.get("id").in(subquery)));

            TypedQuery<Trainer> query = this.em.createQuery(cq);
            return query.getResultList();
        } catch (Exception e) {
            throw e; 
        }
    }
}