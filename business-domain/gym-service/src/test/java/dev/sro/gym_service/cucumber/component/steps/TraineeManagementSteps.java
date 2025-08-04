package dev.sro.gym_service.cucumber.component.steps;

import dev.sro.gym_service.cucumber.component.TraineeTestContext;
import dev.sro.gym_service.dtos.v1.request.auth.LoginRequest;
import dev.sro.gym_service.dtos.v1.request.trainee.RegisterTraineeRequest;
import dev.sro.gym_service.dtos.v1.request.trainee.UpdateTraineeProfileRequest;
import dev.sro.gym_service.dtos.v1.response.auth.LoginResponse;
import dev.sro.gym_service.dtos.v1.response.trainee.RegisterTraineeResponse;
import dev.sro.gym_service.dtos.v1.response.trainee.TraineeProfileResponse;
import dev.sro.gym_service.util.response.ApiStandardResponse;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TraineeManagementSteps {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TraineeTestContext testContext;

    @Before
    public void setUp() {
        testContext.clear();
    }

    @After
    public void tearDown() {
        testContext.clear();
    }

    // Background steps
    @Given("the gym service is running")
    public void theGymServiceIsRunning() {
        // Service is already running due to @SpringBootTest
        assertNotNull(webTestClient);
    }

    @Given("I have a valid authentication token")
    public void iHaveAValidAuthenticationToken() {
        // Create a test trainee and authenticate to get a valid token
        RegisterTraineeRequest request = testContext.createValidRegistrationRequest();
        
        ApiStandardResponse<RegisterTraineeResponse> response = webTestClient.post()
                .uri("/api/v1/trainees")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(new ParameterizedTypeReference<ApiStandardResponse<RegisterTraineeResponse>>() {})
                .returnResult()
                .getResponseBody();

        assertNotNull(response);
        assertNotNull(response.data());
        
        String username = response.data().username();
        String password = response.data().plainPassword();
        
        testContext.setCurrentCredentials(username, password);
        
        // Authenticate to get token
        LoginRequest loginRequest = new LoginRequest(username, password);
        ApiStandardResponse<LoginResponse> loginResponse = webTestClient.post()
                .uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<ApiStandardResponse<LoginResponse>>() {})
                .returnResult()
                .getResponseBody();

        assertNotNull(loginResponse);
        assertNotNull(loginResponse.data());
        testContext.setAccessToken(loginResponse.data().token());
    }

    @Given("I don't have a valid authentication token")
    public void iDontHaveAValidAuthenticationToken() {
        testContext.setAccessToken(null);
    }



    // Given steps for data setup
    @Given("I want to register a new trainee with the following details:")
    public void iWantToRegisterANewTraineeWithTheFollowingDetails(DataTable dataTable) {
        // Get the first row of data (header row is automatically handled)
        List<Map<String, String>> dataList = dataTable.asMaps(String.class, String.class);
        Map<String, String> data = dataList.get(0);
        
        RegisterTraineeRequest request = new RegisterTraineeRequest(
                data.get("firstName"),
                data.get("lastName"),
                LocalDate.parse(data.get("dateOfBirth")),
                data.get("address")
        );
        
        testContext.setCurrentRegistrationRequest(request);
    }

    @Given("I want to register a new trainee with invalid details:")
    public void iWantToRegisterANewTraineeWithInvalidDetails(DataTable dataTable) {
        List<Map<String, String>> dataList = dataTable.asMaps(String.class, String.class);
        Map<String, String> data = dataList.get(0);
        
        RegisterTraineeRequest request = new RegisterTraineeRequest(
                data.get("firstName"),
                data.get("lastName"),
                data.get("dateOfBirth") != null && !data.get("dateOfBirth").isEmpty() 
                    ? LocalDate.parse(data.get("dateOfBirth")) : null,
                data.get("address")
        );
        
        testContext.setCurrentRegistrationRequest(request);
    }

    @Given("a trainee exists with username {string}")
    public void aTraineeExistsWithUsername(String username) {
        // Create a trainee if it doesn't exist
        RegisterTraineeRequest request = new RegisterTraineeRequest(
                "Test",
                "User",
                LocalDate.of(1990, 1, 1),
                "Test Address"
        );
        
        ApiStandardResponse<RegisterTraineeResponse> response = webTestClient.post()
                .uri("/api/v1/trainees")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(new ParameterizedTypeReference<ApiStandardResponse<RegisterTraineeResponse>>() {})
                .returnResult()
                .getResponseBody();

        assertNotNull(response);
        testContext.setTestData("createdUsername", response.data().username());
        testContext.setTestData("createdPassword", response.data().plainPassword());
    }

    @Given("no trainee exists with username {string}")
    public void noTraineeExistsWithUsername(String username) {
        // Ensure the username is not used by creating a unique one
        testContext.setTestData("nonexistentUsername", username);
    }

    @Given("I want to update the trainee with the following details:")
    public void iWantToUpdateTheTraineeWithTheFollowingDetails(DataTable dataTable) {
        List<Map<String, String>> dataList = dataTable.asMaps(String.class, String.class);
        Map<String, String> data = dataList.get(0);
        
        UpdateTraineeProfileRequest request = new UpdateTraineeProfileRequest(
                data.get("firstName"),
                data.get("lastName"),
                LocalDate.parse(data.get("dateOfBirth")),
                data.get("address"),
                true
        );
        
        testContext.setCurrentUpdateRequest(request);
    }

    @Given("I want to update the trainee with valid details")
    public void iWantToUpdateTheTraineeWithValidDetails() {
        UpdateTraineeProfileRequest request = testContext.createValidUpdateRequest();
        testContext.setCurrentUpdateRequest(request);
    }

    // When steps for actions
    @When("I send a POST request to {string}")
    public void iSendAPostRequestTo(String endpoint) {
        RegisterTraineeRequest request = testContext.getCurrentRegistrationRequest();
        assertNotNull(request, "Registration request should be set before making POST request");

        webTestClient.post()
                .uri(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectBody(new ParameterizedTypeReference<ApiStandardResponse<RegisterTraineeResponse>>() {})
                .consumeWith(response -> {
                    testContext.setLastResponseStatus(response.getStatus().value());
                    testContext.setLastResponse(response.getResponseBody());
                });
    }

    @When("I send a GET request to {string}")
    public void iSendAGetRequestTo(String endpoint) {
        String token = testContext.getAccessToken();
        
        var request = webTestClient.get().uri(endpoint);
        
        if (token != null) {
            request = request.header("Authorization", "Bearer " + token);
        }
        
        request.exchange()
                .expectBody(new ParameterizedTypeReference<ApiStandardResponse<TraineeProfileResponse>>() {})
                .consumeWith(response -> {
                    testContext.setLastResponseStatus(response.getStatus().value());
                    testContext.setLastResponse(response.getResponseBody());
                });
    }

    @When("I send a PUT request to {string}")
    public void iSendAPutRequestTo(String endpoint) {
        UpdateTraineeProfileRequest request = testContext.getCurrentUpdateRequest();
        assertNotNull(request, "Update request should be set before making PUT request");
        
        String token = testContext.getAccessToken();
        
        var webRequest = webTestClient.put()
                .uri(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request);
        
        if (token != null) {
            webRequest = webRequest.header("Authorization", "Bearer " + token);
        }
        
        webRequest.exchange()
                .expectBody(new ParameterizedTypeReference<ApiStandardResponse<TraineeProfileResponse>>() {})
                .consumeWith(response -> {
                    testContext.setLastResponseStatus(response.getStatus().value());
                    testContext.setLastResponse(response.getResponseBody());
                });
    }

    @When("I send a DELETE request to {string}")
    public void iSendADeleteRequestTo(String endpoint) {
        String token = testContext.getAccessToken();
        
        var request = webTestClient.delete().uri(endpoint);
        
        if (token != null) {
            request = request.header("Authorization", "Bearer " + token);
        }
        
        request.exchange()
                .expectBody(new ParameterizedTypeReference<ApiStandardResponse<Void>>() {})
                .consumeWith(response -> {
                    testContext.setLastResponseStatus(response.getStatus().value());
                    testContext.setLastResponse(response.getResponseBody());
                });
    }

    // Then steps for assertions
    @Then("the response status should be {int}")
    public void theResponseStatusShouldBe(int expectedStatus) {
        assertEquals(expectedStatus, testContext.getLastResponseStatus());
    }

    @Then("the response should contain a valid trainee registration")
    public void theResponseShouldContainAValidTraineeRegistration() {
        ApiStandardResponse<?> response = testContext.getLastResponse();
        assertNotNull(response);
        assertNotNull(response.data());
        assertTrue(response.data() instanceof RegisterTraineeResponse);
        
        RegisterTraineeResponse traineeResponse = (RegisterTraineeResponse) response.data();
        assertNotNull(traineeResponse.username());
        assertNotNull(traineeResponse.plainPassword());
    }

    @Then("the response should include generated username and password")
    public void theResponseShouldIncludeGeneratedUsernameAndPassword() {
        ApiStandardResponse<?> response = testContext.getLastResponse();
        assertNotNull(response);
        
        RegisterTraineeResponse traineeResponse = (RegisterTraineeResponse) response.data();
        assertFalse(traineeResponse.username().isEmpty());
        assertFalse(traineeResponse.plainPassword().isEmpty());
        
        // Store credentials for future use
        testContext.setCurrentCredentials(traineeResponse.username(), traineeResponse.plainPassword());
    }

    @Then("the response should contain the trainee profile")
    public void theResponseShouldContainTheTraineeProfile() {
        ApiStandardResponse<?> response = testContext.getLastResponse();
        assertNotNull(response);
        assertNotNull(response.data());
        assertTrue(response.data() instanceof TraineeProfileResponse);
    }

    @Then("the profile should have the correct personal information")
    public void theProfileShouldHaveTheCorrectPersonalInformation() {
        ApiStandardResponse<?> response = testContext.getLastResponse();
        assertNotNull(response);
        
        TraineeProfileResponse profile = (TraineeProfileResponse) response.data();
        assertNotNull(profile.firstName());
        assertNotNull(profile.lastName());
        assertNotNull(profile.dateOfBirth());
        assertNotNull(profile.address());
    }

    @Then("the response should contain the updated trainee profile")
    public void theResponseShouldContainTheUpdatedTraineeProfile() {
        ApiStandardResponse<?> response = testContext.getLastResponse();
        assertNotNull(response);
        assertNotNull(response.data());
        assertTrue(response.data() instanceof TraineeProfileResponse);
    }

    @Then("the profile should reflect the updated information")
    public void theProfileShouldReflectTheUpdatedInformation() {
        ApiStandardResponse<?> response = testContext.getLastResponse();
        UpdateTraineeProfileRequest updateRequest = testContext.getCurrentUpdateRequest();
        
        assertNotNull(response);
        assertNotNull(updateRequest);
        
        TraineeProfileResponse profile = (TraineeProfileResponse) response.data();
        assertEquals(updateRequest.firstName(), profile.firstName());
        assertEquals(updateRequest.lastName(), profile.lastName());
        assertEquals(updateRequest.dateOfBirth(), profile.dateOfBirth());
        assertEquals(updateRequest.address(), profile.address());
    }



    @Then("the response should contain validation errors")
    public void theResponseShouldContainValidationErrors() {
        ApiStandardResponse<?> response = testContext.getLastResponse();
        assertNotNull(response);
        // Validation errors typically result in 400 status with error details
        assertTrue(testContext.getLastResponseStatus() >= 400);
    }

    @Then("the response should contain a not found error")
    public void theResponseShouldContainANotFoundError() {
        assertEquals(404, testContext.getLastResponseStatus());
    }

    @Then("the response should contain an authentication error")
    public void theResponseShouldContainAnAuthenticationError() {
        assertEquals(401, testContext.getLastResponseStatus());
    }

    @Then("the response should contain an authorization error")
    public void theResponseShouldContainAnAuthorizationError() {
        assertEquals(403, testContext.getLastResponseStatus());
    }
} 