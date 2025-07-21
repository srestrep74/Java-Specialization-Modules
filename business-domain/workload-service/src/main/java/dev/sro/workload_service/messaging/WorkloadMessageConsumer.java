package dev.sro.workload_service.messaging;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import dev.sro.workload_service.dtos.v1.request.TrainerWorkloadRequest;
import dev.sro.workload_service.exception.InvalidWorkloadDataException;
import dev.sro.workload_service.metrics.DLQMetrics;
import dev.sro.workload_service.service.TrainerWorkloadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class WorkloadMessageConsumer {

    private final TrainerWorkloadService trainerWorkloadService;
    private final DLQMetrics dlqMetrics;

    private static final String WORKLOAD_QUEUE = "workload-queue";
    private static final String WORKLOAD_DLQ = "ActiveMQ.DLQ";

    @JmsListener(destination = WORKLOAD_QUEUE)
    public void processWorkloadMessage(TrainerWorkloadRequest request) {
        try {
            log.info("Processing workload message for trainer: {}", request.trainerUsername());

            trainerWorkloadService.processTrainerWorkload(request);

            log.info("Successfully processed workload message for trainer: {}", request.trainerUsername());
        } catch (InvalidWorkloadDataException e) {
            log.error("Invalid workload data for trainer {} : {}", request.trainerUsername(), e.getMessage());

            dlqMetrics.recordMessageByErrorType("validation_error");
            dlqMetrics.recordRetryAttempt();

            throw new RuntimeException("Invalid workload data for trainer " + request.trainerUsername(), e);
        } catch (IllegalArgumentException e) {
            log.error("Invalid workload data for trainer {} : {}", request.trainerUsername(), e.getMessage());

            dlqMetrics.recordMessageByErrorType("illegal_argument");
            dlqMetrics.recordRetryAttempt();

            throw new RuntimeException("Invalid workload data for trainer " + request.trainerUsername(), e);
        } catch (Exception e) {
            log.error("Error processing workload message for trainer {} : {}", request.trainerUsername(),
                    e.getMessage(), e);

            dlqMetrics.recordMessageByErrorType("general_error");
            dlqMetrics.recordRetryAttempt();

            throw new RuntimeException("Error processing workload message for trainer " + request.trainerUsername(), e);
        }
    }

    @JmsListener(destination = WORKLOAD_DLQ)
    public void processDLQMessage(TrainerWorkloadRequest request) {
        long startTime = System.currentTimeMillis();

        try {
            log.warn("Processing message from DLQ for trainer: {}", request.trainerUsername());

            // Here it would be the logic to process the message from DLQ : Save in DB, send
            // notification, etc.

            long processingTime = System.currentTimeMillis() - startTime;
            dlqMetrics.recordCompleteDLQFlow("dlq_processed", processingTime);

            log.info("Successfully processed DLQ message for trainer: {}", request.trainerUsername());

        } catch (Exception e) {
            log.error("Error processing DLQ message for trainer {} : {}", request.trainerUsername(), e.getMessage(), e);
            dlqMetrics.recordDLQProcessingError();
        }
    }
}
