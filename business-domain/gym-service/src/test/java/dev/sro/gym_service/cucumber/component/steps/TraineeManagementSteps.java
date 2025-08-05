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

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TraineeManagementSteps extends CommonHttpSteps {

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

    @Given("the gym service is running")
    public void theGymServiceIsRunning() {
        assertNotNull(webTestClient);
    }

    @Given("I have a valid authentication token")
    public void iHaveAValidAuthenticationToken() {
        RegisterTraineeRequest request = testContext.createValidRegistrationRequest();

        sendPostRequest("/api/v1/trainees", request,
                new ParameterizedTypeReference<ApiStandardResponse<RegisterTraineeResponse>>() {
                },
                (status, responseBody) -> {
                    ApiStandardResponse<RegisterTraineeResponse> response = (ApiStandardResponse<RegisterTraineeResponse>) responseBody;
                    assertNotNull(response);
                    assertNotNull(response.data());

                    String username = response.data().username();
                    String password = response.data().plainPassword();
                    testContext.setCurrentCredentials(username, password);

                    LoginRequest loginRequest = new LoginRequest(username, password);
                    sendPostRequest("/api/v1/auth/login", loginRequest,
                            new ParameterizedTypeReference<ApiStandardResponse<LoginResponse>>() {
                            },
                            (loginStatus, loginResponseBody) -> {
                                ApiStandardResponse<LoginResponse> loginResponse = (ApiStandardResponse<LoginResponse>) loginResponseBody;
                                assertNotNull(loginResponse);
                                assertNotNull(loginResponse.data());
                                testContext.setAccessToken(loginResponse.data().token());
                            });
                });
    }

    @Given("I don't have a valid authentication token")
    public void iDontHaveAValidAuthenticationToken() {
        testContext.setAccessToken(null);
    }

    @Given("I want to register a new trainee with the following details:")
    public void iWantToRegisterANewTraineeWithTheFollowingDetails(DataTable dataTable) {
        List<Map<String, String>> dataList = dataTable.asMaps(String.class, String.class);
        Map<String, String> data = dataList.get(0);

        RegisterTraineeRequest request = new RegisterTraineeRequest(
                data.get("firstName"),
                data.get("lastName"),
                LocalDate.parse(data.get("dateOfBirth")),
                data.get("address"));

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
                        ? LocalDate.parse(data.get("dateOfBirth"))
                        : null,
                data.get("address"));

        testContext.setCurrentRegistrationRequest(request);
    }

    @Given("a trainee exists with username {string}")
    public void aTraineeExistsWithUsername(String username) {
        RegisterTraineeRequest request = new RegisterTraineeRequest(
                "Test",
                "User",
                LocalDate.of(1990, 1, 1),
                "Test Address");

        sendPostRequest("/api/v1/trainees", request,
                new ParameterizedTypeReference<ApiStandardResponse<RegisterTraineeResponse>>() {
                },
                (status, responseBody) -> {
                    ApiStandardResponse<RegisterTraineeResponse> response = (ApiStandardResponse<RegisterTraineeResponse>) responseBody;
                    assertNotNull(response);
                    testContext.setTestData("createdUsername", response.data().username());
                    testContext.setTestData("createdPassword", response.data().plainPassword());
                });
    }

    @Given("no trainee exists with username {string}")
    public void noTraineeExistsWithUsername(String username) {
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
                true);

        testContext.setCurrentUpdateRequest(request);
    }

    @Given("I want to update the trainee with valid details")
    public void iWantToUpdateTheTraineeWithValidDetails() {
        UpdateTraineeProfileRequest request = testContext.createValidUpdateRequest();
        testContext.setCurrentUpdateRequest(request);
    }

    @When("I send a POST request to {string}")
    public void iSendAPostRequestTo(String endpoint) {
        RegisterTraineeRequest request = testContext.getCurrentRegistrationRequest();
        assertNotNull(request, "Registration request should be set before making POST request");

        sendPostRequest(endpoint, request,
                new ParameterizedTypeReference<ApiStandardResponse<RegisterTraineeResponse>>() {
                },
                (status, responseBody) -> {
                    testContext.setLastResponseStatus(status);
                    testContext.setLastResponse((ApiStandardResponse<?>) responseBody);
                });
    }

    @When("I send a GET request to {string}")
    public void iSendAGetRequestTo(String endpoint) {
        String token = testContext.getAccessToken();

        sendGetRequest(endpoint, token, new ParameterizedTypeReference<ApiStandardResponse<TraineeProfileResponse>>() {
        },
                (status, responseBody) -> {
                    testContext.setLastResponseStatus(status);
                    testContext.setLastResponse((ApiStandardResponse<?>) responseBody);
                });
    }

    @When("I send a PUT request to {string}")
    public void iSendAPutRequestTo(String endpoint) {
        UpdateTraineeProfileRequest request = testContext.getCurrentUpdateRequest();
        assertNotNull(request, "Update request should be set before making PUT request");

        String token = testContext.getAccessToken();

        sendPutRequest(endpoint, request, token,
                new ParameterizedTypeReference<ApiStandardResponse<TraineeProfileResponse>>() {
                },
                (status, responseBody) -> {
                    testContext.setLastResponseStatus(status);
                    testContext.setLastResponse((ApiStandardResponse<?>) responseBody);
                });
    }

    @When("I send a DELETE request to {string}")
    public void iSendADeleteRequestTo(String endpoint) {
        String token = testContext.getAccessToken();

        sendDeleteRequest(endpoint, token, new ParameterizedTypeReference<ApiStandardResponse<Void>>() {
        },
                (status, responseBody) -> {
                    testContext.setLastResponseStatus(status);
                    testContext.setLastResponse((ApiStandardResponse<?>) responseBody);
                });
    }

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