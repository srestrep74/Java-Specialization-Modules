package dev.sro.gym_service.cucumber.integration.config;

import org.apache.activemq.broker.BrokerService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

@TestConfiguration
@Slf4j
public class ActiveMQTestConfig {

    private BrokerService broker;

    @PostConstruct
    public void startBroker() throws Exception {
        broker = new BrokerService();
        broker.setBrokerName("test-broker");
        broker.setUseJmx(false);
        broker.setPersistent(false);
        broker.setDataDirectory("target/activemq-data");
        broker.addConnector("vm://test-broker");
        
        // Configure for quick shutdown
        broker.setShutdownHooks(null);
        broker.setSystemExitOnShutdown(false);
        broker.setUseShutdownHook(false);
        
        broker.start();
        log.info("ActiveMQ test broker started");
    }

    @PreDestroy
    public void stopBroker() throws Exception {
        if (broker != null) {
            try {
                broker.stop();
                broker.waitUntilStopped();
                log.info("ActiveMQ test broker stopped");
            } catch (Exception e) {
                log.warn("Error stopping ActiveMQ broker: {}", e.getMessage());
            }
        }
    }

    @Bean
    @Primary
    public MessageConverter messageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setObjectMapper(objectMapper());
        return converter;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
} 