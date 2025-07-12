package dev.sro.gym_service.client;

import dev.sro.gym_service.config.WorkloadRelayClientConfig;
import dev.sro.gym_service.dtos.v1.request.workload.TrainerWorkloadRequest;
import dev.sro.gym_service.dtos.v1.response.workload.TrainerWorkloadResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * A dedicated Feign client for the WorkloadRelayService.
 * This client does NOT have a fallback. If a call fails, it will throw an
 * exception, which is the desired behavior for the relay service to catch
 * and decide to retry the operation later.
 */
@FeignClient(
    name = "workload-service",
    contextId = "workload-relay-client",
    configuration = WorkloadRelayClientConfig.class
)
public interface WorkloadRelayClient {

    @PostMapping("/api/v1/trainers/workload")
    TrainerWorkloadResponse processTrainerWorkload(@RequestBody TrainerWorkloadRequest request);
} 