package com.sro.SpringCoreTask1.repository.impl;

import com.sro.SpringCoreTask1.dto.TraineeTrainingFilterDTO;
import com.sro.SpringCoreTask1.dto.TrainerTrainingFilterDTO;
import com.sro.SpringCoreTask1.entity.Training;
import com.sro.SpringCoreTask1.repository.TrainingRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class TrainingRepositoryImpl implements TrainingRepository {

    private final EntityManager em;

    public TrainingRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public Training save(Training entity) {
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
    public Optional<Training> findById(Long id) {
        return Optional.ofNullable(em.find(Training.class, id));
    }

    @Override
    public List<Training> findAll() {
        TypedQuery<Training> query = em.createQuery("SELECT t FROM Training t", Training.class);
        return query.getResultList();
    }

    @Override
    public void deleteById(Long id) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Training entity = findById(id).orElse(null);
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
    public Training update(Training entity) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Training updatedEntity = em.merge(entity);
            tx.commit();
            return updatedEntity;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }

    @Override
    public List<Training> findTrainingsByTraineeWithFilters(TraineeTrainingFilterDTO filterDTO) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Training> cq = cb.createQuery(Training.class);
        Root<Training> training = cq.from(Training.class);

        List<Predicate> predicates = new ArrayList<>();

        Optional.ofNullable(filterDTO.traineeId())
                .ifPresent(id -> predicates.add(cb.equal(training.get("trainee").get("id"), id)));

        Optional.ofNullable(filterDTO.fromDate())
                .ifPresent(from -> predicates.add(cb.greaterThanOrEqualTo(training.get("trainingDate"), from)));

        Optional.ofNullable(filterDTO.toDate())
                .ifPresent(to -> predicates.add(cb.lessThanOrEqualTo(training.get("trainingDate"), to)));

        Optional.ofNullable(filterDTO.trainerName())
                .filter(name -> !name.isEmpty())
                .ifPresent(name -> predicates.add(cb.like(cb.lower(training.get("trainer").get("username")), "%" + name.toLowerCase() + "%")));

        Optional.ofNullable(filterDTO.trainingType())
                .filter(type -> !type.isEmpty())
                .ifPresent(type -> predicates.add(cb.equal(training.get("trainingType").get("name"), type)));

        cq.where(predicates.toArray(new Predicate[0]));
        TypedQuery<Training> query = em.createQuery(cq);

        return query.getResultList();
    }

    @Override
    public List<Training> findTrainingsByTrainerWithFilters(TrainerTrainingFilterDTO filterDTO) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Training> cq = cb.createQuery(Training.class);
        Root<Training> training = cq.from(Training.class);

        List<Predicate> predicates = new ArrayList<>();

        Optional.ofNullable(filterDTO.trainerId())
                .ifPresent(id -> predicates.add(cb.equal(training.get("trainer").get("id"), id)));

        Optional.ofNullable(filterDTO.fromDate())
                .ifPresent(from -> predicates.add(cb.greaterThanOrEqualTo(training.get("trainingDate"), from)));

        Optional.ofNullable(filterDTO.toDate())
                .ifPresent(to -> predicates.add(cb.lessThanOrEqualTo(training.get("trainingDate"), to)));

        Optional.ofNullable(filterDTO.traineeName())
                .filter(name -> !name.isEmpty())
                .ifPresent(name -> predicates.add(cb.like(cb.lower(training.get("trainee").get("username")), "%" + name.toLowerCase() + "%")));

        Optional.ofNullable(filterDTO.trainingType())
                .filter(type -> !type.isEmpty())
                .ifPresent(type -> predicates.add(cb.equal(training.get("trainingType").get("name"), type)));

        cq.where(predicates.toArray(new Predicate[0]));
        TypedQuery<Training> query = em.createQuery(cq);

        return query.getResultList();
    }
}
