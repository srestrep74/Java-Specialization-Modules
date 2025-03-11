package com.sro.SpringCoreTask1.repository.impl;

import com.sro.SpringCoreTask1.entity.Trainee;
import com.sro.SpringCoreTask1.repository.TraineeRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TraineeRepositoryImpl implements TraineeRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Trainee save(Trainee entity) {
        em.persist(entity);
        return entity;
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        return Optional.ofNullable(em.find(Trainee.class, id));
    }

    @Override
    public List<Trainee> findAll() {
        TypedQuery<Trainee> query = em.createQuery("SELECT t FROM Trainee t", Trainee.class);
        return query.getResultList();
    }

    @Override
    public void deleteById(Long id) {
        Trainee entity = findById(id).orElse(null);
        if (entity != null) {
            em.remove(entity);
        }
    }

    @Override
    public Trainee update(Trainee entity) {
        return em.merge(entity);
    }

    @Override
    public Optional<Trainee> findByUsername(String username) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery<Trainee> cq = cb.createQuery(Trainee.class);
        Root<Trainee> trainee = cq.from(Trainee.class);

        Predicate usernamePredicate = cb.equal(trainee.get("username"), username);
        cq.where(usernamePredicate);

        TypedQuery<Trainee> query = this.em.createQuery(cq);
        return Optional.ofNullable(query.getSingleResult());
    }

    @Override
    public void deleteByUsername(String username) {
        this.em.createQuery("DELETE FROM Trainee t WHERE t.username = :username")
                .setParameter("username", username)
                .executeUpdate();
    }
}
