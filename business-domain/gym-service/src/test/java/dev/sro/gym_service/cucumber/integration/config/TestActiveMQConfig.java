package dev.sro.gym_service.cucumber.integration.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

import jakarta.jms.ConnectionFactory;

@TestConfiguration
@EnableAutoConfiguration(exclude = {ActiveMQAutoConfiguration.class})
public class TestActiveMQConfig {

    @Bean
    @Primary
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
        JmsTemplate template = new JmsTemplate(connectionFactory);
        template.setDefaultDestinationName("workload-queue");
        template.setReceiveTimeout(5000);
        return template;
    }

    @Bean
    @Primary
    public ConnectionFactory connectionFactory() {
        // Usar el connection factory configurado por el contenedor
        return new CachingConnectionFactory();
    }
}
