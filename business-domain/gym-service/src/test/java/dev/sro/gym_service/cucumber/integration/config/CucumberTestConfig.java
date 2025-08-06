package dev.sro.gym_service.cucumber.integration.config;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import io.cucumber.spring.CucumberContextConfiguration;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration-test")
@Import({IntegrationTestConfig.class, ActiveMQTestcontainersConfig.class, TestJmsConfig.class})
public class CucumberTestConfig {
} 