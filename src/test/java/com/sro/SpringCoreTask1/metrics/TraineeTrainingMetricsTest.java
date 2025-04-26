package com.sro.SpringCoreTask1.metrics;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sro.SpringCoreTask1.repository.TraineeRepository;
import com.sro.SpringCoreTask1.repository.TrainingRepository;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

@ExtendWith(MockitoExtension.class)
class TraineeTrainingMetricsTest {

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainingRepository trainingRepository;

    private MeterRegistry meterRegistry;
    private TraineeTrainingMetrics underTest;

    @BeforeEach
    void init() {
        meterRegistry = new SimpleMeterRegistry();
        underTest = new TraineeTrainingMetrics(meterRegistry, traineeRepository, trainingRepository);
    }

    @Test
    void shouldRegisterAllMetrics() {
        underTest.registerMetrics();

        assertNotNull(meterRegistry.find("trainee.training.sessions").meter());
        assertNotNull(meterRegistry.find("trainee.training.duration").meter());
        assertNotNull(meterRegistry.find("trainee.active.count").meter());
        assertNotNull(meterRegistry.find("trainee.avg.training.count").meter());
    }

    @Test
    void shouldIncrementSessionCounter() {
        underTest.registerMetrics();
        underTest.recordTraineeSession();

        Counter counter = meterRegistry.find("trainee.training.sessions").counter();
        assertNotNull(counter);
        assertEquals(1.0, counter.count());
    }

    @Test
    void shouldRecordTrainingDuration() {
        underTest.registerMetrics();
        underTest.recordTraineeTrainingDuration(90);

        DistributionSummary summary = meterRegistry.find("trainee.training.duration").summary();
        assertNotNull(summary);
        assertEquals(1, summary.count());
        assertEquals(90.0, summary.totalAmount());
    }

    @Test
    void shouldReflectActiveTraineeCount() {
        when(traineeRepository.countByActive(true)).thenReturn(8L);

        underTest.registerMetrics();

        Gauge gauge = meterRegistry.find("trainee.active.count").gauge();
        assertNotNull(gauge);
        assertEquals(8.0, gauge.value());
        verify(traineeRepository).countByActive(true);
    }

    @Test
    void shouldCalculateAveragePerTrainee() {
        when(traineeRepository.count()).thenReturn(20L);
        when(trainingRepository.count()).thenReturn(100L);

        underTest.registerMetrics();

        Gauge gauge = meterRegistry.find("trainee.avg.training.count").gauge();
        assertNotNull(gauge);
        assertEquals(5.0, gauge.value());
        verify(traineeRepository).count();
        verify(trainingRepository).count();
    }

    @Test
    void shouldReturnZeroWhenNoTrainees() {
        when(traineeRepository.count()).thenReturn(0L);

        underTest.registerMetrics();

        Gauge gauge = meterRegistry.find("trainee.avg.training.count").gauge();
        assertNotNull(gauge);
        assertEquals(0.0, gauge.value());
        verify(traineeRepository).count();
        verify(trainingRepository, never()).count();
    }
}