package dev.sro.workload_service.domain.repository;

import dev.sro.workload_service.domain.entity.TrainingSession;

import java.time.LocalDate;
import java.util.List;

public interface TrainingSessionRepository {
    
    TrainingSession save(TrainingSession trainingSession);
    
    List<TrainingSession> findByTrainerUsernameAndTrainingDateBetween(
        String trainerUsername, 
        LocalDate startDate, 
        LocalDate endDate
    );
    
    List<TrainingSession> findByTrainerUsername(String trainerUsername);
    
    void delete(TrainingSession trainingSession);
} 