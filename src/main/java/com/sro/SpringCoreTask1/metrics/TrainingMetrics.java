package com.sro.SpringCoreTask1.metrics;

import com.sro.SpringCoreTask1.repository.TrainingRepository;
import com.sro.SpringCoreTask1.repository.specification.TrainingSpecifications;
import io.micrometer.core.instrument.*;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.function.ToDoubleFunction;

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
        registerCounters();
        registerSummaries();
        registerGauges();
    }

    private void registerCounters() {
        trainingCreationCounter = Counter.builder("training.creations")
                .description("Total number of training sessions created")
                .register(meterRegistry);
    }

    private void registerSummaries() {
        trainingDurationSummary = DistributionSummary.builder("training.duration")
                .baseUnit("minutes")
                .description("Duration of training sessions in minutes")
                .register(meterRegistry);
    }

    private void registerGauges() {
        registerGauge("training.total.count", "Total number of training sessions", repo -> repo.count());

        LocalDate today = LocalDate.now();
        registerGauge(
                "training.today.count",
                "Number of training sessions scheduled for today",
                repo -> repo.findAll(
                        TrainingSpecifications.dateAfterOrEqual(today)
                                .and(TrainingSpecifications.dateBeforeOrEqual(today)))
                        .size()
        );

        LocalDate startOfWeek = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1);
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        registerGauge(
                "training.weekly.count",
                "Number of training sessions scheduled for this week",
                repo -> repo.findAll(
                        TrainingSpecifications.dateAfterOrEqual(startOfWeek)
                                .and(TrainingSpecifications.dateBeforeOrEqual(endOfWeek)))
                        .size()
        );
    }

    private void registerGauge(String name, String description, ToDoubleFunction<TrainingRepository> valueFunction) {
        Gauge.builder(name, trainingRepository, valueFunction)
                .description(description)
                .register(meterRegistry);
    }

    public void recordNewTraining() {
        trainingCreationCounter.increment();
    }

    public void recordTrainingDuration(long durationMinutes) {
        trainingDurationSummary.record(durationMinutes);
    }
}
