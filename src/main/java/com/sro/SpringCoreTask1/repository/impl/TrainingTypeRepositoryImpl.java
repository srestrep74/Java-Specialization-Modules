package com.sro.SpringCoreTask1.repository.impl;

import com.sro.SpringCoreTask1.entity.TrainingType;
import com.sro.SpringCoreTask1.repository.TrainingTypeRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class TrainingTypeRepositoryImpl implements TrainingTypeRepository {

    private EntityManager em;

    public TrainingTypeRepositoryImpl(EntityManager em){
        this.em = em;
    }

    @Override
    @Transactional
    public TrainingType save(TrainingType entity) {
        em.persist(entity);
        return entity;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TrainingType> findById(Long id) {
        return Optional.ofNullable(em.find(TrainingType.class, id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingType> findAll() {
        TypedQuery<TrainingType> query = em.createQuery("SELECT t FROM TrainingType t", TrainingType.class);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        TrainingType entity = findById(id).orElse(null);
        if (entity != null) {
            em.remove(entity);
        }
    }

    @Override
    @Transactional
    public TrainingType update(TrainingType entity) {
        return em.merge(entity);
    }
}
