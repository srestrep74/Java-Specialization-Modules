package dev.sro.gym_service.cucumber.integration.config;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import io.cucumber.spring.CucumberContextConfiguration;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration-test")
@Import({IntegrationTestConfig.class, ActiveMQTestcontainersConfig.class, TestJmsConfig.class})
@TestPropertySource(properties = {
    "spring.config.import=classpath:application-integration-test.yml",
    "logging.level.dev.sro.gym_service=DEBUG",
    "logging.level.org.springframework.jms=DEBUG",
    "logging.level.org.apache.activemq=DEBUG",
    "spring.task.scheduling.pool.size=1",
    "spring.task.scheduling.thread-name-prefix=test-scheduler-",
    "spring.main.allow-bean-definition-overriding=true",
    "spring.jms.listener.auto-startup=false",
    "spring.jms.listener.acknowledge-mode=auto",
    "spring.jms.listener.concurrency=1",
    "activemq.broker-url=${ACTIVEMQ_BROKER_URL:tcp://localhost:61616}",
    "spring.activemq.embedded.enabled=false",
    "spring.jms.template.receive-timeout=5000",
    "spring.jms.template.send-timeout=5000"
})
public class CucumberTestConfig {
} 