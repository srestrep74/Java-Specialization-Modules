package com.sro.SpringCoreTask1.repository.impl;

import com.sro.SpringCoreTask1.entity.Trainee;
import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.repository.TrainerRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TrainerRepositoryImpl implements TrainerRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Trainer save(Trainer entity) {
        em.persist(entity);
        return entity;
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
        Trainer entity = findById(id).orElse(null);
        if (entity != null) {
            em.remove(entity);
        }
    }

    @Override
    public Trainer update(Trainer entity) {
        return em.merge(entity);
    }

    @Override
    public Optional<Trainer> findByUsername(String username) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery<Trainer> cq = cb.createQuery(Trainer.class);
        Root<Trainer> trainer = cq.from(Trainer.class);

        Predicate usernamePredicate = cb.equal(trainer.get("username"), username);
        cq.where(usernamePredicate);

        TypedQuery<Trainer> query = this.em.createQuery(cq);
        return Optional.ofNullable(query.getSingleResult());
    }

    @Override
    public List<Trainer> findTrainersNotAssignedToTrainee(String traineeUsername){
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery<Trainer> cq = cb.createQuery(Trainer.class);
        Root<Trainer> trainer = cq.from(Trainer.class);

        Join<Trainer, Trainee> trainerTraineeJoin = trainer.join("trainees", JoinType.LEFT);

        Predicate traineeMatch = cb.equal(trainerTraineeJoin.get("username"), traineeUsername);
        Predicate trainerNotAssigned = cb.isNull(trainerTraineeJoin.get("id"));

        cq.select(trainer).where(cb.or(trainerNotAssigned, cb.not(traineeMatch)));

        TypedQuery<Trainer> query = this.em.createQuery(cq);
        return query.getResultList();
    }
}
