package dev.sro.workload_service.infrastructure.repository.jpa;

import dev.sro.workload_service.domain.entity.TrainingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TrainingSessionJpaRepository extends JpaRepository<TrainingSession, Long> {
    
    List<TrainingSession> findByTrainerUsernameAndTrainingDateBetween(
        String trainerUsername, 
        LocalDate startDate, 
        LocalDate endDate
    );
    
    List<TrainingSession> findByTrainerUsername(String trainerUsername);
} 