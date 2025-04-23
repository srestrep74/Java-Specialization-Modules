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
    public void init() {
        traineeSessionCounter = Counter.builder("trainee.training.sessions")
                .description("Total trainee training sessions")
                .register(meterRegistry);

        traineeTrainingDurationSummary = DistributionSummary.builder("trainee.training.duration")
                .baseUnit("minutes")
                .description("Duration of trainee training sessions")
                .register(meterRegistry);

        Gauge.builder(
                "trainee.active.count",
                traineeRepository,
                repo -> repo.countByActive(true))
                .description("Number of active trainees")
                .register(meterRegistry);

        Gauge.builder(
                "trainee.avg.training.count",
                traineeRepository,
                repo -> {
                    long traineesCount = repo.count();
                    if (traineesCount == 0)
                        return 0;
                    long totalTrainings = trainingRepository.count();
                    return (double) totalTrainings / traineesCount;
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