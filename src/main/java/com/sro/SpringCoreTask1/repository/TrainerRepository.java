package com.sro.SpringCoreTask1.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.sro.SpringCoreTask1.entity.Trainer;

public interface TrainerRepository extends JpaRepository<Trainer, Long>, JpaSpecificationExecutor<Trainer> {

    Optional<Trainer> findByUsername(String username);

    boolean existsByUsername(String username);

    @Modifying
    @Query("UPDATE Trainer t SET t.password = :newPassword WHERE t.id = :trainerId")
    void updatePassword(Long trainerId, String newPassword);

    @Query("SELECT t FROM Trainer t JOIN t.trainees tr WHERE tr.id = :traineeId")
    Set<Trainer> findTrainersByTraineeId(Long traineeId);

    @Query("SELECT COUNT(t) FROM Trainer t WHERE t.active = :active")
    long countByActive(boolean active);

}