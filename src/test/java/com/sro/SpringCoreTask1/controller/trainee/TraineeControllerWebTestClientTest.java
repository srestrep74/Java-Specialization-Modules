package com.sro.SpringCoreTask1.controller.trainee;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sro.SpringCoreTask1.dtos.v1.request.auth.LoginRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.trainee.*;
import com.sro.SpringCoreTask1.dtos.v1.request.training.TraineeTrainingResponse;
import com.sro.SpringCoreTask1.dtos.v1.response.auth.LoginResponse;
import com.sro.SpringCoreTask1.dtos.v1.response.trainee.*;
import com.sro.SpringCoreTask1.dtos.v1.response.trainer.UnassignedTrainerResponse;
import com.sro.SpringCoreTask1.util.response.ApiStandardResponse;
import com.sro.SpringCoreTask1.util.response.ApiStandardError;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.profiles.active=local")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TraineeControllerWebTestClientTest {

    private static final String BASE_URL = "/api/v1/trainees";
    private static String createdTraineeUsername;
    private static String createdTraineePassword;
    private static String accessToken;
    private static List<UnassignedTrainerResponse> availableTrainers;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @Order(1)
    void registerTrainee_ShouldCreateNewTrainee() throws JsonProcessingException {
        RegisterTraineeRequest request = new RegisterTraineeRequest(
                "Sebas",
                "Rpo",
                LocalDate.of(1990, 5, 15),
                "123 Main St, New York");

        webTestClient.post()
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(new ParameterizedTypeReference<ApiStandardResponse<RegisterTraineeResponse>>() {
                })
                .consumeWith(response -> {
                    ApiStandardResponse<RegisterTraineeResponse> responseBody = response.getResponseBody();
                    assertNotNull(responseBody);
                    assertNotNull(responseBody.data());
                    assertNotNull(responseBody.data().username());
                    assertNotNull(responseBody.data().password());

                    createdTraineeUsername = responseBody.data().username();
                    createdTraineePassword = responseBody.data().plainPassword();
                    LoginResponse loginResponse = authenticate(createdTraineeUsername, createdTraineePassword);
                    accessToken = loginResponse.token();
                });
    }

    @Test
    @Order(2)
    void getProfile_ShouldReturnTraineeProfile() {
        webTestClient.get()
                .uri(BASE_URL + "/{username}", createdTraineeUsername)
                .header("Authorization", "Bearer " + accessToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<ApiStandardResponse<TraineeProfileResponse>>() {
                })
                .consumeWith(response -> {
                    ApiStandardResponse<TraineeProfileResponse> apiResponse = response.getResponseBody();
                    assertNotNull(apiResponse);

                    TraineeProfileResponse profile = apiResponse.data();
                    assertNotNull(profile);
                    assertEquals("Sebas", profile.firstName());
                    assertEquals("Rpo", profile.lastName());
                    assertEquals(LocalDate.of(1990, 5, 15), profile.dateOfBirth());
                    assertEquals("123 Main St, New York", profile.address());
                    assertTrue(profile.active());
                    assertNotNull(profile.trainers());
                });
    }

    @Test
    @Order(3)
    void updateProfile_ShouldUpdateTraineeDetails() {
        UpdateTraineeProfileRequest updateRequest = new UpdateTraineeProfileRequest(
                "Sebas Updated",
                "Rpo Updated",
                LocalDate.of(1991, 6, 16),
                "456 Updated St, Boston",
                true);

        webTestClient.put()
                .uri(BASE_URL + "/{username}", createdTraineeUsername)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<ApiStandardResponse<TraineeProfileResponse>>() {
                })
                .consumeWith(response -> {
                    ApiStandardResponse<TraineeProfileResponse> apiResponse = response.getResponseBody();
                    assertNotNull(apiResponse);

                    TraineeProfileResponse updatedProfile = apiResponse.data();
                    assertNotNull(updatedProfile);
                    assertEquals("Sebas Updated", updatedProfile.firstName());
                    assertEquals("Rpo Updated", updatedProfile.lastName());
                    assertEquals(LocalDate.of(1991, 6, 16), updatedProfile.dateOfBirth());
                    assertEquals("456 Updated St, Boston", updatedProfile.address());
                });
    }

    @Test
    @Order(4)
    void updateActivationStatus_ShouldDeactivateTrainee() {
        UpdateTraineeActivation deactivateRequest = new UpdateTraineeActivation(false);

        webTestClient.patch()
                .uri(BASE_URL + "/{username}/activation", createdTraineeUsername)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken)
                .bodyValue(deactivateRequest)
                .exchange()
                .expectStatus().isOk();

        webTestClient.get()
                .uri(BASE_URL + "/{username}", createdTraineeUsername)
                .header("Authorization", "Bearer " + accessToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<ApiStandardResponse<TraineeProfileResponse>>() {
                })
                .consumeWith(response -> {
                    ApiStandardResponse<TraineeProfileResponse> apiResponse = response.getResponseBody();
                    assertNotNull(apiResponse);
                    TraineeProfileResponse profile = apiResponse.data();
                    assertFalse(profile.active());
                });
    }

    @Test
    @Order(5)
    void updateActivationStatus_ShouldReactivateTrainee() {
        UpdateTraineeActivation activateRequest = new UpdateTraineeActivation(true);

        webTestClient.patch()
                .uri(BASE_URL + "/{username}/activation", createdTraineeUsername)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken)
                .bodyValue(activateRequest)
                .exchange()
                .expectStatus().isOk();

        webTestClient.get()
                .uri(BASE_URL + "/{username}", createdTraineeUsername)
                .header("Authorization", "Bearer " + accessToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<ApiStandardResponse<TraineeProfileResponse>>() {
                })
                .consumeWith(response -> {
                    ApiStandardResponse<TraineeProfileResponse> apiResponse = response.getResponseBody();
                    assertNotNull(apiResponse);
                    TraineeProfileResponse profile = apiResponse.data();
                    assertTrue(profile.active());
                });
    }

    @Test
    @Order(6)
    void getTraineeTrainings_ShouldReturnTrainingList() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(BASE_URL + "/{username}/trainings")
                        .queryParam("fromDate", "2023-01-01")
                        .queryParam("toDate", "2023-12-31")
                        .queryParam("sortField", "trainingDate")
                        .queryParam("sortDirection", "DESC")
                        .build(createdTraineeUsername))
                .header("Authorization", "Bearer " + accessToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<ApiStandardResponse<List<TraineeTrainingResponse>>>() {
                })
                .consumeWith(response -> {
                    ApiStandardResponse<List<TraineeTrainingResponse>> apiResponse = response.getResponseBody();
                    assertNotNull(apiResponse);
                    assertNotNull(apiResponse.data());
                });
    }

    @Test
    @Order(7)
    void getTraineeTrainings_WithDifferentSort_ShouldReturnTrainingList() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(BASE_URL + "/{username}/trainings")
                        .queryParam("sortField", "duration")
                        .queryParam("sortDirection", "ASC")
                        .build(createdTraineeUsername))
                .header("Authorization", "Bearer " + accessToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<ApiStandardResponse<List<TraineeTrainingResponse>>>() {
                });
    }

    @Test
    @Order(8)
    void getUnassignedTrainers_ShouldReturnTrainerList() {
        webTestClient.get()
                .uri(BASE_URL + "/{username}/unassigned-trainers", createdTraineeUsername)
                .header("Authorization", "Bearer " + accessToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<ApiStandardResponse<List<UnassignedTrainerResponse>>>() {
                })
                .consumeWith(response -> {
                    ApiStandardResponse<List<UnassignedTrainerResponse>> apiResponse = response.getResponseBody();
                    assertNotNull(apiResponse);
                    assertNotNull(apiResponse.data());
                    availableTrainers = apiResponse.data();
                });
    }

    @Test
    @Order(9)
    void updateTrainersList_ShouldAssignTrainers() {
        if (availableTrainers == null || availableTrainers.isEmpty()) {
            return;
        }

        List<String> trainerUsernames = List.of(availableTrainers.get(0).username());
        UpdateTraineeTrainerListRequest updateRequest = new UpdateTraineeTrainerListRequest(trainerUsernames);

        webTestClient.put()
                .uri(BASE_URL + "/{username}/trainers", createdTraineeUsername)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<ApiStandardResponse<List<TrainerSummaryResponse>>>() {
                })
                .consumeWith(response -> {
                    ApiStandardResponse<List<TrainerSummaryResponse>> apiResponse = response.getResponseBody();
                    assertNotNull(apiResponse);
                    assertNotNull(apiResponse.data());
                    assertEquals(trainerUsernames.size(), apiResponse.data().size());
                });

        webTestClient.get()
                .uri(BASE_URL + "/{username}", createdTraineeUsername)
                .header("Authorization", "Bearer " + accessToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<ApiStandardResponse<TraineeProfileResponse>>() {
                })
                .consumeWith(response -> {
                    ApiStandardResponse<TraineeProfileResponse> apiResponse = response.getResponseBody();
                    assertNotNull(apiResponse);
                    TraineeProfileResponse profile = apiResponse.data();
                    assertFalse(profile.trainers().isEmpty());
                });
    }

    @Test
    @Order(10)
    void updateTrainersList_WithEmptyList_ShouldRemoveAllTrainers() {
        UpdateTraineeTrainerListRequest updateRequest = new UpdateTraineeTrainerListRequest(List.of());

        webTestClient.put()
                .uri(BASE_URL + "/{username}/trainers", createdTraineeUsername)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<ApiStandardResponse<List<TrainerSummaryResponse>>>() {
                })
                .consumeWith(response -> {
                    ApiStandardResponse<List<TrainerSummaryResponse>> apiResponse = response.getResponseBody();
                    assertNotNull(apiResponse);
                    assertTrue(apiResponse.data().isEmpty());
                });

        webTestClient.get()
                .uri(BASE_URL + "/{username}", createdTraineeUsername)
                .header("Authorization", "Bearer " + accessToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<ApiStandardResponse<TraineeProfileResponse>>() {
                })
                .consumeWith(response -> {
                    ApiStandardResponse<TraineeProfileResponse> apiResponse = response.getResponseBody();
                    assertNotNull(apiResponse);
                    TraineeProfileResponse profile = apiResponse.data();
                    assertTrue(profile.trainers().isEmpty());
                });
    }

    @Test
    @Order(11)
    void registerTrainee_WithInvalidData_ShouldReturnBadRequest() {
        RegisterTraineeRequest invalidRequest = new RegisterTraineeRequest(
                "",
                "",
                null,
                "");

        webTestClient.post()
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(new ParameterizedTypeReference<ApiStandardError>() {
                });
    }

    @Test
    @Order(12)
    void getProfile_WithNonExistentUsername_ShouldReturnNotFound() {
        webTestClient.get()
                .uri(BASE_URL + "/nonexistentuser")
                .header("Authorization", "Bearer " + accessToken)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(new ParameterizedTypeReference<ApiStandardError>() {
                });
    }

    @Test
    @Order(13)
    void updateTrainersList_WithInvalidTrainer_ShouldReturnBadRequest() {
        UpdateTraineeTrainerListRequest updateRequest = new UpdateTraineeTrainerListRequest(
                List.of("nonexistenttrainer"));

        webTestClient.put()
                .uri(BASE_URL + "/{username}/trainers", createdTraineeUsername)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    @Order(14)
    void deleteProfile_ShouldRemoveTrainee() {
        webTestClient.delete()
                .uri(BASE_URL + "/{username}", createdTraineeUsername)
                .header("Authorization", "Bearer " + accessToken)
                .exchange()
                .expectStatus().isOk();

        webTestClient.get()
                .uri(BASE_URL + "/{username}", createdTraineeUsername)
                .header("Authorization", "Bearer " + accessToken)
                .exchange()
                .expectStatus().isUnauthorized();
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