package com.sro.SpringCoreTask1.repository.impl;

import com.sro.SpringCoreTask1.entity.TrainingType;
import com.sro.SpringCoreTask1.repository.TrainingTypeRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TrainingTypeRepositoryImpl implements TrainingTypeRepository {

    private final EntityManager em;

    public TrainingTypeRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public TrainingType save(TrainingType entity) {
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
    public Optional<TrainingType> findById(Long id) {
        return Optional.ofNullable(em.find(TrainingType.class, id));
    }

    @Override
    public List<TrainingType> findAll() {
        TypedQuery<TrainingType> query = em.createQuery("SELECT t FROM TrainingType t", TrainingType.class);
        return query.getResultList();
    }

    @Override
    public void deleteById(Long id) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            TrainingType entity = findById(id).orElse(null);
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
    public TrainingType update(TrainingType entity) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            TrainingType mergedEntity = em.merge(entity);
            tx.commit();
            return mergedEntity;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }
}
