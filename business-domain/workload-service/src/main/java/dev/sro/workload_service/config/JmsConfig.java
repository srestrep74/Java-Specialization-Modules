package dev.sro.workload_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import dev.sro.workload_service.exception.messaging.JmsErrorHandler;
import jakarta.jms.ConnectionFactory;

@Configuration
@EnableJms
public class JmsConfig {
    
    @Value("${spring.activemq.broker-url}")
    private String brokerUrl;
    
    @Value("${spring.activemq.user}")
    private String username;
    
    @Value("${spring.activemq.password}")
    private String password;

    @Value("${app.jms.max-redeliveries:3}")
    private int maxRedeliveries;

    @Value("${app.jms.initial-redelivery-delay:2000}")
    private long initialRedeliveryDelay;

    @Value("${app.jms.redelivery-delay-max:5000}")
    private long redeliveryDelayMax;
    
    @Bean
    public ConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(brokerUrl);
        connectionFactory.setUserName(username);
        connectionFactory.setPassword(password);

        RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setMaximumRedeliveries(maxRedeliveries);
        redeliveryPolicy.setInitialRedeliveryDelay(initialRedeliveryDelay);
        redeliveryPolicy.setRedeliveryDelay(redeliveryDelayMax);
        redeliveryPolicy.setUseExponentialBackOff(true);
        redeliveryPolicy.setBackOffMultiplier(2.0);
        redeliveryPolicy.setMaximumRedeliveryDelay(6000);
        connectionFactory.setRedeliveryPolicy(redeliveryPolicy);
        return connectionFactory;
    }
    
    @Bean
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        converter.setObjectMapper(objectMapper);

        converter.setTypeIdMappings(java.util.Map.of(
            "dev.sro.gym_service.dtos.v1.request.workload.TrainerWorkloadRequest", 
            dev.sro.workload_service.dtos.v1.request.TrainerWorkloadRequest.class
        ));
        
        return converter;
    }
    
    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(
            ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setConcurrency("1-10");
        factory.setSessionTransacted(true);
        factory.setErrorHandler(new JmsErrorHandler());
        return factory;
    }
}
