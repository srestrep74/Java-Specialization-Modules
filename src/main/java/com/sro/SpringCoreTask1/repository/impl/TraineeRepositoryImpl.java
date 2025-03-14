package com.sro.SpringCoreTask1.repository.impl;

import com.sro.SpringCoreTask1.entity.Trainee;
import com.sro.SpringCoreTask1.repository.TraineeRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TraineeRepositoryImpl implements TraineeRepository {

    private final EntityManager em;

    public TraineeRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public Trainee save(Trainee entity) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(entity);
            transaction.commit();
            return entity;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Error saving Trainee", e);
        }
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
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Trainee entity = em.find(Trainee.class, id);
            if (entity != null) {
                em.remove(entity);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Error deleting Trainee", e);
        }
    }

    @Override
    public Trainee update(Trainee entity) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Trainee updatedEntity = em.merge(entity);
            transaction.commit();
            return updatedEntity;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Error updating Trainee", e);
        }
    }

    @Override
    public Optional<Trainee> findByUsername(String username) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            String sql = "SELECT * FROM trainee WHERE username = :username";
            Trainee trainee = (Trainee) em.createNativeQuery(sql, Trainee.class).setParameter("username", username).getSingleResult();
            transaction.commit();
            return Optional.ofNullable(trainee);
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Error finding Trainee by username", e);
        } 
    }

    @Override
    public void deleteByUsername(String username) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            int deletedCount = em.createQuery("DELETE FROM Trainee t WHERE t.username = :username")
                    .setParameter("username", username)
                    .executeUpdate();
            transaction.commit();
            if (deletedCount == 0) {
                throw new RuntimeException("No Trainee found with username: " + username);
            }
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Error deleting Trainee by username", e);
        }
    }
}
