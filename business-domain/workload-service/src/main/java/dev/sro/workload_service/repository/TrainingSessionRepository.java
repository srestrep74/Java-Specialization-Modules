package dev.sro.workload_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.sro.workload_service.entity.TrainingSession;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TrainingSessionRepository extends JpaRepository<TrainingSession, Long> {
    
    List<TrainingSession> findByTrainerUsernameAndTrainingDateBetween(
        String trainerUsername, 
        LocalDate startDate, 
        LocalDate endDate
    );
    
    List<TrainingSession> findByTrainerUsername(String trainerUsername);
} 