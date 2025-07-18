package dev.sro.workload_service.messaging;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import dev.sro.workload_service.dtos.v1.request.TrainerWorkloadRequest;
import dev.sro.workload_service.service.TrainerWorkloadService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WorkloadMessageConsumer {
    
    private final TrainerWorkloadService trainerWorkloadService;

    private static final String WORKLOAD_QUEUE = "workload-queue";

    @JmsListener(destination = WORKLOAD_QUEUE)
    public void processWorkloadMessage(TrainerWorkloadRequest request) {
        try {
            trainerWorkloadService.processTrainerWorkload(request);
        } catch (Exception e) {
            throw e;
        }
    }
}
