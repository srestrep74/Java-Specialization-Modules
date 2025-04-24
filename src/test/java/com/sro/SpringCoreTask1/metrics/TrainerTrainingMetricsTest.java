package com.sro.SpringCoreTask1.metrics;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sro.SpringCoreTask1.repository.TrainerRepository;
import com.sro.SpringCoreTask1.repository.TrainingRepository;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

@ExtendWith(MockitoExtension.class)
class TrainerTrainingMetricsTest {

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TrainingRepository trainingRepository;

    private MeterRegistry meterRegistry;
    private TrainerTrainingMetrics underTest;

    @BeforeEach
    void init() {
        meterRegistry = new SimpleMeterRegistry();
        underTest = new TrainerTrainingMetrics(meterRegistry, trainerRepository, trainingRepository);
    }

    @Test
    void shouldRegisterAllMetrics() {
        underTest.registerMetrics();

        assertNotNull(meterRegistry.find("trainer.training.sessions").meter());
        assertNotNull(meterRegistry.find("trainer.training.duration").meter());
        assertNotNull(meterRegistry.find("trainer.active.count").meter());
        assertNotNull(meterRegistry.find("trainer.avg.training.count").meter());
    }

    @Test
    void shouldIncrementSessionCounter() {
        underTest.registerMetrics();
        underTest.recordTrainerSession();

        Counter counter = meterRegistry.find("trainer.training.sessions").counter();
        assertNotNull(counter);
        assertEquals(1.0, counter.count());
    }

    @Test
    void shouldRecordTrainingDuration() {
        underTest.registerMetrics();
        underTest.recordTrainerTrainingDuration(45);

        DistributionSummary summary = meterRegistry.find("trainer.training.duration").summary();
        assertNotNull(summary);
        assertEquals(1, summary.count());
        assertEquals(45.0, summary.totalAmount());
    }

    @Test
    void shouldReflectActiveTrainerCount() {
        when(trainerRepository.countByActive(true)).thenReturn(5L);

        underTest.registerMetrics();

        Gauge gauge = meterRegistry.find("trainer.active.count").gauge();
        assertNotNull(gauge);
        assertEquals(5.0, gauge.value());
        verify(trainerRepository).countByActive(true);
    }

    @Test
    void shouldCalculateAveragePerTrainer() {
        when(trainerRepository.count()).thenReturn(10L);
        when(trainingRepository.count()).thenReturn(50L);

        underTest.registerMetrics();

        Gauge gauge = meterRegistry.find("trainer.avg.training.count").gauge();
        assertNotNull(gauge);
        assertEquals(5.0, gauge.value());
        verify(trainerRepository).count();
        verify(trainingRepository).count();
    }

    @Test
    void shouldReturnZeroWhenNoTrainers() {
        when(trainerRepository.count()).thenReturn(0L);

        underTest.registerMetrics();

        Gauge gauge = meterRegistry.find("trainer.avg.training.count").gauge();
        assertNotNull(gauge);
        assertEquals(0.0, gauge.value());
        verify(trainerRepository).count();
        verify(trainingRepository, never()).count();
    }
}