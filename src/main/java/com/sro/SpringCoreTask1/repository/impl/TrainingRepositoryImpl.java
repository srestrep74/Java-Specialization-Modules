package com.sro.SpringCoreTask1.repository.impl;

import com.sro.SpringCoreTask1.entity.Training;
import com.sro.SpringCoreTask1.repository.TrainingRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class TrainingRepositoryImpl implements TrainingRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public Training save(Training entity) {
        em.persist(entity);
        return entity;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Training> findById(Long id) {
        return Optional.ofNullable(em.find(Training.class, id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Training> findAll() {
        TypedQuery<Training> query = em.createQuery("SELECT t FROM Training t", Training.class);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Training entity = findById(id).orElse(null);
        if (entity != null) {
            em.remove(entity);
        }
    }

    @Override
    @Transactional
    public Training update(Training entity) {
        return em.merge(entity);
    }
}
