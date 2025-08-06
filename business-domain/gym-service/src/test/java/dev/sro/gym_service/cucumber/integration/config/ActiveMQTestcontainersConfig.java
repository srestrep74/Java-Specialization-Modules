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

@TestConfiguration
public class ActiveMQTestcontainersConfig {

    private static GenericContainer<?> activeMQContainer;
    private static String brokerUrl;

    static {
        try {
            activeMQContainer = new GenericContainer<>(DockerImageName.parse("apache/activemq-classic:5.17.6"))
                    .withExposedPorts(61616, 8161)
                    .withEnv("ACTIVEMQ_ADMIN_LOGIN", "admin")
                    .withEnv("ACTIVEMQ_ADMIN_PASSWORD", "admin")
                    .withEnv("ACTIVEMQ_OPTS", "-Djava.util.logging.config.file=logging.properties")
                    .withStartupTimeout(java.time.Duration.ofSeconds(60))
                    .withReuse(true)
                    .withLogConsumer(
                            outputFrame -> System.out.println("ActiveMQ: " + outputFrame.getUtf8String().trim()));

            activeMQContainer.start();

            // Wait for the container to be fully ready
            Thread.sleep(3000);

            String host = activeMQContainer.getHost();
            int port = activeMQContainer.getMappedPort(61616);
            brokerUrl = "tcp://" + host + ":" + port;

            System.setProperty("ACTIVEMQ_BROKER_URL", brokerUrl);
            System.out.println("ActiveMQ container started with broker URL: " + brokerUrl);

        } catch (Exception e) {
            System.out.println("Failed to start ActiveMQ container: " + e.getMessage());
            throw new RuntimeException("ActiveMQ container failed to start", e);
        }
    }

    @PostConstruct
    public void verifyActiveMQ() {
        if (activeMQContainer != null && activeMQContainer.isRunning()) {
        }
    }

    @PreDestroy
    public void stopActiveMQ() {
        if (activeMQContainer != null && activeMQContainer.isRunning()) {
            activeMQContainer.stop();
        }
    }

    @Bean
    @Primary
    public MessageConverter testMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setObjectMapper(objectMapper());

        converter.setTypeIdPropertyName("_type");

        return converter;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

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
            return false;
        }
    }

    public void stopContainer() {
        if (activeMQContainer != null && activeMQContainer.isRunning()) {
            activeMQContainer.stop();
        }
    }

    public void startContainer() {
        if (activeMQContainer == null || !activeMQContainer.isRunning()) {
            try {
                if (activeMQContainer == null) {
                    activeMQContainer = new GenericContainer<>(DockerImageName.parse("apache/activemq-classic:5.17.6"))
                            .withExposedPorts(61616, 8161)
                            .withEnv("ACTIVEMQ_ADMIN_LOGIN", "admin")
                            .withEnv("ACTIVEMQ_ADMIN_PASSWORD", "admin")
                            .withStartupTimeout(java.time.Duration.ofSeconds(60))
                            .withReuse(true);
                }
                activeMQContainer.start();
                Thread.sleep(3000); // Wait for container to be ready
                System.out.println("ActiveMQ container restarted successfully");
            } catch (Exception e) {
                System.out.println("Failed to restart ActiveMQ container: " + e.getMessage());
            }
        }
    }

    public void clearQueues() {
        if (activeMQContainer != null && activeMQContainer.isRunning()) {
            try {
                activeMQContainer.execInContainer("sh", "-c",
                        "rm -rf /opt/activemq/data/activemq-data/*");
            } catch (Exception e) {
                try {
                    activeMQContainer.execInContainer("sh", "-c",
                            "find /opt/activemq/data -name '*.db' -delete");
                } catch (Exception e2) {
                }
            }
        }
    }
}