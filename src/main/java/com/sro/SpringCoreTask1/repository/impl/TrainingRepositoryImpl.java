package com.sro.SpringCoreTask1.repository.impl;

import com.sro.SpringCoreTask1.entity.Training;
import com.sro.SpringCoreTask1.repository.TrainingRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

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
}
