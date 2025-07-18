package dev.sro.workload_service.messaging;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import dev.sro.workload_service.dtos.v1.request.TrainerWorkloadRequest;
import dev.sro.workload_service.exception.InvalidWorkloadDataException;
import dev.sro.workload_service.service.TrainerWorkloadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class WorkloadMessageConsumer {
    
    private final TrainerWorkloadService trainerWorkloadService;

    private static final String WORKLOAD_QUEUE = "workload-queue";
    private static final String WORKLOAD_DLQ = "ActiveMQ.DLQ";

    @JmsListener(destination = WORKLOAD_QUEUE)
    public void processWorkloadMessage(TrainerWorkloadRequest request) {
        try {
            trainerWorkloadService.processTrainerWorkload(request);
        } catch (InvalidWorkloadDataException | IllegalArgumentException e) {
            log.error("Invalid workload data for trainer {} : {}", request.trainerUsername(), e.getMessage());
        } catch (Exception e) {
            log.error("Error processing workload message for trainer {} : {}", request.trainerUsername(), e.getMessage(), e);
            throw e;
        }
    }

    @JmsListener(destination = WORKLOAD_DLQ)
    public void processDLQMessage(TrainerWorkloadRequest request) {
        log.warn("Message moved to DLQ: {}", request.trainerUsername());
    }
}
