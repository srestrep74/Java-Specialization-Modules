package dev.sro.gym_service.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${app.kafka.retry-topic}")
    private String retryTopic;

    @Bean
    public NewTopic workloadRetryTopic() {
        return TopicBuilder.name(retryTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }
} 