package dev.sro.workload_service.repository;

import dev.sro.workload_service.entity.TrainingSession;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingSessionRepository extends MongoRepository<TrainingSession, String> {
    
    /**
     * Find training session by trainer and date
     */
    Optional<TrainingSession> findByTrainerUsernameAndTrainingDate(String trainerUsername, LocalDate trainingDate);
    
    /**
     * Find all training sessions for a trainer in a specific month
     */
    @Query("{'trainerUsername': ?0, 'trainingDate': {$gte: ?1, $lt: ?2}}")
    List<TrainingSession> findByTrainerUsernameAndTrainingDateBetween(String trainerUsername, LocalDate startDate, LocalDate endDate);
    
    /**
     * Find all training sessions for a trainer in a specific year and month
     */
    @Query("{'trainerUsername': ?0, 'trainingDate': {$gte: ?1, $lt: ?2}}")
    List<TrainingSession> findByTrainerUsernameAndYearAndMonth(String trainerUsername, Integer year, Integer month);
    
    /**
     * Check if training session exists for trainer and date
     */
    boolean existsByTrainerUsernameAndTrainingDate(String trainerUsername, LocalDate trainingDate);
} 