package com.sro.SpringCoreTask1.controller.trainee;

import com.sro.SpringCoreTask1.dtos.v1.request.auth.LoginRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.trainee.*;
import com.sro.SpringCoreTask1.dtos.v1.request.training.TraineeTrainingResponse;
import com.sro.SpringCoreTask1.dtos.v1.response.auth.LoginResponse;
import com.sro.SpringCoreTask1.dtos.v1.response.trainee.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import java.time.LocalDate;
import java.util.Optional;
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

        ResponseEntity<RegisterTraineeResponse> response = restTemplate.postForEntity(
            BASE_URL, 
            request, 
            RegisterTraineeResponse.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

        RegisterTraineeResponse responseBody = response.getBody();
        assertNotNull(responseBody);
        assertNotNull(responseBody.username());
        assertNotNull(responseBody.password());
        
        createdTraineeUsername = responseBody.username();
        createdTraineePassword = responseBody.password();
        authenticate(createdTraineeUsername, createdTraineePassword);
    }

    @Test
    @Order(2)
    void getProfile_ShouldReturnTraineeProfile() {
        ResponseEntity<TraineeProfileResponse> response = restTemplate.getForEntity(
            BASE_URL + "/{username}", 
            TraineeProfileResponse.class, 
            createdTraineeUsername);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        TraineeProfileResponse profile = response.getBody();
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
            createdTraineeUsername,
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

        ResponseEntity<TraineeProfileResponse> response = restTemplate.exchange(
            BASE_URL + "/{username}",
            HttpMethod.PUT,
            requestEntity,
            TraineeProfileResponse.class,
            createdTraineeUsername);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        TraineeProfileResponse updatedProfile = response.getBody();
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

        ResponseEntity<TraineeProfileResponse> getResponse = restTemplate.getForEntity(
            BASE_URL + "/{username}",
            TraineeProfileResponse.class,
            createdTraineeUsername);

        TraineeProfileResponse profile = Optional.ofNullable(getResponse.getBody())
            .orElseThrow(() -> new AssertionError("Response body should not be null"));
        assertFalse(profile.active());
    }

    @Test
    @Order(5)
    void getTraineeTrainings_ShouldReturnTrainingList() {
        String url = BASE_URL + "/{username}/trainings?fromDate={fromDate}&toDate={toDate}" +
                    "&sortField={sortField}&sortDirection={sortDirection}";
        
        ResponseEntity<TraineeTrainingResponse[]> response = restTemplate.getForEntity(
            url,
            TraineeTrainingResponse[].class,
            createdTraineeUsername,
            "2023-01-01",
            "2023-12-31",
            "trainingDate",
            "DESC");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @Order(6)
    void deleteProfile_ShouldRemoveTrainee() {
        restTemplate.delete(BASE_URL + "/{username}", createdTraineeUsername);

        ResponseEntity<Void> response = restTemplate.getForEntity(
            BASE_URL + "/{username}",
            Void.class,
            createdTraineeUsername);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(7)
    void registerTrainee_WithInvalidData_ShouldReturnBadRequest() {
        ResponseEntity<String> response = restTemplate.postForEntity(
            BASE_URL,
            new RegisterTraineeRequest("", "", null, ""),
            String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Order(8)
    void getProfile_WithNonExistentUsername_ShouldReturnNotFound() {
        ResponseEntity<Void> response = restTemplate.getForEntity(
            BASE_URL + "/nonexistentuser",
            Void.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
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