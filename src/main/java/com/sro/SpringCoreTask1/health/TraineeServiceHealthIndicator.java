package com.sro.SpringCoreTask1.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

import com.sro.SpringCoreTask1.repository.TraineeRepository;

@Component
public class TraineeServiceHealthIndicator implements HealthIndicator {

    private final TraineeRepository traineeRepository;

    public TraineeServiceHealthIndicator(TraineeRepository traineeRepository) {
        this.traineeRepository = traineeRepository;
    }

    @Override
    public Health health() {
        try {
            long count = traineeRepository.count();
            Health.Builder builder = Health.up()
                    .withDetail("total_trainees", count)
                    .withDetail("description", "Trainee service health check");

            if (count == 0) {
                builder.status(Status.UNKNOWN)
                        .withDetail("warning", "No trainees registered");
            }

            return builder.build();
        } catch (Exception e) {
            return Health.down()
                    .withException(e)
                    .withDetail("error", "Trainee service is not available")
                    .build();
        }
    }
}
