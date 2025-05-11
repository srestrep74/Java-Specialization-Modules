package com.sro.SpringCoreTask1.controller;

import com.sro.SpringCoreTask1.dtos.v1.request.auth.LoginRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.trainee.RegisterTraineeRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.trainingType.TrainingTypeRequestDTO;
import com.sro.SpringCoreTask1.dtos.v1.response.auth.LoginResponse;
import com.sro.SpringCoreTask1.dtos.v1.response.trainee.RegisterTraineeResponse;
import com.sro.SpringCoreTask1.dtos.v1.response.trainingType.TrainingTypeResponse;
import com.sro.SpringCoreTask1.service.TraineeService;
import com.sro.SpringCoreTask1.service.TrainingTypeService;
import com.sro.SpringCoreTask1.util.response.ApiStandardResponse;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.profiles.active=local")
class TrainingTypeControllerWebTestClientTest {

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