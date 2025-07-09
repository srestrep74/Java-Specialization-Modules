package dev.sro.gym_service.kafka.consumer;

import dev.sro.gym_service.client.WorkloadServiceClient;
import dev.sro.gym_service.dtos.v1.request.workload.TrainerWorkloadRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

@Service
public class WorkloadConsumer {

    private static final Logger log = LoggerFactory.getLogger(WorkloadConsumer.class);

    private final WorkloadServiceClient workloadServiceClient;

    public WorkloadConsumer(WorkloadServiceClient workloadServiceClient) {
        this.workloadServiceClient = workloadServiceClient;
    }

    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            autoCreateTopics = "false",
            dltStrategy = DltStrategy.FAIL_ON_ERROR,
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE)
    @KafkaListener(topics = "${app.kafka.retry-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleWorkloadRetry(TrainerWorkloadRequest request) {
        log.info("Retrying workload for trainer: {}", request.trainerUsername());
        try {
            workloadServiceClient.processTrainerWorkload(request);
            log.info("Successfully processed retried workload for trainer: {}", request.trainerUsername());
        } catch (Exception e) {
            log.error("Failed to process retried workload for trainer: {}. Error: {}", request.trainerUsername(), e.getMessage());
            throw e; 
        }
    }

    @DltHandler
    public void handleDlt(TrainerWorkloadRequest request, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.error("Workload message for trainer {} moved to DLT (Dead Letter Topic): {}", request.trainerUsername(), topic);
    }
} 