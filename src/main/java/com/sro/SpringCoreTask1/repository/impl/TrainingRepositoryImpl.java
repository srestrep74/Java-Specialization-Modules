package com.sro.SpringCoreTask1.repository.impl;

import com.sro.SpringCoreTask1.dto.TraineeTrainingFilterDTO;
import com.sro.SpringCoreTask1.dto.TrainerTrainingFilterDTO;
import com.sro.SpringCoreTask1.entity.Training;
import com.sro.SpringCoreTask1.repository.TrainingRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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

    @PersistenceContext
    private EntityManager em;

    @Override
    public Training save(Training entity) {
        em.persist(entity);
        return entity;
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
        Training entity = findById(id).orElse(null);
        if (entity != null) {
            em.remove(entity);
        }
    }

    @Override
    public Training update(Training entity) {
        return em.merge(entity);
    }

    @Override
    public List<Training> findTrainingsByTraineeWithFilters(TraineeTrainingFilterDTO filterDTO){
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
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
    public List<Training> findTrainingsByTrainerWithFilters(TrainerTrainingFilterDTO filterDTO){
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
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
