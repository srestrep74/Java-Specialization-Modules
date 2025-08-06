package dev.sro.gym_service.cucumber.integration.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

@TestConfiguration
@Slf4j
public class ActiveMQTestcontainersConfig {

    private static GenericContainer<?> activeMQContainer;
    private static String brokerUrl;

    static {
        log.info("Initializing ActiveMQ container for integration tests...");
        
        try {
            activeMQContainer = new GenericContainer<>(DockerImageName.parse("apache/activemq-classic:5.17.6"))
                .withExposedPorts(61616, 8161)
                .withEnv("ACTIVEMQ_ADMIN_LOGIN", "admin")
                .withEnv("ACTIVEMQ_ADMIN_PASSWORD", "admin")
                .withEnv("ACTIVEMQ_OPTS", "-Djava.util.logging.config.file=logging.properties")
                .withStartupTimeout(java.time.Duration.ofSeconds(60))
                .withReuse(true)
                .withLogConsumer(outputFrame -> log.info("ActiveMQ: {}", outputFrame.getUtf8String().trim()));

            activeMQContainer.start();
            
            String host = activeMQContainer.getHost();
            int port = activeMQContainer.getMappedPort(61616);
            brokerUrl = "tcp://" + host + ":" + port;
            
            // Configure the broker URL immediately so Spring picks it up
            System.setProperty("ACTIVEMQ_BROKER_URL", brokerUrl);
            
            log.info("ActiveMQ container started successfully");
            log.info("Broker URL: {}", brokerUrl);
            log.info("Web Console: http://{}:{}", host, activeMQContainer.getMappedPort(8161));
            
        } catch (Exception e) {
            log.error("Failed to start ActiveMQ container: {}", e.getMessage());
            // Set a fallback URL for embedded broker
            brokerUrl = "vm://embedded?broker.persistent=false";
            System.setProperty("ACTIVEMQ_BROKER_URL", brokerUrl);
            log.info("Using fallback broker URL: {}", brokerUrl);
        }
    }

    @PostConstruct
    public void verifyActiveMQ() {
        log.info("Verifying ActiveMQ container is running...");
        log.info("Broker URL: {}", brokerUrl);
        if (activeMQContainer != null && activeMQContainer.isRunning()) {
            log.info("Web Console: http://{}:{}", activeMQContainer.getHost(), activeMQContainer.getMappedPort(8161));
        } else {
            log.warn("ActiveMQ container is not running");
        }
    }

    @PreDestroy
    public void stopActiveMQ() {
        if (activeMQContainer != null && activeMQContainer.isRunning()) {
            log.info("Stopping ActiveMQ container...");
            activeMQContainer.stop();
            log.info("ActiveMQ container stopped");
        }
    }

    @Bean
    @Primary
    public MessageConverter testMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setObjectMapper(objectMapper());
        
        // Configure type mapping to avoid null property issues
        converter.setTypeIdPropertyName("_type");
        
        return converter;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        
        // Configure to be more tolerant
        mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        
        return mapper;
    }

    public String getBrokerUrl() {
        return brokerUrl;
    }

    public boolean isContainerRunning() {
        try {
            return activeMQContainer != null && activeMQContainer.isRunning();
        } catch (Exception e) {
            log.warn("Error checking if ActiveMQ container is running: {}", e.getMessage());
            return false;
        }
    }

    public void stopContainer() {
        if (activeMQContainer != null && activeMQContainer.isRunning()) {
            log.info("Stopping ActiveMQ container...");
            activeMQContainer.stop();
            log.info("ActiveMQ container stopped");
        }
    }

    public void startContainer() {
        if (activeMQContainer == null || !activeMQContainer.isRunning()) {
            log.info("ActiveMQ container is not running, but it should be started in static block");
        }
    }

    public void clearQueues() {
        if (activeMQContainer != null && activeMQContainer.isRunning()) {
            try {
                // Ejecutar comando para limpiar las colas
                activeMQContainer.execInContainer("sh", "-c", 
                    "rm -rf /opt/activemq/data/activemq-data/*");
                log.info("ActiveMQ queues cleared");
            } catch (Exception e) {
                log.warn("Could not clear ActiveMQ queues: {}", e.getMessage());
                // Try alternative approach
                try {
                    activeMQContainer.execInContainer("sh", "-c", 
                        "find /opt/activemq/data -name '*.db' -delete");
                    log.info("ActiveMQ queues cleared using alternative method");
                } catch (Exception e2) {
                    log.warn("Alternative queue clearing also failed: {}", e2.getMessage());
                }
            }
        } else {
            log.info("ActiveMQ container not running, skipping queue clearing");
        }
    }
} 