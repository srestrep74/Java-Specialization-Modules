package com.sro.SpringCoreTask1.controller.trainee;

import com.sro.SpringCoreTask1.dtos.v1.request.auth.LoginRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.trainee.*;
import com.sro.SpringCoreTask1.dtos.v1.request.training.TraineeTrainingResponse;
import com.sro.SpringCoreTask1.dtos.v1.response.auth.LoginResponse;
import com.sro.SpringCoreTask1.dtos.v1.response.trainee.*;
import com.sro.SpringCoreTask1.dtos.v1.response.trainer.UnassignedTrainerResponse;
import com.sro.SpringCoreTask1.util.response.ApiStandardError;
import com.sro.SpringCoreTask1.util.response.ApiStandardResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.profiles.active=local")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TraineeControllerRestTemplateTest {

        private static final String BASE_URL = "/api/v1/trainees";
        private static String createdTraineeUsername;
        private static String createdTraineePassword;
        private static String accessToken;

        @Autowired
        private TestRestTemplate restTemplate;

        @Test
        @Order(1)
        void registerTrainee_ShouldCreateNewTrainee() {
                RegisterTraineeRequest request = new RegisterTraineeRequest(
                                "Sebas",
                                "Rpo",
                                LocalDate.of(1990, 5, 15),
                                "123 Main St, New York");

                ResponseEntity<ApiStandardResponse<RegisterTraineeResponse>> response = restTemplate.exchange(
                                BASE_URL,
                                HttpMethod.POST,
                                new HttpEntity<>(request, createJsonHeaders()),
                                new ParameterizedTypeReference<ApiStandardResponse<RegisterTraineeResponse>>() {
                                });

                assertEquals(HttpStatus.CREATED, response.getStatusCode());
                assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

                ApiStandardResponse<RegisterTraineeResponse> responseBody = response.getBody();
                assertNotNull(responseBody);
                assertNotNull(responseBody.data());
                assertNotNull(responseBody.data().username());
                assertNotNull(responseBody.data().password());

                createdTraineeUsername = responseBody.data().username();
                createdTraineePassword = responseBody.data().plainPassword();
                LoginResponse loginResponse = authenticate(createdTraineeUsername, createdTraineePassword);
                accessToken = loginResponse.token();
        }

        @Test
        @Order(2)
        void getProfile_ShouldReturnTraineeProfile() {
                HttpHeaders headers = createJsonHeaders();
                headers.setBearerAuth(accessToken);

                ResponseEntity<ApiStandardResponse<TraineeProfileResponse>> response = restTemplate.exchange(
                                BASE_URL + "/{username}",
                                HttpMethod.GET,
                                new HttpEntity<>(headers),
                                new ParameterizedTypeReference<ApiStandardResponse<TraineeProfileResponse>>() {
                                },
                                createdTraineeUsername);

                assertEquals(HttpStatus.OK, response.getStatusCode());

                ApiStandardResponse<TraineeProfileResponse> apiResponse = response.getBody();
                assertNotNull(apiResponse);

                TraineeProfileResponse profile = apiResponse.data();
                assertNotNull(profile);
                assertEquals("Sebas", profile.firstName());
                assertEquals("Rpo", profile.lastName());
                assertEquals(LocalDate.of(1990, 5, 15), profile.dateOfBirth());
                assertEquals("123 Main St, New York", profile.address());
                assertTrue(profile.active());
                assertNotNull(profile.trainers());
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

                HttpHeaders headers = createJsonHeaders();
                headers.setBearerAuth(accessToken);

                HttpEntity<UpdateTraineeProfileRequest> requestEntity = new HttpEntity<>(
                                updateRequest,
                                headers);

                ResponseEntity<ApiStandardResponse<TraineeProfileResponse>> response = restTemplate.exchange(
                                BASE_URL + "/{username}",
                                HttpMethod.PUT,
                                requestEntity,
                                new ParameterizedTypeReference<ApiStandardResponse<TraineeProfileResponse>>() {
                                },
                                createdTraineeUsername);

                assertEquals(HttpStatus.OK, response.getStatusCode());

                ApiStandardResponse<TraineeProfileResponse> apiResponse = response.getBody();
                assertNotNull(apiResponse);

                TraineeProfileResponse updatedProfile = apiResponse.data();
                assertNotNull(updatedProfile);
                assertEquals("Sebas Updated", updatedProfile.firstName());
                assertEquals("Rpo Updated", updatedProfile.lastName());
                assertEquals(LocalDate.of(1991, 6, 16), updatedProfile.dateOfBirth());
                assertEquals("456 Updated St, Boston", updatedProfile.address());
        }

        @Test
        @Order(4)
        void getTraineeTrainings_ShouldReturnTrainingList() {
                String url = BASE_URL + "/{username}/trainings?fromDate={fromDate}&toDate={toDate}" +
                                "&sortField={sortField}&sortDirection={sortDirection}";

                HttpHeaders headers = createJsonHeaders();
                headers.setBearerAuth(accessToken);

                ResponseEntity<ApiStandardResponse<List<TraineeTrainingResponse>>> response = restTemplate.exchange(
                                url,
                                HttpMethod.GET,
                                new HttpEntity<>(headers),
                                new ParameterizedTypeReference<ApiStandardResponse<List<TraineeTrainingResponse>>>() {
                                },
                                createdTraineeUsername,
                                "2023-01-01",
                                "2023-12-31",
                                "trainingDate",
                                "DESC");

                ApiStandardResponse<List<TraineeTrainingResponse>> responseBody = response.getBody();
                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertNotNull(responseBody);
                assertNotNull(responseBody.data());
        }

        @Test
        @Order(5)
        void getUnassignedTrainers_ShouldReturnTrainerList() {
                HttpHeaders headers = createJsonHeaders();
                headers.setBearerAuth(accessToken);

                ResponseEntity<ApiStandardResponse<List<UnassignedTrainerResponse>>> response = restTemplate.exchange(
                                BASE_URL + "/{username}/unassigned-trainers",
                                HttpMethod.GET,
                                new HttpEntity<>(headers),
                                new ParameterizedTypeReference<ApiStandardResponse<List<UnassignedTrainerResponse>>>() {
                                },
                                createdTraineeUsername);

                assertEquals(HttpStatus.OK, response.getStatusCode());

                ApiStandardResponse<List<UnassignedTrainerResponse>> responseBody = response.getBody();
                assertNotNull(responseBody);
                assertNotNull(responseBody.data());
        }

        @Test
        @Order(6)
        void registerTrainee_WithInvalidData_ShouldReturnBadRequest() {
                ResponseEntity<ApiStandardError> response = restTemplate.exchange(
                                BASE_URL,
                                HttpMethod.POST,
                                new HttpEntity<>(new RegisterTraineeRequest("", "", null, ""), createJsonHeaders()),
                                ApiStandardError.class);

                assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                assertNotNull(response.getBody());
        }

        @Test
        @Order(7)
        void getProfile_WithNonExistentUsername_ShouldReturnNotFound() {
                HttpHeaders headers = createJsonHeaders();
                headers.setBearerAuth(accessToken);

                ResponseEntity<ApiStandardError> response = restTemplate.exchange(
                                BASE_URL + "/nonexistentuser",
                                HttpMethod.GET,
                                new HttpEntity<>(headers),
                                ApiStandardError.class);

                assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
                assertNotNull(response.getBody());
        }

        @Test
        @Order(8)
        void deleteProfile_ShouldRemoveTrainee() {
                HttpHeaders headers = createJsonHeaders();
                headers.setBearerAuth(accessToken);

                restTemplate.exchange(
                                BASE_URL + "/{username}",
                                HttpMethod.DELETE,
                                new HttpEntity<>(headers),
                                Void.class,
                                createdTraineeUsername);

                ResponseEntity<ApiStandardError> response = restTemplate.exchange(
                                BASE_URL + "/{username}",
                                HttpMethod.GET,
                                new HttpEntity<>(headers),
                                ApiStandardError.class,
                                createdTraineeUsername);

                assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        }

        private LoginResponse authenticate(String username, String password) {
                ResponseEntity<ApiStandardResponse<LoginResponse>> response = restTemplate.exchange(
                                "/api/v1/auth/login",
                                HttpMethod.POST,
                                new HttpEntity<>(new LoginRequest(username, password), createJsonHeaders()),
                                new ParameterizedTypeReference<ApiStandardResponse<LoginResponse>>() {
                                });

                assertEquals(HttpStatus.OK, response.getStatusCode());
                ApiStandardResponse<LoginResponse> responseBody = response.getBody();
                return responseBody != null ? responseBody.data() : null;
        }

        private HttpHeaders createJsonHeaders() {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                return headers;
        }
}