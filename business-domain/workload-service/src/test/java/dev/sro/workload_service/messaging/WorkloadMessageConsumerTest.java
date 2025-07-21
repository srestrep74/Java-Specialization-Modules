package dev.sro.workload_service.messaging;

import dev.sro.workload_service.dtos.v1.request.TrainerWorkloadRequest;
import dev.sro.workload_service.entity.enums.ActionType;
import dev.sro.workload_service.exception.InvalidWorkloadDataException;
import dev.sro.workload_service.metrics.DLQMetrics;
import dev.sro.workload_service.service.TrainerWorkloadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkloadMessageConsumerTest {

    @Mock
    private TrainerWorkloadService trainerWorkloadService;

    @Mock
    private DLQMetrics dlqMetrics;

    private WorkloadMessageConsumer consumer;

    private TrainerWorkloadRequest validRequest;

    @BeforeEach
    void setUp() {
        consumer = new WorkloadMessageConsumer(trainerWorkloadService, dlqMetrics);

        validRequest = new TrainerWorkloadRequest(
                "test.trainer",
                "John",
                "Doe",
                true,
                LocalDate.of(2024, 1, 15),
                60,
                ActionType.ADD);
    }

    @Test
    void shouldProcessWorkloadMessageSuccessfully() {
        doNothing().when(trainerWorkloadService).processTrainerWorkload(validRequest);

        assertDoesNotThrow(() -> consumer.processWorkloadMessage(validRequest));

        verify(trainerWorkloadService, times(1)).processTrainerWorkload(validRequest);
        verifyNoInteractions(dlqMetrics);
    }

    @Test
    void shouldHandleInvalidWorkloadDataException() {
        String errorMessage = "Invalid trainer data";
        doThrow(new InvalidWorkloadDataException(errorMessage))
                .when(trainerWorkloadService).processTrainerWorkload(validRequest);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> consumer.processWorkloadMessage(validRequest));

        assertTrue(exception.getMessage().contains("Invalid workload data for trainer test.trainer"));
        assertTrue(exception.getCause() instanceof InvalidWorkloadDataException);

        verify(dlqMetrics, times(1)).recordMessageByErrorType("validation_error");
        verify(dlqMetrics, times(1)).recordRetryAttempt();
    }

    @Test
    void shouldHandleIllegalArgumentException() {
        String errorMessage = "Invalid argument provided";
        doThrow(new IllegalArgumentException(errorMessage))
                .when(trainerWorkloadService).processTrainerWorkload(validRequest);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> consumer.processWorkloadMessage(validRequest));

        assertTrue(exception.getMessage().contains("Invalid workload data for trainer test.trainer"));
        assertTrue(exception.getCause() instanceof IllegalArgumentException);

        verify(dlqMetrics, times(1)).recordMessageByErrorType("illegal_argument");
        verify(dlqMetrics, times(1)).recordRetryAttempt();
    }

    @Test
    void shouldHandleGeneralException() {
        String errorMessage = "Database connection failed";
        doThrow(new RuntimeException(errorMessage))
                .when(trainerWorkloadService).processTrainerWorkload(validRequest);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> consumer.processWorkloadMessage(validRequest));

        assertTrue(exception.getMessage().contains("Error processing workload message for trainer test.trainer"));
        assertTrue(exception.getCause() instanceof RuntimeException);

        verify(dlqMetrics, times(1)).recordMessageByErrorType("general_error");
        verify(dlqMetrics, times(1)).recordRetryAttempt();
    }

    @Test
    void shouldProcessDLQMessageSuccessfully() {
        assertDoesNotThrow(() -> consumer.processDLQMessage(validRequest));

        verify(dlqMetrics, times(1)).recordCompleteDLQFlow(eq("dlq_processed"), anyLong());
        verifyNoInteractions(trainerWorkloadService);
    }

    @Test
    void shouldHandleDLQProcessingError() {
        doThrow(new RuntimeException("DLQ processing failed"))
                .when(dlqMetrics).recordCompleteDLQFlow(eq("dlq_processed"), anyLong());

        assertDoesNotThrow(() -> consumer.processDLQMessage(validRequest));

        verify(dlqMetrics, times(1)).recordDLQProcessingError();
        verifyNoInteractions(trainerWorkloadService);
    }

    @Test
    void shouldHandleNullRequestInWorkloadMessage() {
        TrainerWorkloadRequest nullRequest = null;

        assertThrows(NullPointerException.class,
                () -> consumer.processWorkloadMessage(nullRequest));

        verifyNoInteractions(trainerWorkloadService, dlqMetrics);
    }

    @Test
    void shouldHandleNullRequestInDLQMessage() {
        TrainerWorkloadRequest nullRequest = null;

        assertThrows(NullPointerException.class,
                () -> consumer.processDLQMessage(nullRequest));

        verifyNoInteractions(trainerWorkloadService, dlqMetrics);
    }

    @Test
    void shouldProcessDifferentActionTypes() {
        TrainerWorkloadRequest deleteRequest = new TrainerWorkloadRequest(
                "test.trainer",
                "John",
                "Doe",
                true,
                LocalDate.of(2024, 1, 15),
                60,
                ActionType.DELETE);

        doNothing().when(trainerWorkloadService).processTrainerWorkload(deleteRequest);

        assertDoesNotThrow(() -> consumer.processWorkloadMessage(deleteRequest));

        verify(trainerWorkloadService, times(1)).processTrainerWorkload(deleteRequest);
    }

    @Test
    void shouldProcessInactiveTrainer() {
        TrainerWorkloadRequest inactiveRequest = new TrainerWorkloadRequest(
                "inactive.trainer",
                "Jane",
                "Smith",
                false,
                LocalDate.of(2024, 1, 15),
                30,
                ActionType.ADD);

        doNothing().when(trainerWorkloadService).processTrainerWorkload(inactiveRequest);

        assertDoesNotThrow(() -> consumer.processWorkloadMessage(inactiveRequest));

        verify(trainerWorkloadService, times(1)).processTrainerWorkload(inactiveRequest);
    }
}