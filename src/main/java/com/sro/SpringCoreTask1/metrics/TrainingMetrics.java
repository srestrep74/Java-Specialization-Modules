package com.sro.SpringCoreTask1.metrics;

import org.springframework.stereotype.Component;

import com.sro.SpringCoreTask1.repository.TrainingRepository;
import com.sro.SpringCoreTask1.repository.specification.TrainingSpecifications;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;

import java.time.LocalDate;

@Component
public class TrainingMetrics {

    private final MeterRegistry meterRegistry;
    private final TrainingRepository trainingRepository;

    private Counter trainingCreationCounter;
    private DistributionSummary trainingDurationSummary;

    public TrainingMetrics(MeterRegistry meterRegistry, TrainingRepository trainingRepository) {
        this.meterRegistry = meterRegistry;
        this.trainingRepository = trainingRepository;
    }

    @PostConstruct
    public void init() {
        trainingCreationCounter = Counter.builder("training.creations")
                .description("Total training sessions created")
                .register(meterRegistry);

        trainingDurationSummary = DistributionSummary.builder("training.duration")
                .baseUnit("minutes")
                .description("Duration of training sessions")
                .register(meterRegistry);

        Gauge.builder(
                "training.total.count",
                trainingRepository,
                repo -> repo.count())
                .description("Total number of training sessions in the system")
                .register(meterRegistry);

        LocalDate today = LocalDate.now();
        Gauge.builder(
                "training.today.count",
                trainingRepository,
                repo -> repo.findAll(TrainingSpecifications.dateAfterOrEqual(today)
                        .and(TrainingSpecifications.dateBeforeOrEqual(today))).size())
                .description("Number of trainings scheduled for today")
                .register(meterRegistry);

        LocalDate startOfWeek = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1);
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        Gauge.builder(
                "training.weekly.count",
                trainingRepository,
                repo -> repo.findAll(
                        TrainingSpecifications.dateAfterOrEqual(startOfWeek)
                                .and(TrainingSpecifications.dateBeforeOrEqual(endOfWeek)))
                        .size())
                .description("Number of trainings scheduled for this week")
                .register(meterRegistry);
    }

    public void recordNewTraining() {
        trainingCreationCounter.increment();
    }

    public void recordTrainingDuration(long durationMinutes) {
        trainingDurationSummary.record(durationMinutes);
    }
}