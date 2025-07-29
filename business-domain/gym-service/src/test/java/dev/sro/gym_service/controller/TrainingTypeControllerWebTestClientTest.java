package dev.sro.gym_service.controller;

import dev.sro.gym_service.config.properties.JwtProperties;
import dev.sro.gym_service.dtos.v1.request.auth.LoginRequest;
import dev.sro.gym_service.dtos.v1.request.trainee.RegisterTraineeRequest;
import dev.sro.gym_service.dtos.v1.request.trainingType.TrainingTypeRequestDTO;
import dev.sro.gym_service.dtos.v1.response.auth.LoginResponse;
import dev.sro.gym_service.dtos.v1.response.trainee.RegisterTraineeResponse;
import dev.sro.gym_service.dtos.v1.response.trainingType.TrainingTypeResponse;
import dev.sro.gym_service.service.TraineeService;
import dev.sro.gym_service.service.TrainingTypeService;
import dev.sro.gym_service.service.WorkloadNotificationService;
import dev.sro.gym_service.service.impl.auth.LoginAttemptService;
import dev.sro.gym_service.service.impl.InMemoryTokenStorageServiceImpl;
import dev.sro.gym_service.util.response.ApiStandardResponse;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import static org.mockito.Mockito.mock;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.profiles.active=test")
class TrainingTypeControllerWebTestClientTest {

    @TestConfiguration
    static class ControllerTestConfig {
        @Bean
        @Primary
        public WorkloadNotificationService workloadNotificationService() {
            return mock(WorkloadNotificationService.class);
        }

        @Bean
        @Primary
        public JwtProperties jwtProperties() {
            return new JwtProperties(
                    "5JI1p09GOcOlK9z8A/QBiLM7P+ZzS7DBvzIKM5G6Md2jYMkSvCbdQR13nPhJGwKkXZvRK9lNCPUXX/bSA44qzw==",
                    120000L,
                    604800000L,
                    new JwtProperties.BlacklistProperties("blacklisted_token:", "60000"),
                    new JwtProperties.RefreshProperties("user:refresh_tokens:", 30));
        }

        @Bean
        @Primary
        public InMemoryTokenStorageServiceImpl tokenStorageService(JwtProperties jwtProperties) {
            return new InMemoryTokenStorageServiceImpl(jwtProperties);
        }

        @Bean
        @Primary
        public LoginAttemptService loginAttemptService() {
            return mock(LoginAttemptService.class);
        }
    }

    private static final String BASE_URL = "/api/v1/training-types";
    private static RegisterTraineeResponse trainee;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeAll
    static void setupAll(
            @Autowired TraineeService traineeService,
            @Autowired TrainingTypeService trainingTypeService) {
        RegisterTraineeRequest traineeRequest = new RegisterTraineeRequest(
                "Test", "Trainee2", LocalDate.of(1990, 1, 1), "Test Address");
        trainee = traineeService.save(traineeRequest);

        TrainingTypeRequestDTO trainingTypeRequest = new TrainingTypeRequestDTO(
                "Test Training Type");
        trainingTypeService.save(trainingTypeRequest);
    }

    @Test
    void shouldReturnListOfTrainingTypes() {
        LoginResponse loginResponse = authenticate(trainee.username(), trainee.plainPassword());

        assertNotNull(loginResponse);
        assertTrue(loginResponse.success());
        assertNotNull(loginResponse.token());

        webTestClient.get()
                .uri(BASE_URL)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + loginResponse.token())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TrainingTypeResponse.class)
                .value(types -> {
                    assertNotNull(types);
                    assertFalse(types.isEmpty());
                });
    }

    private LoginResponse authenticate(String username, String password) {
        LoginRequest loginRequest = new LoginRequest(username, password);
        ParameterizedTypeReference<ApiStandardResponse<LoginResponse>> responseType = new ParameterizedTypeReference<ApiStandardResponse<LoginResponse>>() {
        };

        ApiStandardResponse<LoginResponse> response = webTestClient.post()
                .uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(responseType)
                .returnResult()
                .getResponseBody();
        return response != null ? response.data() : null;
    }
}