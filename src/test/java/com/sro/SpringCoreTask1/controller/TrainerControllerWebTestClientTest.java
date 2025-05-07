package com.sro.SpringCoreTask1.controller;

import com.sro.SpringCoreTask1.dtos.v1.request.auth.LoginRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.trainer.*;
import com.sro.SpringCoreTask1.dtos.v1.request.training.TrainerTrainingResponse;
import com.sro.SpringCoreTask1.dtos.v1.request.trainingType.TrainingTypeRequestDTO;
import com.sro.SpringCoreTask1.dtos.v1.response.auth.LoginResponse;
import com.sro.SpringCoreTask1.dtos.v1.response.trainer.*;
import com.sro.SpringCoreTask1.dtos.v1.response.trainingType.TrainingTypeResponse;
import com.sro.SpringCoreTask1.service.TrainingTypeService;
import com.sro.SpringCoreTask1.util.response.ApiStandardResponse;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.profiles.active=local")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TrainerControllerWebTestClientTest {

    private static final String BASE_URL = "/api/v1/trainers";
    private static String createdTrainerUsername;
    private static String createdTrainerPassword;
    private static Long trainingTypeId;
    private static String accessToken;

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
                trainingTypeId);

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
                    assertNotNull(responseBody.username());
                    createdTrainerUsername = responseBody.username();
                    createdTrainerPassword = responseBody.plainPassword();

                    LoginResponse loginResponse = authenticate(createdTrainerUsername, createdTrainerPassword);
                    accessToken = loginResponse.token();
                });
    }

    @Test
    @Order(2)
    void getProfile_ShouldReturnTrainerProfile() {
        webTestClient.get()
                .uri(BASE_URL + "/{username}", createdTrainerUsername)
                .header("Authorization", "Bearer " + accessToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TrainerProfileResponse.class)
                .consumeWith(response -> {
                    TrainerProfileResponse profile = response.getResponseBody();
                    assertNotNull(profile);
                    assertEquals("Sergio", profile.firstName());
                    assertEquals("Rodriguez", profile.lastName());
                });
    }

    @Test
    @Order(3)
    void updateProfile_ShouldUpdateTrainerDetails() {
        UpdateTrainerProfileRequest updateRequest = new UpdateTrainerProfileRequest(
                "Sergio Updated",
                "Rodriguez Updated",
                6L,
                true);

        webTestClient.put()
                .uri(BASE_URL + "/{username}", createdTrainerUsername)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken)
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
                .header("Authorization", "Bearer " + accessToken)
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
                .header("Authorization", "Bearer " + accessToken)
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
                .header("Authorization", "Bearer " + accessToken)
                .bodyValue(deactivateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().exists(HttpHeaders.LOCATION);
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