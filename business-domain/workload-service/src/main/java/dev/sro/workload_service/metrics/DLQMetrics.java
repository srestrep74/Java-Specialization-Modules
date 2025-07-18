package dev.sro.workload_service.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class DLQMetrics {

    private final Counter dlqMessagesTotal;
    private final Counter dlqProcessingSuccessTotal;
    private final Counter dlqProcessingErrorsTotal;
    private final Timer dlqProcessingDuration;
    private final Counter dlqRetryAttemptsTotal;
    private final Counter dlqMessagesByErrorTypeTotal;

    public DLQMetrics(MeterRegistry meterRegistry) {
        this.dlqMessagesTotal = Counter.builder("dlq_messages_total")
                .description("Total number of messages moved to DLQ")
                .tag("service", "workload-service")
                .register(meterRegistry);

        this.dlqProcessingSuccessTotal = Counter.builder("dlq_processing_success_total")
                .description("Number of messages successfully processed from DLQ")
                .tag("service", "workload-service")
                .register(meterRegistry);

        this.dlqProcessingErrorsTotal = Counter.builder("dlq_processing_errors_total")
                .description("Number of errors while processing messages from DLQ")
                .tag("service", "workload-service")
                .register(meterRegistry);

        this.dlqProcessingDuration = Timer.builder("dlq_processing_duration_seconds")
                .description("Time taken to process messages from DLQ")
                .tag("service", "workload-service")
                .register(meterRegistry);

        this.dlqRetryAttemptsTotal = Counter.builder("dlq_retry_attempts_total")
                .description("Number of retry attempts for DLQ messages")
                .tag("service", "workload-service")
                .register(meterRegistry);

        this.dlqMessagesByErrorTypeTotal = Counter.builder("dlq_messages_by_error_type_total")
                .description("Number of messages in DLQ by error type")
                .tag("service", "workload-service")
                .register(meterRegistry);
    }

    public void recordMessageMovedToDLQ() {
        dlqMessagesTotal.increment();
    }

    public void recordDLQProcessingSuccess() {
        dlqProcessingSuccessTotal.increment();
    }

    public void recordDLQProcessingError() {
        dlqProcessingErrorsTotal.increment();
    }

    public void recordDLQProcessingTime(long timeInMillis) {
        dlqProcessingDuration.record(timeInMillis, TimeUnit.MILLISECONDS);
    }

    public void recordRetryAttempt() {
        dlqRetryAttemptsTotal.increment();
    }

    public void recordMessageByErrorType(String errorType) {
        dlqMessagesByErrorTypeTotal.increment();
    }

    public void recordCompleteDLQFlow(String errorType, long processingTimeMillis) {
        recordMessageMovedToDLQ();
        recordMessageByErrorType(errorType);
        recordDLQProcessingTime(processingTimeMillis);
        recordDLQProcessingSuccess();
    }
} 