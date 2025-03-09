package com.sro.SpringCoreTask1.repository.impl;

import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.repository.TrainerRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
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
}
