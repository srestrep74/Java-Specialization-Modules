package dev.sro.workload_service.cucumber.component.steps;

import dev.sro.workload_service.cucumber.component.WorkloadTestContext;
import dev.sro.workload_service.cucumber.component.config.properties.TestProperties;
import dev.sro.workload_service.dtos.v1.request.TrainerWorkloadRequest;
import dev.sro.workload_service.dtos.v1.response.TrainerMonthlySummaryResponse;
import dev.sro.workload_service.dtos.v1.response.TrainerWorkloadResponse;
import dev.sro.workload_service.entity.enums.ActionType;
import dev.sro.workload_service.util.response.ApiStandardResponse;
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

public class WorkloadManagementSteps extends CommonHttpSteps {

    @Autowired
    private WorkloadTestContext testContext;

    @Autowired
    private TestProperties testProperties;

    @Before
    public void setUp() {
        testContext.clear();
    }

    @After
    public void tearDown() {
        testContext.clear();
    }

    @Given("the workload service is running for workload tests")
    public void theWorkloadServiceIsRunningForWorkloadTests() {
        assertNotNull(webTestClient);
    }

    @Given("I have a valid authentication token for workload")
    public void iHaveAValidAuthenticationTokenForWorkload() {
        String internalToken = testProperties.auth().internalToken();
        testContext.setAccessToken(internalToken);
    }

    @Given("I don't have a valid authentication token for workload")
    public void iDontHaveAValidAuthenticationTokenForWorkload() {
        testContext.setAccessToken(null);
    }

    @Given("I want to process a trainer workload with the following details:")
    public void iWantToProcessATrainerWorkloadWithTheFollowingDetails(DataTable dataTable) {
        List<Map<String, String>> dataList = dataTable.asMaps(String.class, String.class);
        Map<String, String> data = dataList.get(0);

        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                data.get("trainerUsername"),
                data.get("firstName"),
                data.get("lastName"),
                Boolean.parseBoolean(data.get("isActive")),
                LocalDate.parse(data.get("trainingDate")),
                Integer.parseInt(data.get("duration")),
                ActionType.valueOf(data.get("actionType")));

        testContext.setCurrentWorkloadRequest(request);
    }

    @Given("I want to process a trainer workload with invalid details:")
    public void iWantToProcessATrainerWorkloadWithInvalidDetails(DataTable dataTable) {
        List<Map<String, String>> dataList = dataTable.asMaps(String.class, String.class);
        Map<String, String> data = dataList.get(0);

        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                data.get("trainerUsername"),
                data.get("firstName"),
                data.get("lastName"),
                data.get("isActive") != null ? Boolean.parseBoolean(data.get("isActive")) : null,
                data.get("trainingDate") != null && !data.get("trainingDate").isEmpty()
                        ? LocalDate.parse(data.get("trainingDate"))
                        : null,
                data.get("duration") != null && !data.get("duration").isEmpty()
                        ? Integer.parseInt(data.get("duration"))
                        : 0,
                data.get("actionType") != null ? ActionType.valueOf(data.get("actionType")) : null);

        testContext.setCurrentWorkloadRequest(request);
    }

    @Given("a trainer workload exists for username {string}")
    public void aTrainerWorkloadExistsForUsername(String username) {
        testContext.setTestData("trainerUsername", username);

        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                username,
                "Test",
                "Trainer",
                true,
                LocalDate.now(),
                60,
                ActionType.ADD);

        testContext.setCurrentWorkloadRequest(request);
    }

    @When("I send a POST request to process trainer workload at {string}")
    public void iSendAPostRequestToProcessTrainerWorkload(String endpoint) {
        TrainerWorkloadRequest request = testContext.getCurrentWorkloadRequest();
        assertNotNull(request, "Workload request should be set before making POST request");

        String token = testContext.getAccessToken();

        sendPostRequest(endpoint, request, token,
                new ParameterizedTypeReference<ApiStandardResponse<TrainerWorkloadResponse>>() {
                },
                (status, responseBody) -> {
                    testContext.setLastResponseStatus(status);
                    if (responseBody instanceof ApiStandardResponse) {
                        ApiStandardResponse<TrainerWorkloadResponse> apiResponse = (ApiStandardResponse<TrainerWorkloadResponse>) responseBody;
                        testContext.setLastResponse(apiResponse.data());
                    }
                });
    }

    @When("I send a GET request to retrieve trainer monthly summary at {string}")
    public void iSendAGetRequestToRetrieveTrainerMonthlySummary(String endpoint) {
        String token = testContext.getAccessToken();
        String username = (String) testContext.getTestData("trainerUsername");

        if (username == null) {
            username = "test.trainer";
        }

        String fullEndpoint = endpoint.replace("{username}", username);

        sendGetRequest(fullEndpoint, token,
                new ParameterizedTypeReference<ApiStandardResponse<TrainerMonthlySummaryResponse>>() {
                },
                (status, responseBody) -> {
                    testContext.setLastResponseStatus(status);
                    if (responseBody instanceof ApiStandardResponse) {
                        ApiStandardResponse<TrainerMonthlySummaryResponse> apiResponse = (ApiStandardResponse<TrainerMonthlySummaryResponse>) responseBody;
                        testContext.setTestData("monthlySummary", apiResponse.data());
                    }
                });
    }

    @When("I send a GET request to retrieve trainer monthly summary by year at {string}")
    public void iSendAGetRequestToRetrieveTrainerMonthlySummaryByYear(String endpoint) {
        String token = testContext.getAccessToken();
        String username = (String) testContext.getTestData("trainerUsername");

        if (username == null) {
            username = "test.trainer";
        }

        String fullEndpoint = endpoint.replace("{username}", username).replace("{year}", "2024");

        sendGetRequest(fullEndpoint, token,
                new ParameterizedTypeReference<ApiStandardResponse<TrainerMonthlySummaryResponse>>() {
                },
                (status, responseBody) -> {
                    testContext.setLastResponseStatus(status);
                    if (responseBody instanceof ApiStandardResponse) {
                        ApiStandardResponse<TrainerMonthlySummaryResponse> apiResponse = (ApiStandardResponse<TrainerMonthlySummaryResponse>) responseBody;
                        testContext.setTestData("monthlySummary", apiResponse.data());
                    }
                });
    }

    @When("I send a GET request to retrieve trainer monthly summary by month at {string}")
    public void iSendAGetRequestToRetrieveTrainerMonthlySummaryByMonth(String endpoint) {
        String token = testContext.getAccessToken();
        String username = (String) testContext.getTestData("trainerUsername");

        if (username == null) {
            username = "test.trainer";
        }

        String fullEndpoint = endpoint.replace("{username}", username).replace("{year}", "2024").replace("{month}",
                "7");

        sendGetRequest(fullEndpoint, token,
                new ParameterizedTypeReference<ApiStandardResponse<TrainerMonthlySummaryResponse>>() {
                },
                (status, responseBody) -> {
                    testContext.setLastResponseStatus(status);
                    if (responseBody instanceof ApiStandardResponse) {
                        ApiStandardResponse<TrainerMonthlySummaryResponse> apiResponse = (ApiStandardResponse<TrainerMonthlySummaryResponse>) responseBody;
                        testContext.setTestData("monthlySummary", apiResponse.data());
                    }
                });
    }

    @Then("the workload response status should be {int}")
    public void theWorkloadResponseStatusShouldBe(int expectedStatus) {
        assertEquals(expectedStatus, testContext.getLastResponseStatus());
    }

    @Then("the response should contain a valid workload processing")
    public void theResponseShouldContainAValidWorkloadProcessing() {
        assertTrue(testContext.getLastResponseStatus() == 200);
        assertNotNull(testContext.getLastResponse());
    }

    @Then("the response should contain a valid monthly summary")
    public void theResponseShouldContainAValidMonthlySummary() {
        assertTrue(testContext.getLastResponseStatus() == 200);
        assertNotNull(testContext.getTestData("monthlySummary"));
    }

    @Then("the response should contain workload validation errors")
    public void theResponseShouldContainWorkloadValidationErrors() {
        assertTrue(testContext.getLastResponseStatus() >= 400);
    }

    @Then("the response should contain a workload not found error")
    public void theResponseShouldContainAWorkloadNotFoundError() {
        assertEquals(404, testContext.getLastResponseStatus());
    }

    @Then("the response should contain a workload authentication error")
    public void theResponseShouldContainAWorkloadAuthenticationError() {
        assertEquals(401, testContext.getLastResponseStatus());
    }

    @Then("the response should contain a workload authorization error")
    public void theResponseShouldContainAWorkloadAuthorizationError() {
        assertEquals(403, testContext.getLastResponseStatus());
    }
}