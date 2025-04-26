package com.sro.SpringCoreTask1.metrics;

import org.springframework.stereotype.Component;

import com.sro.SpringCoreTask1.repository.TrainerRepository;
import com.sro.SpringCoreTask1.repository.TrainingRepository;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;

import jakarta.annotation.PostConstruct;

@Component
public class TrainerTrainingMetrics {

    private final MeterRegistry meterRegistry;
    private final TrainerRepository trainerRepository;
    private final TrainingRepository trainingRepository;

    private Counter trainerSessionCounter;
    private DistributionSummary trainerTrainingDurationSummary;

    public TrainerTrainingMetrics(
            MeterRegistry meterRegistry,
            TrainerRepository trainerRepository,
            TrainingRepository trainingRepository) {
        this.meterRegistry = meterRegistry;
        this.trainerRepository = trainerRepository;
        this.trainingRepository = trainingRepository;
    }

    @PostConstruct
    public void registerMetrics() {
        registerSessionCounter();
        registerTrainingDurationSummary();
        registerActiveTrainerGauge();
        registerAverageTrainingsPerTrainerGauge();
    }

    private void registerSessionCounter() {
        trainerSessionCounter = Counter.builder("trainer.training.sessions")
                .description("Total number of training sessions conducted by trainers")
                .register(meterRegistry);
    }

    private void registerTrainingDurationSummary() {
        trainerTrainingDurationSummary = DistributionSummary.builder("trainer.training.duration")
                .baseUnit("minutes")
                .description("Distribution of training durations for trainers")
                .register(meterRegistry);
    }

    private void registerActiveTrainerGauge() {
        Gauge.builder("trainer.active.count", trainerRepository, 
                repo -> repo.countByActive(true))
            .description("Current number of active trainers")
            .register(meterRegistry);
    }

    private void registerAverageTrainingsPerTrainerGauge() {
        Gauge.builder("trainer.avg.training.count", trainerRepository, 
                repo -> {
                    long trainerCount = repo.count();
                    if (trainerCount == 0) return 0.0;
                    long totalTrainings = trainingRepository.count();
                    return (double) totalTrainings / trainerCount;
                })
            .description("Average number of trainings per trainer")
            .register(meterRegistry);
    }

    public void recordTrainerSession() {
        trainerSessionCounter.increment();
    }

    public void recordTrainerTrainingDuration(long durationMinutes) {
        trainerTrainingDurationSummary.record(durationMinutes);
    }
} 