package dev.sro.gym_service.client;

import dev.sro.gym_service.config.WorkloadRelayClientConfig;
import dev.sro.gym_service.dtos.v1.request.workload.TrainerWorkloadRequest;
import dev.sro.gym_service.dtos.v1.response.workload.TrainerWorkloadResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "workload-service", contextId = "workload-relay-client", configuration = WorkloadRelayClientConfig.class)
public interface WorkloadRelayClient {

    @PostMapping("/api/v1/workloads")
    TrainerWorkloadResponse processTrainerWorkload(@RequestBody TrainerWorkloadRequest request);
}