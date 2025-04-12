package com.sro.SpringCoreTask1.controller.trainee;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sro.SpringCoreTask1.dtos.v1.request.auth.LoginRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.trainee.*;
import com.sro.SpringCoreTask1.dtos.v1.request.training.TraineeTrainingResponse;
import com.sro.SpringCoreTask1.dtos.v1.response.auth.LoginResponse;
import com.sro.SpringCoreTask1.dtos.v1.response.trainee.*;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TraineeControllerWebTestClientTest {

    private static final String BASE_URL = "/api/v1/trainees";
    private static String createdTraineeUsername;
    private static String createdTraineePassword;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @Order(1)
    void registerTrainee_ShouldCreateNewTrainee() throws JsonProcessingException {
        RegisterTraineeRequest request = new RegisterTraineeRequest(
                "Sebas",
                "Rpo",
                LocalDate.of(1990, 5, 15),
                "123 Main St, New York"
        );

        webTestClient.post()
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(new ParameterizedTypeReference<ApiStandardResponse<RegisterTraineeResponse>>() {})
                .consumeWith(response -> {
                    ApiStandardResponse<RegisterTraineeResponse> responseBody = response.getResponseBody();
                    assertNotNull(responseBody);
                    assertNotNull(responseBody.data());
                    assertNotNull(responseBody.data().username());
                    assertNotNull(responseBody.data().password());
                    
                    createdTraineeUsername = responseBody.data().username();
                    createdTraineePassword = responseBody.data().password();
                    authenticate(createdTraineeUsername, createdTraineePassword);
                });
    }

    @Test
    @Order(2)
    void getProfile_ShouldReturnTraineeProfile() {
        webTestClient.get()
                .uri(BASE_URL + "/{username}", createdTraineeUsername)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<ApiStandardResponse<TraineeProfileResponse>>() {})
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
                true
        );

        webTestClient.put()
                .uri(BASE_URL + "/{username}", createdTraineeUsername)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<ApiStandardResponse<TraineeProfileResponse>>() {})
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
                .bodyValue(deactivateRequest)
                .exchange()
                .expectStatus().isNoContent();

        webTestClient.get()
                .uri(BASE_URL + "/{username}", createdTraineeUsername)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<ApiStandardResponse<TraineeProfileResponse>>() {})
                .consumeWith(response -> {
                    ApiStandardResponse<TraineeProfileResponse> apiResponse = response.getResponseBody();
                    assertNotNull(apiResponse);
                    assertFalse(apiResponse.data().active());
                });
    }

    @Test
    @Order(5)
    void getTraineeTrainings_ShouldReturnTrainingList() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(BASE_URL + "/{username}/trainings")
                        .queryParam("fromDate", "2023-01-01")
                        .queryParam("toDate", "2023-12-31")
                        .queryParam("sortField", "trainingDate")
                        .queryParam("sortDirection", "DESC")
                        .build(createdTraineeUsername))
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<ApiStandardResponse<List<TraineeTrainingResponse>>>() {})
                .consumeWith(response -> {
                    ApiStandardResponse<List<TraineeTrainingResponse>> apiResponse = response.getResponseBody();
                    assertNotNull(apiResponse);
                    assertNotNull(apiResponse.data());
                });
    }

    @Test
    @Order(6)
    void deleteProfile_ShouldRemoveTrainee() {
        webTestClient.delete()
                .uri(BASE_URL + "/{username}", createdTraineeUsername)
                .exchange()
                .expectStatus().isNoContent();

        webTestClient.get()
                .uri(BASE_URL + "/{username}", createdTraineeUsername)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(7)
    void registerTrainee_WithInvalidData_ShouldReturnBadRequest() {
        RegisterTraineeRequest invalidRequest = new RegisterTraineeRequest(
                "",
                "",
                null,
                ""
        );

        webTestClient.post()
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(new ParameterizedTypeReference<ApiStandardError>() {});
    }

    @Test
    @Order(8)
    void getProfile_WithNonExistentUsername_ShouldReturnNotFound() {
        webTestClient.get()
                .uri(BASE_URL + "/nonexistentuser")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(new ParameterizedTypeReference<ApiStandardError>() {});
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