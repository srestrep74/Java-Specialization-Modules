package com.sro.SpringCoreTask1.controller;

import com.sro.SpringCoreTask1.dtos.v1.request.auth.ChangePasswordRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.auth.LoginRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.auth.RefreshTokenRequest;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.profiles.active=local")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerWebTestClientTest {

        private static final String AUTH_URL = "/api/v1/auth";
        private static final String TRAINEE_URL = "/api/v1/trainees";
        private static String testUsername;
        private static String testPassword;
        private static String accessToken;
        private static String refreshToken;

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
                                .expectBody(new ParameterizedTypeReference<ApiStandardResponse<RegisterTraineeResponse>>() {
                                })
                                .consumeWith(response -> {
                                        ApiStandardResponse<RegisterTraineeResponse> responseBody = response
                                                        .getResponseBody();
                                        assertNotNull(responseBody);
                                        assertNotNull(responseBody.data());
                                        assertNotNull(responseBody.data().username());
                                        assertNotNull(responseBody.data().plainPassword());

                                        testUsername = responseBody.data().username();
                                        testPassword = responseBody.data().plainPassword();
                                });
        }

        @Test
        @Order(1)
        void login_WithValidCredentials_ShouldReturnToken() {
                LoginRequest loginRequest = new LoginRequest(testUsername, testPassword);

                ParameterizedTypeReference<ApiStandardResponse<LoginResponse>> responseType = new ParameterizedTypeReference<ApiStandardResponse<LoginResponse>>() {
                };

                webTestClient.post()
                                .uri(AUTH_URL + "/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(loginRequest)
                                .exchange()
                                .expectStatus().isOk()
                                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                                .expectBody(responseType)
                                .consumeWith(response -> {
                                        ApiStandardResponse<LoginResponse> responseBody = response.getResponseBody();
                                        assertNotNull(responseBody);
                                        assertNotNull(responseBody.data());
                                        LoginResponse loginResponse = responseBody.data();
                                        assertNotNull(loginResponse);
                                        assertNotNull(loginResponse.username());
                                        assertNotNull(loginResponse.token());
                                        assertTrue(loginResponse.success());

                                        accessToken = loginResponse.token();
                                        refreshToken = loginResponse.refreshToken();
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
                                .expectStatus().is4xxClientError();
        }

        @Test
        @Order(3)
        void refreshToken_WithValidRefreshToken_ShouldReturnNewTokens() {
                RefreshTokenRequest refreshRequest = new RefreshTokenRequest(refreshToken);

                webTestClient.post()
                                .uri(AUTH_URL + "/refresh")
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(refreshRequest)
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody(LoginResponse.class)
                                .consumeWith(response -> {
                                        LoginResponse responseBody = response.getResponseBody();
                                        assertNotNull(responseBody);
                                        assertNotNull(responseBody.token());
                                        assertNotNull(responseBody.refreshToken());
                                        assertEquals(testUsername, responseBody.username());

                                        accessToken = responseBody.token();
                                        refreshToken = responseBody.refreshToken();
                                });
        }

        @Test
        @Order(4)
        void refreshToken_WithInvalidRefreshToken_ShouldReturnUnauthorized() {
                RefreshTokenRequest refreshRequest = new RefreshTokenRequest("invalid.refresh.token");

                webTestClient.post()
                                .uri(AUTH_URL + "/refresh")
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(refreshRequest)
                                .exchange()
                                .expectStatus().isUnauthorized();
        }

        @Test
        @Order(5)
        void changePassword_WithValidData_ShouldReturnNoContent() {
                String newPassword = "newSecurePassword123";
                ChangePasswordRequest changeRequest = new ChangePasswordRequest(
                                testUsername,
                                testPassword,
                                newPassword);

                webTestClient.put()
                                .uri(AUTH_URL + "/change-password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + accessToken)
                                .bodyValue(changeRequest)
                                .exchange()
                                .expectStatus().isOk();

                testPassword = newPassword;

                LoginRequest loginRequest = new LoginRequest(testUsername, testPassword);

                ParameterizedTypeReference<ApiStandardResponse<LoginResponse>> responseType = new ParameterizedTypeReference<ApiStandardResponse<LoginResponse>>() {
                };

                webTestClient.post()
                                .uri(AUTH_URL + "/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(loginRequest)
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody(responseType)
                                .consumeWith(response -> {
                                        ApiStandardResponse<LoginResponse> responseBody = response.getResponseBody();
                                        assertNotNull(responseBody);
                                        assertNotNull(responseBody.data());
                                        assertNotNull(responseBody.data().token());

                                        accessToken = responseBody.data().token();
                                        refreshToken = responseBody.data().refreshToken();
                                });
        }

        @Test
        @Order(6)
        void changePassword_WithInvalidOldPassword_ShouldReturnBadRequest() {
                ChangePasswordRequest changeRequest = new ChangePasswordRequest(
                                testUsername,
                                "wrongOldPassword",
                                "newPassword123");

                webTestClient.put()
                                .uri(AUTH_URL + "/change-password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + accessToken)
                                .bodyValue(changeRequest)
                                .exchange()
                                .expectStatus().is4xxClientError();
        }

        @Test
        @Order(7)
        void changePassword_WithInvalidUsername_ShouldReturn4xxClientError() {
                ChangePasswordRequest changeRequest = new ChangePasswordRequest(
                                testUsername + "invalid",
                                testPassword,
                                "newPassword123");

                webTestClient.put()
                                .uri(AUTH_URL + "/change-password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + accessToken)
                                .bodyValue(changeRequest)
                                .exchange()
                                .expectStatus().is4xxClientError();
        }

        @Test
        @Order(8)
        void changePassword_WithoutToken_ShouldReturnUnauthorized() {
                ChangePasswordRequest changeRequest = new ChangePasswordRequest(
                                testUsername,
                                testPassword,
                                "newPassword123");

                webTestClient.put()
                                .uri(AUTH_URL + "/change-password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(changeRequest)
                                .exchange()
                                .expectStatus().isUnauthorized();
        }

        @Test
        @Order(9)
        void logout_WithValidToken_ShouldReturnNoContent() {
                webTestClient.post()
                                .uri(AUTH_URL + "/logout")
                                .header("Authorization", "Bearer " + accessToken)
                                .exchange()
                                .expectStatus().isNoContent();

                webTestClient.put()
                                .uri(AUTH_URL + "/change-password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + accessToken)
                                .bodyValue(new ChangePasswordRequest(testUsername, testPassword, "anotherPassword"))
                                .exchange()
                                .expectStatus().isUnauthorized();
        }

        @Test
        @Order(10)
        void logout_WithoutToken_ShouldSucceed() {
                webTestClient.post()
                                .uri(AUTH_URL + "/logout")
                                .exchange()
                                .expectStatus().isNoContent();
        }

        @Test
        @Order(11)
        void refreshToken_AfterLogout_ShouldFail() {
                RefreshTokenRequest refreshRequest = new RefreshTokenRequest(refreshToken);

                webTestClient.post()
                                .uri(AUTH_URL + "/refresh")
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(refreshRequest)
                                .exchange()
                                .expectStatus().is4xxClientError();
        }
}