package com.sro.SpringCoreTask1.repository.impl;

import com.sro.SpringCoreTask1.dtos.v1.request.training.TraineeTrainingFilter;
import com.sro.SpringCoreTask1.dtos.v1.request.training.TrainerTrainingFilter;
import com.sro.SpringCoreTask1.entity.Trainee;
import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.entity.Training;
import com.sro.SpringCoreTask1.repository.TrainingRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class TrainingRepositoryImpl implements TrainingRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Training save(Training training) {
        try {
            entityManager.persist(training);
            training.getTrainee().getTrainings().add(training);
            training.getTrainer().getTrainings().add(training);
            training.getTrainingType().getTrainings().add(training);
            return training;
        } catch (PersistenceException e) {
            throw e;
        }
    }

    @Override
    public Optional<Training> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Training.class, id));
    }

    @Override
    public List<Training> findAll() {
        return entityManager.createQuery("SELECT t FROM Training t", Training.class)
                           .getResultList();
    }

    @Override
    public boolean deleteById(Long id) {
        try {
            Training training = entityManager.find(Training.class, id);
            if (training == null) {
                return false;
            }
            entityManager.remove(training);
            return true;
        } catch (PersistenceException e) {
            throw e;
        }
    }

    @Override
    public Optional<Training> update(Training training) {
        try {
            Training existingTraining = entityManager.find(Training.class, training.getId());
            if (existingTraining == null) {
                return Optional.empty();
            }
            Training updatedTraining = entityManager.merge(training);
            return Optional.of(updatedTraining);
        } catch (PersistenceException e) {
            throw e;
        }
    }

    @Override
    public List<Training> findTrainingsByTraineeWithFilters(TraineeTrainingFilter filterDTO, String sortField, String sortDirection) {
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Training> cq = cb.createQuery(Training.class);
            Root<Training> training = cq.from(Training.class);

            List<Predicate> predicates = new ArrayList<>();

            Optional.ofNullable(filterDTO.username())
                .filter(username -> !username.isEmpty())
                .ifPresent(username -> predicates.add(
                    cb.equal(training.get("trainee").get("username"), username)));

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
            
            Path<Object> fieldPath = training.get(sortField);
            Order order = "ASC".equalsIgnoreCase(sortDirection)
                ? cb.asc(fieldPath)
                : cb.desc(fieldPath);
            cq.orderBy(order);

            cq.where(predicates.toArray(new Predicate[0]));
            TypedQuery<Training> query = entityManager.createQuery(cq);

            return query.getResultList();
        } catch (PersistenceException e) {
            throw e;
        }
    }

    @Override
    public List<Training> findTrainingsByTrainerWithFilters(TrainerTrainingFilter filterDTO) {
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Training> cq = cb.createQuery(Training.class);
            Root<Training> training = cq.from(Training.class);

            List<Predicate> predicates = new ArrayList<>();

            Optional.ofNullable(filterDTO.username())
                .filter(username -> !username.isEmpty())
                .ifPresent(username -> predicates.add(
                    cb.equal(training.get("trainer").get("username"), username)));

            Optional.ofNullable(filterDTO.fromDate())
                    .ifPresent(from -> predicates.add(cb.greaterThanOrEqualTo(training.get("trainingDate"), from)));

            Optional.ofNullable(filterDTO.toDate())
                    .ifPresent(to -> predicates.add(cb.lessThanOrEqualTo(training.get("trainingDate"), to)));

            Optional.ofNullable(filterDTO.traineeName())
                    .filter(name -> !name.isEmpty())
                    .ifPresent(name -> predicates.add(cb.like(cb.lower(training.get("trainee").get("username")), "%" + name.toLowerCase() + "%")));

            cq.where(predicates.toArray(new Predicate[0]));
            TypedQuery<Training> query = entityManager.createQuery(cq);

            return query.getResultList();
        } catch (PersistenceException e) {
            throw e;
        }
    }

    @Override
    public boolean existsByTraineeIdAndTrainerAndTrainingDate(Trainee trainee, Trainer trainer, LocalDate trainingDate) {
        try {
            String jpql = "SELECT COUNT(t) FROM Training t " +
                            "WHERE t.trainee = :trainee " +
                            "AND t.trainer = :trainer " +
                            "AND t.trainingDate = :trainingDate";
            
            Long count = entityManager.createQuery(jpql, Long.class)
                    .setParameter("trainee", trainee)
                    .setParameter("trainer", trainer)
                    .setParameter("trainingDate", trainingDate)
                    .getSingleResult();

            return count > 0;
        } catch (PersistenceException e) {
            throw e;
        }
    }
}