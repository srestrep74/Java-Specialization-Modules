package com.sro.SpringCoreTask1.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import com.sro.SpringCoreTask1.repository.TraineeRepository;
import com.sro.SpringCoreTask1.repository.TrainerRepository;
import com.sro.SpringCoreTask1.repository.TrainingTypeRepository;

@Component
public class DataInitializationHealthIndicator implements HealthIndicator {

    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;

    public DataInitializationHealthIndicator(
            TrainingTypeRepository trainingTypeRepository,
            TrainerRepository trainerRepository,
            TraineeRepository traineeRepository) {
        this.trainingTypeRepository = trainingTypeRepository;
        this.trainerRepository = trainerRepository;
        this.traineeRepository = traineeRepository;
    }

    @Override
    public Health health() {
        try {
            long trainingTypeCount = trainingTypeRepository.count();
            long trainerCount = trainerRepository.count();
            long traineeCount = traineeRepository.count();
            
            boolean dataExists = trainingTypeCount > 0 && trainerCount > 0 && traineeCount > 0;
            
            if (dataExists) {
                return Health.up()
                        .withDetail("component", "DataInitialization")
                        .withDetail("trainingTypes", trainingTypeCount)
                        .withDetail("trainers", trainerCount)
                        .withDetail("trainees", traineeCount)
                        .withDetail("message", "Data initialization is complete")
                        .build();
            } else {
                return Health.down()
                        .withDetail("component", "DataInitialization")
                        .withDetail("trainingTypes", trainingTypeCount)
                        .withDetail("trainers", trainerCount)
                        .withDetail("trainees", traineeCount)
                        .withDetail("message", "Data initialization is incomplete")
                        .build();
            }
        } catch (Exception e) {
            return Health.down()
                    .withDetail("component", "DataInitialization")
                    .withDetail("message", "Error checking data initialization status")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}