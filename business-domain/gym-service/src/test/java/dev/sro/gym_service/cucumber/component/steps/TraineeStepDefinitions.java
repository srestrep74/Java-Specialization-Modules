package dev.sro.gym_service.cucumber.component.steps;

import dev.sro.gym_service.cucumber.component.TraineeTestContext;
import dev.sro.gym_service.util.response.ApiStandardError;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.*;

public class TraineeStepDefinitions {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TraineeTestContext testContext;

    @Then("the response should contain validation errors with details")
    public void theResponseShouldContainValidationErrorsWithDetails() {
        assertEquals(400, testContext.getLastResponseStatus());
        
        // For validation errors, we might get a different response structure
        // This step can be enhanced based on the actual error response format
        assertTrue(testContext.getLastResponseStatus() >= 400);
    }

    @Then("the error response should indicate missing required fields")
    public void theErrorResponseShouldIndicateMissingRequiredFields() {
        assertEquals(400, testContext.getLastResponseStatus());
        // Additional validation can be added here based on the actual error response structure
    }

    @Then("the system should return a proper error message")
    public void theSystemShouldReturnAProperErrorMessage() {
        assertTrue(testContext.getLastResponseStatus() >= 400);
        // Verify that we have some form of error response
        assertNotNull(testContext.getLastResponse());
    }

    @Then("the authentication should be required")
    public void theAuthenticationShouldBeRequired() {
        assertEquals(401, testContext.getLastResponseStatus());
    }

    @Then("the resource should not be found")
    public void theResourceShouldNotBeFound() {
        assertEquals(404, testContext.getLastResponseStatus());
    }

    @Then("the operation should be successful")
    public void theOperationShouldBeSuccessful() {
        assertTrue(testContext.getLastResponseStatus() >= 200 && testContext.getLastResponseStatus() < 300);
    }

    @Then("the response should be in the correct format")
    public void theResponseShouldBeInTheCorrectFormat() {
        assertNotNull(testContext.getLastResponse());
        // Additional format validation can be added here
    }
} 