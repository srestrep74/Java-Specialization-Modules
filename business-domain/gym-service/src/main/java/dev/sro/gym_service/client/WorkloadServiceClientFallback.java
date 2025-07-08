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
        log.warn("Workload service is not available. Fallback executed for trainer: {} with action: {}", 
            request.getTrainerUsername(), request.getActionType());
        
        return TrainerWorkloadResponse.builder()
            .message("Workload service temporarily unavailable. Training operation completed but workload not updated.")
            .success(false)
            .build();
    }
} 