package com.sro.SpringCoreTask1.metrics;

import org.springframework.stereotype.Component;

import com.sro.SpringCoreTask1.repository.TraineeRepository;
import com.sro.SpringCoreTask1.repository.TrainingRepository;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;

import jakarta.annotation.PostConstruct;

@Component
public class TraineeTrainingMetrics {

    private final MeterRegistry meterRegistry;
    private final TraineeRepository traineeRepository;
    private final TrainingRepository trainingRepository;

    private Counter traineeSessionCounter;
    private DistributionSummary traineeTrainingDurationSummary;

    public TraineeTrainingMetrics(
            MeterRegistry meterRegistry,
            TraineeRepository traineeRepository,
            TrainingRepository trainingRepository) {
        this.meterRegistry = meterRegistry;
        this.traineeRepository = traineeRepository;
        this.trainingRepository = trainingRepository;
    }

    @PostConstruct
    public void registerMetrics() {
        registerSessionCounter();
        registerTrainingDurationSummary();
        registerActiveTraineeGauge();
        registerAverageTrainingsPerTraineeGauge();
    }

    private void registerSessionCounter() {
        traineeSessionCounter = Counter.builder("trainee.training.sessions")
                .description("Total number of training sessions attended by trainees")
                .register(meterRegistry);
    }

    private void registerTrainingDurationSummary() {
        traineeTrainingDurationSummary = DistributionSummary.builder("trainee.training.duration")
                .baseUnit("minutes")
                .description("Distribution of training durations for trainees")
                .register(meterRegistry);
    }

    private void registerActiveTraineeGauge() {
        Gauge.builder("trainee.active.count", traineeRepository, 
                repo -> repo.countByActive(true))
            .description("Current number of active trainees")
            .register(meterRegistry);
    }

    private void registerAverageTrainingsPerTraineeGauge() {
        Gauge.builder("trainee.avg.training.count", traineeRepository, 
                repo -> {
                    long traineeCount = repo.count();
                    if (traineeCount == 0) return 0.0;
                    long totalTrainings = trainingRepository.count();
                    return (double) totalTrainings / traineeCount;
                })
            .description("Average number of trainings per trainee")
            .register(meterRegistry);
    }

    public void recordTraineeSession() {
        traineeSessionCounter.increment();
    }

    public void recordTraineeTrainingDuration(long durationMinutes) {
        traineeTrainingDurationSummary.record(durationMinutes);
    }
}
