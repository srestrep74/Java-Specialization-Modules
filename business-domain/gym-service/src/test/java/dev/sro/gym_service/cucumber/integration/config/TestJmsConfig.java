package dev.sro.gym_service.cucumber.integration.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;

import jakarta.jms.ConnectionFactory;

@TestConfiguration
public class TestJmsConfig {

    @Autowired
    private ActiveMQFailureSimulator activeMQFailureSimulator;

    @Bean
    @Primary
    public JmsTemplate testJmsTemplate(ConnectionFactory connectionFactory, MessageConverter testMessageConverter) {
        JmsTemplate template = new JmsTemplate(connectionFactory) {
            @Override
            public void convertAndSend(String destinationName, Object message) {
                if (activeMQFailureSimulator.isInFailureMode()) {
                    throw new RuntimeException("ActiveMQ is in failure mode - simulating connection failure");
                }
                super.convertAndSend(destinationName, message);
            }
        };
        template.setMessageConverter(testMessageConverter);
        template.setDefaultDestinationName("workload-queue");
        // Set timeout to prevent hanging in tests
        template.setReceiveTimeout(1000); // 1 second timeout
        return template;
    }
} 