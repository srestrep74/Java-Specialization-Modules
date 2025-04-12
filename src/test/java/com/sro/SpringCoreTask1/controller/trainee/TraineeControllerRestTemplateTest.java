package com.sro.SpringCoreTask1.controller.trainee;

import com.sro.SpringCoreTask1.dtos.v1.request.auth.LoginRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.trainee.*;
import com.sro.SpringCoreTask1.dtos.v1.request.training.TraineeTrainingResponse;
import com.sro.SpringCoreTask1.dtos.v1.response.auth.LoginResponse;
import com.sro.SpringCoreTask1.dtos.v1.response.trainee.*;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TraineeControllerRestTemplateTest {

    private static final String BASE_URL = "/api/v1/trainees";
    private static String createdTraineeUsername;
    private static String createdTraineePassword;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @Order(1)
    void registerTrainee_ShouldCreateNewTrainee() {
        RegisterTraineeRequest request = new RegisterTraineeRequest(
            "Sebas",
            "Rpo",
            LocalDate.of(1990, 5, 15),
            "123 Main St, New York"
        );

        ResponseEntity<ApiStandardResponse<RegisterTraineeResponse>> response = restTemplate.exchange(
            BASE_URL,
            HttpMethod.POST,
            new HttpEntity<>(request, createJsonHeaders()),
            new ParameterizedTypeReference<ApiStandardResponse<RegisterTraineeResponse>>() {});

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

        ApiStandardResponse<RegisterTraineeResponse> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertNotNull(responseBody.data());
        assertNotNull(responseBody.data().username());
        assertNotNull(responseBody.data().password());
        
        createdTraineeUsername = responseBody.data().username();
        createdTraineePassword = responseBody.data().password();
        authenticate(createdTraineeUsername, createdTraineePassword);
    }

    @Test
    @Order(2)
    void getProfile_ShouldReturnTraineeProfile() {
        ResponseEntity<ApiStandardResponse<TraineeProfileResponse>> response = restTemplate.exchange(
            BASE_URL + "/{username}",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<ApiStandardResponse<TraineeProfileResponse>>() {},
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
            true
        );

        HttpEntity<UpdateTraineeProfileRequest> requestEntity = new HttpEntity<>(
            updateRequest, 
            createJsonHeaders()
        );

        ResponseEntity<ApiStandardResponse<TraineeProfileResponse>> response = restTemplate.exchange(
            BASE_URL + "/{username}",
            HttpMethod.PUT,
            requestEntity,
            new ParameterizedTypeReference<ApiStandardResponse<TraineeProfileResponse>>() {},
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
    void updateActivationStatus_ShouldDeactivateTrainee() {
        HttpEntity<UpdateTraineeActivation> requestEntity = new HttpEntity<>(
            new UpdateTraineeActivation(false), 
            createJsonHeaders()
        );

        ResponseEntity<Void> response = restTemplate.exchange(
            BASE_URL + "/{username}/activation",
            HttpMethod.PATCH,
            requestEntity,
            Void.class,
            createdTraineeUsername);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        ResponseEntity<ApiStandardResponse<TraineeProfileResponse>> getResponse = restTemplate.exchange(
            BASE_URL + "/{username}",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<ApiStandardResponse<TraineeProfileResponse>>() {},
            createdTraineeUsername);

        ApiStandardResponse<TraineeProfileResponse> apiResponse = getResponse.getBody();
        assertNotNull(apiResponse);
        assertFalse(apiResponse.data().active());
    }

    @Test
    @Order(5)
    void getTraineeTrainings_ShouldReturnTrainingList() {
        String url = BASE_URL + "/{username}/trainings?fromDate={fromDate}&toDate={toDate}" +
                    "&sortField={sortField}&sortDirection={sortDirection}";
        
        ResponseEntity<ApiStandardResponse<List<TraineeTrainingResponse>>> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<ApiStandardResponse<List<TraineeTrainingResponse>>>() {},
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
    @Order(6)
    void deleteProfile_ShouldRemoveTrainee() {
        restTemplate.delete(BASE_URL + "/{username}", createdTraineeUsername);

        ResponseEntity<ApiStandardError> response = restTemplate.exchange(
            BASE_URL + "/{username}",
            HttpMethod.GET,
            null,
            ApiStandardError.class,
            createdTraineeUsername);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(7)
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
    @Order(8)
    void getProfile_WithNonExistentUsername_ShouldReturnNotFound() {
        ResponseEntity<ApiStandardError> response = restTemplate.exchange(
            BASE_URL + "/nonexistentuser",
            HttpMethod.GET,
            null,
            ApiStandardError.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    private LoginResponse authenticate(String username, String password) {
        ResponseEntity<LoginResponse> response = restTemplate.postForEntity(
            "/api/v1/auth/login",
            new LoginRequest(username, password),
            LoginResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        return response.getBody();
    }

    private HttpHeaders createJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}