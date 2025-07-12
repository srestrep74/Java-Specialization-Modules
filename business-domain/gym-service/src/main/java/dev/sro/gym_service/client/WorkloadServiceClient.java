package dev.sro.gym_service.client;

import dev.sro.gym_service.config.WorkloadServiceClientConfig;
import dev.sro.gym_service.dtos.v1.request.workload.TrainerWorkloadRequest;
import dev.sro.gym_service.dtos.v1.response.workload.TrainerWorkloadResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "workload-service",
    contextId = "workload-service-client",
    fallback = WorkloadServiceClientFallback.class,
    configuration = WorkloadServiceClientConfig.class
)
public interface WorkloadServiceClient {
    
    @PostMapping("/api/v1/trainers/workload")
    TrainerWorkloadResponse processTrainerWorkload(@RequestBody TrainerWorkloadRequest request);
} 