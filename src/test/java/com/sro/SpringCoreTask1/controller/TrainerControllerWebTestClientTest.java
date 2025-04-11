package com.sro.SpringCoreTask1.controller;

import com.sro.SpringCoreTask1.dtos.v1.request.trainer.*;
import com.sro.SpringCoreTask1.dtos.v1.request.training.TrainerTrainingResponse;
import com.sro.SpringCoreTask1.dtos.v1.response.trainer.*;
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

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @Order(1)
    void registerTrainer_ShouldCreateNewTrainer() {
        RegisterTrainerRequest request = new RegisterTrainerRequest(
                "Sergio",
                "Rodriguez",
                1L
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
                    assertEquals(1L, profile.specialization());
                });
    }

    @Test
    @Order(3)
    void updateProfile_ShouldUpdateTrainerDetails() {
        UpdateTrainerProfileRequest updateRequest = new UpdateTrainerProfileRequest(
                createdTrainerUsername,
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
    void updateActivationStatus_ShouldDeactivateTrainer() {
        UpdateTrainerActivation deactivateRequest = new UpdateTrainerActivation(false);

        webTestClient.patch()
                .uri(BASE_URL + "/{username}/activation", createdTrainerUsername)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(deactivateRequest)
                .exchange()
                .expectStatus().isNoContent()
                .expectHeader().exists(HttpHeaders.LOCATION);

        webTestClient.get()
                .uri(BASE_URL + "/{username}", createdTrainerUsername)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.active").isEqualTo(false);
    }

    @Test
    @Order(5)
    void getUnassignedTrainers_ShouldReturnTrainerList() {
        webTestClient.get()
                .uri(BASE_URL + "/unassigned?traineeUsername=testTrainee")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UnassignedTrainerResponse.class)
                .value(trainers -> assertNotNull(trainers));
    }

    @Test
    @Order(6)
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
    @Order(7)
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
    @Order(8)
    void getProfile_WithNonExistentUsername_ShouldReturnNotFound() {
        webTestClient.get()
                .uri(BASE_URL + "/nonexistentuser")
                .exchange()
                .expectStatus().isNotFound();
    }
}