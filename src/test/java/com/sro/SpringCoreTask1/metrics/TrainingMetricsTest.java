package com.sro.SpringCoreTask1.metrics;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import com.sro.SpringCoreTask1.entity.Training;
import com.sro.SpringCoreTask1.repository.TrainingRepository;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

@ExtendWith(MockitoExtension.class)
class TrainingMetricsTest {

    @Mock
    private TrainingRepository trainingRepository;

    private MeterRegistry meterRegistry;
    private TrainingMetrics underTest;

    @BeforeEach
    void init() {
        meterRegistry = new SimpleMeterRegistry();
        underTest = new TrainingMetrics(meterRegistry, trainingRepository);
    }

    @Test
    void shouldRegisterAllMetricsOnInit() {
        underTest.init();

        assertNotNull(meterRegistry.find("training.creations").meter());
        assertNotNull(meterRegistry.find("training.duration").meter());
        assertNotNull(meterRegistry.find("training.total.count").meter());
        assertNotNull(meterRegistry.find("training.today.count").meter());
        assertNotNull(meterRegistry.find("training.weekly.count").meter());
    }

    @Test
    void shouldIncrementTrainingCreationsCounter() {
        underTest.init();
        underTest.recordNewTraining();

        Counter counter = meterRegistry.find("training.creations").counter();
        assertNotNull(counter);
        assertEquals(1.0, counter.count());
    }

    @Test
    void shouldRecordTrainingDuration() {
        underTest.init();
        underTest.recordTrainingDuration(60);

        DistributionSummary summary = meterRegistry.find("training.duration").summary();
        assertNotNull(summary);
        assertEquals(1, summary.count());
        assertEquals(60.0, summary.totalAmount());
    }

    @Test
    void shouldReflectTotalTrainingCountFromRepository() {
        when(trainingRepository.count()).thenReturn(10L);

        underTest.init();

        Gauge gauge = meterRegistry.find("training.total.count").gauge();
        assertNotNull(gauge);
        assertEquals(10.0, gauge.value());
    }

    @Test
    void shouldReflectTrainingsScheduledToday() {
        List<Training> trainings = List.of(mock(Training.class), mock(Training.class));
        when(trainingRepository.findAll(any(Specification.class))).thenReturn(trainings);

        underTest.init();

        Gauge gauge = meterRegistry.find("training.today.count").gauge();
        assertNotNull(gauge);
        assertEquals(2.0, gauge.value());
        verify(trainingRepository).findAll(any(Specification.class));
    }

    @Test
    void shouldReflectWeeklyTrainings() {
        List<Training> trainings = List.of(
                mock(Training.class),
                mock(Training.class),
                mock(Training.class));
        when(trainingRepository.findAll(any(Specification.class))).thenReturn(trainings);

        underTest.init();

        Gauge gauge = meterRegistry.find("training.weekly.count").gauge();
        assertNotNull(gauge);
        assertEquals(3.0, gauge.value());
        verify(trainingRepository).findAll(any(Specification.class));
    }
}