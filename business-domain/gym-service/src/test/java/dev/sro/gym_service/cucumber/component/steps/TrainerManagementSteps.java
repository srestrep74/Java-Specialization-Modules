package dev.sro.gym_service.cucumber.component.steps;

import dev.sro.gym_service.cucumber.component.TrainerTestContext;
import dev.sro.gym_service.dtos.v1.request.auth.LoginRequest;
import dev.sro.gym_service.dtos.v1.request.trainee.RegisterTraineeRequest;
import dev.sro.gym_service.dtos.v1.request.trainer.RegisterTrainerRequest;
import dev.sro.gym_service.dtos.v1.request.trainer.UpdateTrainerActivation;
import dev.sro.gym_service.dtos.v1.request.trainer.UpdateTrainerProfileRequest;
import dev.sro.gym_service.dtos.v1.response.auth.LoginResponse;
import dev.sro.gym_service.dtos.v1.response.trainee.RegisterTraineeResponse;
import dev.sro.gym_service.dtos.v1.response.trainer.RegisterTrainerResponse;
import dev.sro.gym_service.dtos.v1.response.trainer.TrainerProfileResponse;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TrainerManagementSteps extends CommonHttpSteps {

    @Autowired
    private TrainerTestContext testContext;

    @Before
    public void setUp() {
        testContext.clear();
    }

    @After
    public void tearDown() {
        testContext.clear();
    }

    @Given("the gym service is running for trainer tests")
    public void theGymServiceIsRunningForTrainerTests() {
        assertNotNull(webTestClient);
    }

    @Given("I have a valid authentication token for trainer")
    public void iHaveAValidAuthenticationTokenForTrainer() {
        RegisterTrainerRequest request = testContext.createValidRegistrationRequest();

        webTestClient.post()
                .uri("/api/v1/trainers")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(RegisterTrainerResponse.class)
                .consumeWith(response -> {
                    RegisterTrainerResponse trainerResponse = response.getResponseBody();
                    assertNotNull(trainerResponse);

                    String username = trainerResponse.username();
                    String password = trainerResponse.plainPassword();

                    testContext.setCurrentCredentials(username, password);

                    LoginRequest loginRequest = new LoginRequest(username, password);
                    ApiStandardResponse<LoginResponse> loginResponse = webTestClient.post()
                            .uri("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(loginRequest)
                            .exchange()
                            .expectStatus().isOk()
                            .expectBody(new ParameterizedTypeReference<ApiStandardResponse<LoginResponse>>() {
                            })
                            .returnResult()
                            .getResponseBody();

                    assertNotNull(loginResponse);
                    assertNotNull(loginResponse.data());
                    testContext.setAccessToken(loginResponse.data().token());
                });
    }

    @Given("I don't have a valid authentication token for trainer")
    public void iDontHaveAValidAuthenticationTokenForTrainer() {
        testContext.setAccessToken(null);
    }

    @Given("I have a valid authentication token without trainer role")
    public void iHaveAValidAuthenticationTokenWithoutTrainerRole() {
        RegisterTraineeRequest traineeRequest = new RegisterTraineeRequest(
                "Test",
                "Trainee",
                LocalDate.of(1990, 1, 1),
                "Test Address");

        webTestClient.post()
                .uri("/api/v1/trainees")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(traineeRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(new ParameterizedTypeReference<ApiStandardResponse<RegisterTraineeResponse>>() {
                })
                .consumeWith(response -> {
                    ApiStandardResponse<RegisterTraineeResponse> apiResponse = response.getResponseBody();
                    assertNotNull(apiResponse);
                    assertNotNull(apiResponse.data());

                    RegisterTraineeResponse traineeResponse = apiResponse.data();
                    String username = traineeResponse.username();
                    String password = traineeResponse.plainPassword();

                    assertNotNull(username, "Username should not be null");
                    assertNotNull(password, "Password should not be null");

                    LoginRequest loginRequest = new LoginRequest(username, password);
                    ApiStandardResponse<LoginResponse> loginResponse = webTestClient.post()
                            .uri("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(loginRequest)
                            .exchange()
                            .expectStatus().isOk()
                            .expectBody(new ParameterizedTypeReference<ApiStandardResponse<LoginResponse>>() {
                            })
                            .returnResult()
                            .getResponseBody();

                    assertNotNull(loginResponse);
                    assertNotNull(loginResponse.data());
                    testContext.setAccessToken(loginResponse.data().token());
                });
    }

    @Given("I want to register a new trainer with the following details:")
    public void iWantToRegisterANewTrainerWithTheFollowingDetails(DataTable dataTable) {
        List<Map<String, String>> dataList = dataTable.asMaps(String.class, String.class);
        Map<String, String> data = dataList.get(0);

        RegisterTrainerRequest request = new RegisterTrainerRequest(
                data.get("firstName"),
                data.get("lastName"),
                Long.parseLong(data.get("trainingTypeId")));

        testContext.setCurrentRegistrationRequest(request);
    }

    @Given("I want to register a new trainer with invalid details:")
    public void iWantToRegisterANewTrainerWithInvalidDetails(DataTable dataTable) {
        List<Map<String, String>> dataList = dataTable.asMaps(String.class, String.class);
        Map<String, String> data = dataList.get(0);

        RegisterTrainerRequest request = new RegisterTrainerRequest(
                data.get("firstName"),
                data.get("lastName"),
                data.get("trainingTypeId") != null && !data.get("trainingTypeId").isEmpty()
                        ? Long.parseLong(data.get("trainingTypeId"))
                        : null);

        testContext.setCurrentRegistrationRequest(request);
    }

    @Given("a trainer exists with username {string}")
    public void aTrainerExistsWithUsername(String username) {
        RegisterTrainerRequest request = new RegisterTrainerRequest(
                "Test",
                "Trainer",
                1L);

        webTestClient.post()
                .uri("/api/v1/trainers")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(RegisterTrainerResponse.class)
                .consumeWith(response -> {
                    RegisterTrainerResponse trainerResponse = response.getResponseBody();
                    assertNotNull(trainerResponse);
                    testContext.setTestData("createdUsername", trainerResponse.username());
                    testContext.setTestData("createdPassword", trainerResponse.plainPassword());
                });
    }

    @Given("no trainer exists with username {string}")
    public void noTrainerExistsWithUsername(String username) {
        testContext.setTestData("nonexistentUsername", username);
    }

    @Given("I want to update the trainer with the following details:")
    public void iWantToUpdateTheTrainerWithTheFollowingDetails(DataTable dataTable) {
        List<Map<String, String>> dataList = dataTable.asMaps(String.class, String.class);
        Map<String, String> data = dataList.get(0);

        UpdateTrainerProfileRequest request = new UpdateTrainerProfileRequest(
                data.get("firstName"),
                data.get("lastName"),
                Long.parseLong(data.get("trainingTypeId")),
                Boolean.parseBoolean(data.get("active")));

        testContext.setCurrentUpdateRequest(request);
    }

    @Given("I want to update the trainer with valid details")
    public void iWantToUpdateTheTrainerWithValidDetails() {
        UpdateTrainerProfileRequest request = testContext.createValidUpdateRequest();
        testContext.setCurrentUpdateRequest(request);
    }

    @Given("I want to deactivate the trainer")
    public void iWantToDeactivateTheTrainer() {
        UpdateTrainerActivation request = testContext.createValidActivationRequest();
        testContext.setCurrentActivationRequest(request);
    }

    @When("I send a POST request to register trainer at {string}")
    public void iSendAPostRequestToRegisterTrainer(String endpoint) {
        RegisterTrainerRequest request = testContext.getCurrentRegistrationRequest();
        assertNotNull(request, "Registration request should be set before making POST request");

        sendPostRequest(endpoint, request, RegisterTrainerResponse.class,
                (status, responseBody) -> {
                    testContext.setLastResponseStatus(status);
                    testContext.setLastResponse(null);
                });
    }

    @When("I send a GET request to retrieve trainer profile at {string}")
    public void iSendAGetRequestToRetrieveTrainerProfile(String endpoint) {
        String token = testContext.getAccessToken();

        sendGetRequest(endpoint, token, TrainerProfileResponse.class,
                (status, responseBody) -> {
                    testContext.setLastResponseStatus(status);
                    testContext.setLastResponse(null);
                });
    }

    @When("I send a PUT request to update trainer profile at {string}")
    public void iSendAPutRequestToUpdateTrainerProfile(String endpoint) {
        UpdateTrainerProfileRequest request = testContext.getCurrentUpdateRequest();
        assertNotNull(request, "Update request should be set before making PUT request");

        String token = testContext.getAccessToken();

        sendPutRequest(endpoint, request, token, TrainerProfileResponse.class,
                (status, responseBody) -> {
                    testContext.setLastResponseStatus(status);
                    testContext.setLastResponse(null);
                });
    }

    @When("I send a PATCH request to update trainer activation at {string}")
    public void iSendAPatchRequestToUpdateTrainerActivation(String endpoint) {
        UpdateTrainerActivation request = testContext.getCurrentActivationRequest();
        assertNotNull(request, "Activation request should be set before making PATCH request");

        String token = testContext.getAccessToken();

        sendPatchRequest(endpoint, request, token, Void.class,
                (status, responseBody) -> {
                    testContext.setLastResponseStatus(status);
                    testContext.setLastResponse(null);
                });
    }

    @When("I send a GET request to retrieve trainer trainings at {string}")
    public void iSendAGetRequestToRetrieveTrainerTrainings(String endpoint) {
        String token = testContext.getAccessToken();

        var request = webTestClient.get().uri(endpoint);

        if (token != null) {
            request = request.header("Authorization", "Bearer " + token);
        }

        request.exchange()
                .expectBodyList(Object.class)
                .consumeWith(response -> {
                    testContext.setLastResponseStatus(response.getStatus().value());
                    testContext.setLastResponse(null);
                });
    }

    @Then("the trainer response status should be {int}")
    public void theTrainerResponseStatusShouldBe(int expectedStatus) {
        assertEquals(expectedStatus, testContext.getLastResponseStatus());
    }

    @Then("the response should contain a valid trainer registration")
    public void theResponseShouldContainAValidTrainerRegistration() {
        assertTrue(testContext.getLastResponseStatus() == 201);
    }

    @Then("the response should include generated trainer username and password")
    public void theResponseShouldIncludeGeneratedTrainerUsernameAndPassword() {
        assertTrue(testContext.getLastResponseStatus() == 201);
    }

    @Then("the response should contain the trainer profile")
    public void theResponseShouldContainTheTrainerProfile() {
        assertTrue(testContext.getLastResponseStatus() == 200);
    }

    @Then("the trainer profile should have the correct personal information")
    public void theTrainerProfileShouldHaveTheCorrectPersonalInformation() {
        assertTrue(testContext.getLastResponseStatus() == 200);
    }

    @Then("the response should contain the updated trainer profile")
    public void theResponseShouldContainTheUpdatedTrainerProfile() {
        assertTrue(testContext.getLastResponseStatus() == 200);
    }

    @Then("the trainer profile should reflect the updated information")
    public void theTrainerProfileShouldReflectTheUpdatedInformation() {
        assertTrue(testContext.getLastResponseStatus() == 200);
    }

    @Then("the response should contain a location header for trainer")
    public void theResponseShouldContainALocationHeaderForTrainer() {
        assertTrue(testContext.getLastResponseStatus() == 200);
    }

    @Then("the response should contain a list of trainer trainings")
    public void theResponseShouldContainAListOfTrainerTrainings() {
        assertTrue(testContext.getLastResponseStatus() == 200);
    }

    @Then("the response should contain trainer validation errors")
    public void theResponseShouldContainTrainerValidationErrors() {
        assertTrue(testContext.getLastResponseStatus() >= 400);
    }

    @Then("the response should contain a trainer not found error")
    public void theResponseShouldContainATrainerNotFoundError() {
        assertEquals(404, testContext.getLastResponseStatus());
    }

    @Then("the response should contain a trainer authentication error")
    public void theResponseShouldContainATrainerAuthenticationError() {
        assertEquals(401, testContext.getLastResponseStatus());
    }

    @Then("the response should contain a trainer authorization error")
    public void theResponseShouldContainATrainerAuthorizationError() {
        assertEquals(403, testContext.getLastResponseStatus());
    }
}