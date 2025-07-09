package dev.sro.gym_service.client;

import dev.sro.gym_service.dtos.v1.request.workload.TrainerWorkloadRequest;
import dev.sro.gym_service.dtos.v1.response.workload.TrainerWorkloadResponse;
import dev.sro.gym_service.kafka.producer.WorkloadProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class WorkloadServiceClientFallbackFactory implements FallbackFactory<WorkloadServiceClient> {

    private final WorkloadProducer workloadProducer;

    @Override
    public WorkloadServiceClient create(Throwable cause) {
        return new WorkloadServiceClient() {
            @Override
            public TrainerWorkloadResponse processTrainerWorkload(TrainerWorkloadRequest request) {
                log.warn("Workload service is not available. Fallback executed for trainer: {} with action: {}. Cause: {}. Queueing for retry.",
                        request.trainerUsername(), request.actionType(), cause.getMessage());

                workloadProducer.sendMessage(new dev.sro.gym_service.dtos.v1.request.workload.TrainerWorkloadRequest(
                        request.trainerUsername(),
                        request.trainerFirstName(),
                        request.trainerLastName(),
                        request.isActive(),
                        request.trainingDate(),
                        request.trainingDuration(),
                        request.actionType()
                ));
                
                return new TrainerWorkloadResponse(
                        "Workload service temporarily unavailable. The operation has been queued and will be processed shortly.",
                        false
                );
            }
        };
    }
} 