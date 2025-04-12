package com.sro.SpringCoreTask1.controller;

import com.sro.SpringCoreTask1.dtos.v1.request.auth.ChangePasswordRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.auth.LoginRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.trainee.RegisterTraineeRequest;
import com.sro.SpringCoreTask1.dtos.v1.response.auth.LoginResponse;
import com.sro.SpringCoreTask1.dtos.v1.response.trainee.RegisterTraineeResponse;
import com.sro.SpringCoreTask1.util.response.ApiStandardResponse;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerWebTestClientTest {

    private static final String AUTH_URL = "/api/v1/auth";
    private static final String TRAINEE_URL = "/api/v1/trainees";
    private static String testUsername;
    private static String testPassword;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeAll
    static void setup(@Autowired WebTestClient webTestClient) {
        RegisterTraineeRequest registerRequest = new RegisterTraineeRequest(
                    "Test", "User", LocalDate.of(1990, 1, 1), "Test Address");

        webTestClient.post()
                .uri(TRAINEE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(registerRequest)
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

                    testUsername = responseBody.data().username();
                    testPassword = responseBody.data().password();
                });
    }

    @Test
    @Order(1)
    void login_WithValidCredentials_ShouldReturnToken() {
        LoginRequest loginRequest = new LoginRequest(testUsername, testPassword);

        webTestClient.post()
                .uri(AUTH_URL + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(LoginResponse.class)
                .consumeWith(response -> {
                    LoginResponse loginResponse = response.getResponseBody();
                    assertNotNull(loginResponse);
                    assertNotNull(loginResponse.username());
                });
    }

    @Test
    @Order(2)
    void login_WithInvalidCredentials_ShouldReturnUnauthorized() {
        LoginRequest loginRequest = new LoginRequest(testUsername, "wrongpassword");

        webTestClient.post()
                .uri(AUTH_URL + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    @Order(3)
    void changePassword_WithValidData_ShouldReturnNoContent() {
        String newPassword = "newSecurePassword123";
        ChangePasswordRequest changeRequest = new ChangePasswordRequest(
                testUsername, 
                testPassword, 
                newPassword
        );

        webTestClient.put()
                .uri(AUTH_URL + "/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(changeRequest)
                .exchange()
                .expectStatus().isNoContent();

        testPassword = newPassword;
    }

    @Test
    @Order(4)
    void changePassword_WithInvalidOldPassword_ShouldReturnBadRequest() {
        ChangePasswordRequest changeRequest = new ChangePasswordRequest(
                testUsername, 
                "wrongOldPassword", 
                "newPassword123"
        );

        webTestClient.put()
                .uri(AUTH_URL + "/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(changeRequest)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    @Order(5)
    void changePassword_WithInvalidUsername_ShouldReturn5xxServerError() {
        ChangePasswordRequest changeRequest = new ChangePasswordRequest(
                testUsername + "invalid", 
                testPassword, 
                "newPassword123"
        );

        webTestClient.put()
                .uri(AUTH_URL + "/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(changeRequest)
                .exchange()
                .expectStatus().is5xxServerError();
    }
}