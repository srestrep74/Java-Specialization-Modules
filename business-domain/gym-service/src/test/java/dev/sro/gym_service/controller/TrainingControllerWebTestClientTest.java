package dev.sro.gym_service.controller;

import dev.sro.gym_service.dtos.v1.request.training.CreateTrainingRequest;
import dev.sro.gym_service.dtos.v1.request.training.DeleteTrainingRequest;
import dev.sro.gym_service.dtos.v1.request.trainer.RegisterTrainerRequest;
import dev.sro.gym_service.dtos.v1.request.auth.LoginRequest;
import dev.sro.gym_service.dtos.v1.request.trainee.RegisterTraineeRequest;
import dev.sro.gym_service.dtos.v1.request.trainingType.TrainingTypeRequestDTO;
import dev.sro.gym_service.dtos.v1.response.auth.LoginResponse;
import dev.sro.gym_service.dtos.v1.response.trainee.RegisterTraineeResponse;
import dev.sro.gym_service.service.TraineeService;
import dev.sro.gym_service.service.TrainerService;
import dev.sro.gym_service.service.TrainingTypeService;
import dev.sro.gym_service.service.WorkloadNotificationService;
import dev.sro.gym_service.util.response.ApiStandardResponse;
import dev.sro.gym_service.service.impl.auth.LoginAttemptService;
import dev.sro.gym_service.service.impl.InMemoryTokenStorageServiceImpl;


import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import java.time.LocalDate;

import static org.mockito.Mockito.mock;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.profiles.active=test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TrainingControllerWebTestClientTest {

    @TestConfiguration
    static class ControllerTestConfig {
        @Bean
        @Primary
        public WorkloadNotificationService workloadNotificationService() {
            return mock(WorkloadNotificationService.class);
        }

        @Bean
        @Primary
        public InMemoryTokenStorageServiceImpl tokenStorageService() {
            return mock(InMemoryTokenStorageServiceImpl.class);
        }

        @Bean
        @Primary
        public LoginAttemptService loginAttemptService() {
            return mock(LoginAttemptService.class);
        }
    }

    private static final String BASE_URL = "/api/v1/trainings";
    private static RegisterTraineeResponse trainee;
    private static String traineeUsername;
    private static String trainerUsername;
    private static String accessToken;
    private static LocalDate trainingDate;

    @Autowired
    private WebTestClient webTestClient;


    @BeforeAll
    static void setupAll(
            @Autowired TrainingTypeService trainingTypeService,
            @Autowired TraineeService traineeService,
            @Autowired TrainerService trainerService) {
        
        TrainingTypeRequestDTO typeRequest = new TrainingTypeRequestDTO("Zumba");
        trainingTypeService.save(typeRequest);

        RegisterTraineeRequest traineeRequest = new RegisterTraineeRequest(
                "Test", "Trainee", LocalDate.of(1990, 1, 1), "Test Address");
        trainee = traineeService.save(traineeRequest);
        traineeUsername = trainee.username();

        RegisterTrainerRequest trainerRequest = new RegisterTrainerRequest(
                "Test", "Trainer", 1L);
        trainerUsername = trainerService.save(trainerRequest).username();
        
        trainingDate = LocalDate.now();
    }

    @Test
    @Order(1)
    void createTraining_ShouldReturnSuccess() {
        CreateTrainingRequest request = new CreateTrainingRequest(
                traineeUsername,
                trainerUsername,
                "Zumba",
                trainingDate,
                60
        );

        LoginResponse loginResponse = authenticate(trainee.username(), trainee.plainPassword());
        accessToken = loginResponse.token();

        webTestClient.post()
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @Order(2)
    void createTraining_WithInvalidData_ShouldReturnBadRequest() {
        CreateTrainingRequest invalidRequest = new CreateTrainingRequest(
                "", "", "", null, 0);

        webTestClient.post()
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @Order(3)
    void createTraining_WithNonExistentUsers_ShouldReturnNotFound() {
        CreateTrainingRequest request = new CreateTrainingRequest(
                "nonexistent.trainee",
                "nonexistent.trainer",
                "Invalid Training",
                LocalDate.now(),
                30
        );

        webTestClient.post()
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken)
                .bodyValue(request)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(4)
    void deleteTraining_ShouldReturnSuccess() {
        DeleteTrainingRequest request = new DeleteTrainingRequest(
                traineeUsername,
                trainerUsername,
                trainingDate
        );

        webTestClient.method(org.springframework.http.HttpMethod.DELETE)
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @Order(5)
    void deleteTraining_WithInvalidData_ShouldReturnBadRequest() {
        DeleteTrainingRequest invalidRequest = new DeleteTrainingRequest(
                "", "", null);

        webTestClient.method(org.springframework.http.HttpMethod.DELETE)
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @Order(6)
    void deleteTraining_WithNonExistentUsers_ShouldReturnNotFound() {
        DeleteTrainingRequest request = new DeleteTrainingRequest(
                "nonexistent.trainee",
                "nonexistent.trainer",
                LocalDate.now()
        );

        webTestClient.method(org.springframework.http.HttpMethod.DELETE)
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken)
                .bodyValue(request)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(7)
    void deleteTraining_WithNonExistentTraining_ShouldReturnNotFound() {
        DeleteTrainingRequest request = new DeleteTrainingRequest(
                traineeUsername,
                trainerUsername,
                LocalDate.now().plusDays(30) // Future date that doesn't exist
        );

        webTestClient.method(org.springframework.http.HttpMethod.DELETE)
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken)
                .bodyValue(request)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(8)
    void deleteTraining_WithoutAuthorization_ShouldReturnUnauthorized() {
        DeleteTrainingRequest request = new DeleteTrainingRequest(
                traineeUsername,
                trainerUsername,
                trainingDate
        );

        webTestClient.method(org.springframework.http.HttpMethod.DELETE)
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    private LoginResponse authenticate(String username, String password) {
        LoginRequest loginRequest = new LoginRequest(username, password);
        
        ParameterizedTypeReference<ApiStandardResponse<LoginResponse>> responseType = 
            new ParameterizedTypeReference<ApiStandardResponse<LoginResponse>>() {};
            
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