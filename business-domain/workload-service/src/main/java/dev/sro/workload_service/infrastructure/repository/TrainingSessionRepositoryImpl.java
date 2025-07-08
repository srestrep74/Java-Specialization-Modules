package dev.sro.workload_service.infrastructure.repository;

import dev.sro.workload_service.domain.entity.TrainingSession;
import dev.sro.workload_service.domain.repository.TrainingSessionRepository;
import dev.sro.workload_service.infrastructure.repository.jpa.TrainingSessionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TrainingSessionRepositoryImpl implements TrainingSessionRepository {
    
    private final TrainingSessionJpaRepository trainingSessionJpaRepository;
    
    @Override
    public TrainingSession save(TrainingSession trainingSession) {
        return trainingSessionJpaRepository.save(trainingSession);
    }
    
    @Override
    public List<TrainingSession> findByTrainerUsernameAndTrainingDateBetween(
        String trainerUsername, 
        LocalDate startDate, 
        LocalDate endDate
    ) {
        return trainingSessionJpaRepository.findByTrainerUsernameAndTrainingDateBetween(
            trainerUsername, startDate, endDate
        );
    }
    
    @Override
    public List<TrainingSession> findByTrainerUsername(String trainerUsername) {
        return trainingSessionJpaRepository.findByTrainerUsername(trainerUsername);
    }
    
    @Override
    public void delete(TrainingSession trainingSession) {
        trainingSessionJpaRepository.delete(trainingSession);
    }
} 