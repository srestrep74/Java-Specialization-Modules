package dev.sro.gym_service.client;

import dev.sro.gym_service.dtos.v1.request.workload.TrainerWorkloadRequest;
import dev.sro.gym_service.dtos.v1.response.workload.TrainerWorkloadResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WorkloadServiceClientFallback implements WorkloadServiceClient {
    @Override
    public TrainerWorkloadResponse processTrainerWorkload(TrainerWorkloadRequest request) {
        log.warn("Workload service is not available. Fallback executed for trainer: {} with action: {}.",
                request.trainerUsername(), request.actionType());

        return new TrainerWorkloadResponse(
                "Workload service temporarily unavailable. The operation will be automatically retried.",
                false
        );
    }
} 