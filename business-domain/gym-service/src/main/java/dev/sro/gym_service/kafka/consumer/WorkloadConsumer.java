package dev.sro.gym_service.kafka.consumer;

import dev.sro.gym_service.client.WorkloadServiceClient;
import dev.sro.gym_service.dtos.v1.request.workload.TrainerWorkloadRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class WorkloadConsumer {

    private static final Logger log = LoggerFactory.getLogger(WorkloadConsumer.class);

    private final WorkloadServiceClient workloadServiceClient;

    public WorkloadConsumer(WorkloadServiceClient workloadServiceClient) {
        this.workloadServiceClient = workloadServiceClient;
    }

    @KafkaListener(topics = "${app.kafka.retry-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleWorkloadRetry(TrainerWorkloadRequest request, Acknowledgment ack) {
        log.info("Processing workload message for trainer: {}", request.trainerUsername());
        try {
            workloadServiceClient.processTrainerWorkload(request);
            log.info("Successfully processed workload for trainer: {}", request.trainerUsername());
            ack.acknowledge(); // Only acknowledge if successful
        } catch (Exception e) {
            log.error("Failed to process workload for trainer: {}. Error: {}. Message will NOT be acknowledged and will be retried.", 
                request.trainerUsername(), e.getMessage());
            // Don't acknowledge - message will be retried
        }
    }
} 