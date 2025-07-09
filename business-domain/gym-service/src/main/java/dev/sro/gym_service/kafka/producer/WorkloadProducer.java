package dev.sro.gym_service.kafka.producer;

import dev.sro.gym_service.dtos.v1.request.workload.TrainerWorkloadRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class WorkloadProducer {

    private static final Logger log = LoggerFactory.getLogger(WorkloadProducer.class);

    private final KafkaTemplate<String, TrainerWorkloadRequest> kafkaTemplate;
    private final String retryTopic;

    public WorkloadProducer(KafkaTemplate<String, TrainerWorkloadRequest> kafkaTemplate,
                            @Value("${app.kafka.retry-topic}") String retryTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.retryTopic = retryTopic;
    }

    public void sendMessage(TrainerWorkloadRequest workloadRequest) {
        try {
            log.info("Sending workload to Kafka retry topic: {}. Trainer: {}", retryTopic, workloadRequest.trainerUsername());
            kafkaTemplate.send(retryTopic, workloadRequest.trainerUsername(), workloadRequest);
        } catch (Exception e) {
            log.error("Error sending message to Kafka topic {}: {}", retryTopic, e.getMessage());
        }
    }
} 