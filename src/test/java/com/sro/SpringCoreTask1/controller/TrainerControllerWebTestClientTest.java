package com.sro.SpringCoreTask1.controller;

import com.sro.SpringCoreTask1.dtos.v1.request.auth.LoginRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.trainer.*;
import com.sro.SpringCoreTask1.dtos.v1.request.training.TrainerTrainingResponse;
import com.sro.SpringCoreTask1.dtos.v1.request.trainingType.TrainingTypeRequestDTO;
import com.sro.SpringCoreTask1.dtos.v1.response.auth.LoginResponse;
import com.sro.SpringCoreTask1.dtos.v1.response.trainer.*;
import com.sro.SpringCoreTask1.dtos.v1.response.trainingType.TrainingTypeResponse;
import com.sro.SpringCoreTask1.service.TrainingTypeService;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TrainerControllerWebTestClientTest {

    private static final String BASE_URL = "/api/v1/trainers";
    private static String createdTrainerUsername;
    private static String createdTrainerPassword;
    private static Long trainingTypeId;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeAll
    static void setupAll(
            @Autowired TrainingTypeService trainingTypeService) {
        
        TrainingTypeRequestDTO typeRequest = new TrainingTypeRequestDTO("Strength Training");
        TrainingTypeResponse typeResponse = trainingTypeService.save(typeRequest);
        trainingTypeId = typeResponse.trainingTypeId();
    }

    @Test
    @Order(1)
    void registerTrainer_ShouldCreateNewTrainer() {
        RegisterTrainerRequest request = new RegisterTrainerRequest(
                "Sergio",
                "Rodriguez",
                trainingTypeId
        );

        webTestClient.post()
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(RegisterTrainerResponse.class)
                .consumeWith(response -> {
                    RegisterTrainerResponse responseBody = response.getResponseBody();
                    assertNotNull(responseBody);
                    createdTrainerUsername = responseBody.username();
                    createdTrainerPassword = responseBody.password();
                    authenticate(createdTrainerUsername, createdTrainerPassword);
                });
    }

    @Test
    @Order(2)
    void getProfile_ShouldReturnTrainerProfile() {
        webTestClient.get()
                .uri(BASE_URL + "/{username}", createdTrainerUsername)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TrainerProfileResponse.class)
                .consumeWith(response -> {
                    TrainerProfileResponse profile = response.getResponseBody();
                    assertNotNull(profile);
                    assertEquals("Sergio", profile.firstName());
                    assertEquals("Rodriguez", profile.lastName());
                    assertEquals(3L, profile.specialization());
                });
    }

    @Test
    @Order(3)
    void updateProfile_ShouldUpdateTrainerDetails() {
        UpdateTrainerProfileRequest updateRequest = new UpdateTrainerProfileRequest(
                "Sergio Updated",
                "Rodriguez Updated",
                1L,
                true
        );

        webTestClient.put()
                .uri(BASE_URL + "/{username}", createdTrainerUsername)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TrainerProfileResponse.class)
                .consumeWith(response -> {
                    TrainerProfileResponse updatedProfile = response.getResponseBody();
                    assertNotNull(updatedProfile);
                    assertEquals("Sergio Updated", updatedProfile.firstName());
                    assertEquals("Rodriguez Updated", updatedProfile.lastName());
                });
    }

    @Test
    @Order(4)
    void getTrainerTrainings_ShouldReturnTrainingList() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(BASE_URL + "/{username}/trainings")
                        .queryParam("fromDate", "2023-01-01")
                        .queryParam("toDate", "2023-12-31")
                        .queryParam("traineeName", "testTrainee")
                        .build(createdTrainerUsername))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TrainerTrainingResponse.class)
                .value(trainings -> assertNotNull(trainings));
    }

    @Test
    @Order(5)
    void registerTrainer_WithInvalidData_ShouldReturnBadRequest() {
        RegisterTrainerRequest invalidRequest = new RegisterTrainerRequest("", "", null);
        webTestClient.post()
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @Order(6)
    void getProfile_WithNonExistentUsername_ShouldReturnNotFound() {
        webTestClient.get()
                .uri(BASE_URL + "/nonexistentuser")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(7)
    void updateActivationStatus_ShouldDeactivateTrainer() {
        UpdateTrainerActivation deactivateRequest = new UpdateTrainerActivation(false);

        webTestClient.patch()
                .uri(BASE_URL + "/{username}/activation", createdTrainerUsername)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(deactivateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().exists(HttpHeaders.LOCATION);
    }

    private LoginResponse authenticate(String username, String password) {
        LoginRequest loginRequest = new LoginRequest(username, password);
        return webTestClient.post()
                .uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(LoginResponse.class)
                .returnResult()
                .getResponseBody();
    }
}