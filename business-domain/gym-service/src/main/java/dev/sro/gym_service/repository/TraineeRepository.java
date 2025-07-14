package dev.sro.gym_service.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import dev.sro.gym_service.entity.Trainee;
import dev.sro.gym_service.entity.Trainer;

@Repository
public interface TraineeRepository extends JpaRepository<Trainee, Long> {

    Optional<Trainee> findByUsername(String username);

    boolean existsByUsername(String username);

    @Modifying
    @Query("UPDATE Trainee t SET t.password = :newPassword WHERE t.id = :id")
    int updatePassword(@Param("id") Long id, @Param("newPassword") String newPassword);

    @Modifying
    @Query("DELETE FROM Trainee t WHERE t.username = :username")
    int deleteByUsername(@Param("username") String username);

    @Query("SELECT tr FROM Trainee t JOIN t.trainers tr WHERE t.id = :traineeId")
    Set<Trainer> findTrainersByTraineeId(@Param("traineeId") Long traineeId);

    @Query("SELECT COUNT(t) FROM Trainee t WHERE t.active = true")
    long countByActive(boolean active);
}