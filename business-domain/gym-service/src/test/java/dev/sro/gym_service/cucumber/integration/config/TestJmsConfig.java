package dev.sro.gym_service.cucumber.integration.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;

import jakarta.jms.ConnectionFactory;

@TestConfiguration
public class TestJmsConfig {

    @Bean
    @Primary
    public JmsTemplate testJmsTemplate(ConnectionFactory connectionFactory, MessageConverter testMessageConverter) {
        JmsTemplate template = new JmsTemplate(connectionFactory);
        template.setMessageConverter(testMessageConverter);
        template.setDefaultDestinationName("workload-queue");
        return template;
    }
} 