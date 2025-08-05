package dev.sro.gym_service.cucumber.component.steps;

import dev.sro.gym_service.cucumber.component.TrainingTestContext;
import dev.sro.gym_service.dtos.v1.request.auth.LoginRequest;
import dev.sro.gym_service.dtos.v1.request.trainee.RegisterTraineeRequest;
import dev.sro.gym_service.dtos.v1.request.trainer.RegisterTrainerRequest;
import dev.sro.gym_service.dtos.v1.request.training.CreateTrainingRequest;
import dev.sro.gym_service.dtos.v1.request.training.DeleteTrainingRequest;
import dev.sro.gym_service.dtos.v1.request.training.UpdateTrainingRequest;
import dev.sro.gym_service.dtos.v1.response.auth.LoginResponse;
import dev.sro.gym_service.dtos.v1.response.trainee.RegisterTraineeResponse;
import dev.sro.gym_service.dtos.v1.response.trainer.RegisterTrainerResponse;
import dev.sro.gym_service.dtos.v1.response.training.TrainingMutationResponse;
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

public class TrainingManagementSteps extends CommonHttpSteps {

    @Autowired
    private TrainingTestContext testContext;

    @Before
    public void setUp() {
        testContext.clear();
    }

    @After
    public void tearDown() {
        testContext.clear();
    }

    // Background steps
    @Given("the gym service is running for training tests")
    public void theGymServiceIsRunningForTrainingTests() {
        assertNotNull(webTestClient);
    }

    @Given("I have a valid authentication token for training")
    public void iHaveAValidAuthenticationTokenForTraining() {
        // Create a test trainee and authenticate to get a valid token
        RegisterTraineeRequest traineeRequest = new RegisterTraineeRequest(
                "Test",
                "Trainee",
                LocalDate.of(1990, 1, 1),
                "Test Address"
        );
        
        webTestClient.post()
                .uri("/api/v1/trainees")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(traineeRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(new ParameterizedTypeReference<ApiStandardResponse<RegisterTraineeResponse>>() {})
                .consumeWith(response -> {
                    ApiStandardResponse<RegisterTraineeResponse> apiResponse = response.getResponseBody();
                    assertNotNull(apiResponse);
                    assertNotNull(apiResponse.data());
                    
                    RegisterTraineeResponse traineeResponse = apiResponse.data();
                    String username = traineeResponse.username();
                    String password = traineeResponse.plainPassword();
                    
                    assertNotNull(username, "Username should not be null");
                    assertNotNull(password, "Password should not be null");
                    
                    testContext.setCurrentCredentials(username, password);
                    testContext.setTestData("traineeUsername", username);
                    
                    // Create a test trainer
                    RegisterTrainerRequest trainerRequest = new RegisterTrainerRequest(
                            "Test",
                            "Trainer",
                            1L
                    );
                    
                    webTestClient.post()
                            .uri("/api/v1/trainers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(trainerRequest)
                            .exchange()
                            .expectStatus().isCreated()
                            .expectBody(RegisterTrainerResponse.class)
                            .consumeWith(trainerResponse -> {
                                RegisterTrainerResponse trainer = trainerResponse.getResponseBody();
                                assertNotNull(trainer);
                                testContext.setTestData("trainerUsername", trainer.username());
                            });
                    
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
                });
    }

    @Given("I don't have a valid authentication token for training")
    public void iDontHaveAValidAuthenticationTokenForTraining() {
        testContext.setAccessToken(null);
    }

    // Given steps for data setup
    @Given("I want to create a new training with the following details:")
    public void iWantToCreateANewTrainingWithTheFollowingDetails(DataTable dataTable) {
        List<Map<String, String>> dataList = dataTable.asMaps(String.class, String.class);
        Map<String, String> data = dataList.get(0);
        
        // Use dynamic usernames from context if available, otherwise use the ones from the table
        String traineeUsername = (String) testContext.getTestData("traineeUsername");
        String trainerUsername = (String) testContext.getTestData("trainerUsername");
        
        if (traineeUsername == null) {
            traineeUsername = data.get("traineeUsername");
        }
        if (trainerUsername == null) {
            trainerUsername = data.get("trainerUsername");
        }
        
        CreateTrainingRequest request = new CreateTrainingRequest(
                traineeUsername,
                trainerUsername,
                data.get("trainingName"),
                LocalDate.parse(data.get("trainingDate")),
                Integer.parseInt(data.get("duration"))
        );
        
        testContext.setCurrentCreateRequest(request);
    }

    @Given("I want to create a new training with invalid details:")
    public void iWantToCreateANewTrainingWithInvalidDetails(DataTable dataTable) {
        List<Map<String, String>> dataList = dataTable.asMaps(String.class, String.class);
        Map<String, String> data = dataList.get(0);
        
        CreateTrainingRequest request = new CreateTrainingRequest(
                data.get("traineeUsername"),
                data.get("trainerUsername"),
                data.get("trainingName"),
                data.get("trainingDate") != null && !data.get("trainingDate").isEmpty() 
                    ? LocalDate.parse(data.get("trainingDate")) : null,
                data.get("duration") != null && !data.get("duration").isEmpty() 
                    ? Integer.parseInt(data.get("duration")) : 0
        );
        
        testContext.setCurrentCreateRequest(request);
    }

    @Given("I want to create a new training with non-existent users:")
    public void iWantToCreateANewTrainingWithNonExistentUsers(DataTable dataTable) {
        List<Map<String, String>> dataList = dataTable.asMaps(String.class, String.class);
        Map<String, String> data = dataList.get(0);
        
        // For negative tests, always use the exact usernames from the table
        // Don't use context usernames to ensure we test with non-existent users
        CreateTrainingRequest request = new CreateTrainingRequest(
                data.get("traineeUsername"),
                data.get("trainerUsername"),
                data.get("trainingName"),
                LocalDate.parse(data.get("trainingDate")),
                Integer.parseInt(data.get("duration"))
        );
        
        testContext.setCurrentCreateRequest(request);
    }

    @Given("a training exists with trainee {string} and trainer {string}")
    public void aTrainingExistsWithTraineeAndTrainer(String traineeUsername, String trainerUsername) {
        // Create trainee if it doesn't exist
        RegisterTraineeRequest traineeRequest = new RegisterTraineeRequest(
                "Test",
                "Trainee",
                LocalDate.of(1990, 1, 1),
                "Test Address"
        );
        
        webTestClient.post()
                .uri("/api/v1/trainees")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(traineeRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(new ParameterizedTypeReference<ApiStandardResponse<RegisterTraineeResponse>>() {})
                .consumeWith(response -> {
                    ApiStandardResponse<RegisterTraineeResponse> apiResponse = response.getResponseBody();
                    assertNotNull(apiResponse);
                    String traineeUsernameCreated = apiResponse.data().username();
                    String traineePassword = apiResponse.data().plainPassword();
                    testContext.setTestData("traineeUsername", traineeUsernameCreated);
                    testContext.setTestData("traineePassword", traineePassword);
                });

        // Create trainer if it doesn't exist
        RegisterTrainerRequest trainerRequest = new RegisterTrainerRequest(
                "Test",
                "Trainer",
                1L
        );
        
        webTestClient.post()
                .uri("/api/v1/trainers")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(trainerRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(RegisterTrainerResponse.class)
                .consumeWith(response -> {
                    RegisterTrainerResponse trainerResponse = response.getResponseBody();
                    assertNotNull(trainerResponse);
                    String trainerUsernameCreated = trainerResponse.username();
                    testContext.setTestData("trainerUsername", trainerUsernameCreated);
                    
                    // Now create a training between the trainee and trainer
                    String traineeUsernameFromContext = (String) testContext.getTestData("traineeUsername");
                    String traineePassword = (String) testContext.getTestData("traineePassword");
                    
                    // Authenticate as trainee to get token
                    LoginRequest loginRequest = new LoginRequest(traineeUsernameFromContext, traineePassword);
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
                    String token = loginResponse.data().token();
                    
                                         // Create training
                     LocalDate trainingDate = LocalDate.now();
                     CreateTrainingRequest trainingRequest = new CreateTrainingRequest(
                             traineeUsernameFromContext,
                             trainerUsernameCreated,
                             "Zumba",
                             trainingDate,
                             60
                     );
                     
                     webTestClient.post()
                             .uri("/api/v1/trainings")
                             .contentType(MediaType.APPLICATION_JSON)
                             .header("Authorization", "Bearer " + token)
                             .bodyValue(trainingRequest)
                             .exchange()
                             .expectStatus().isOk();
                     
                     // Store the training date for later use
                     testContext.setTestData("trainingDate", trainingDate);
                });
    }

    @Given("I want to update the training with the following details:")
    public void iWantToUpdateTheTrainingWithTheFollowingDetails(DataTable dataTable) {
        List<Map<String, String>> dataList = dataTable.asMaps(String.class, String.class);
        Map<String, String> data = dataList.get(0);
        
        // Use dynamic usernames from context if available, otherwise use the ones from the table
        String traineeUsername = (String) testContext.getTestData("traineeUsername");
        String trainerUsername = (String) testContext.getTestData("trainerUsername");
        
        if (traineeUsername == null) {
            traineeUsername = data.get("traineeUsername");
        }
        if (trainerUsername == null) {
            trainerUsername = data.get("trainerUsername");
        }
        
        // Use training date from context if available, otherwise use the one from the table
        LocalDate trainingDate = (LocalDate) testContext.getTestData("trainingDate");
        if (trainingDate == null) {
            trainingDate = LocalDate.parse(data.get("trainingDate"));
        }
        
        UpdateTrainingRequest request = new UpdateTrainingRequest(
                data.get("trainingName"),
                trainingDate,
                Integer.parseInt(data.get("duration")),
                trainerUsername,
                traineeUsername,
                data.get("trainingType")
        );
        
        testContext.setCurrentUpdateRequest(request);
    }

    @Given("I want to update the training with non-existent users:")
    public void iWantToUpdateTheTrainingWithNonExistentUsers(DataTable dataTable) {
        List<Map<String, String>> dataList = dataTable.asMaps(String.class, String.class);
        Map<String, String> data = dataList.get(0);
        
        // For negative tests, always use the exact usernames from the table
        // Don't use context usernames to ensure we test with non-existent users
        UpdateTrainingRequest request = new UpdateTrainingRequest(
                data.get("trainingName"),
                LocalDate.parse(data.get("trainingDate")),
                Integer.parseInt(data.get("duration")),
                data.get("trainerUsername"),
                data.get("traineeUsername"),
                data.get("trainingType")
        );
        
        testContext.setCurrentUpdateRequest(request);
    }

    @Given("I want to delete the training with the following details:")
    public void iWantToDeleteTheTrainingWithTheFollowingDetails(DataTable dataTable) {
        List<Map<String, String>> dataList = dataTable.asMaps(String.class, String.class);
        Map<String, String> data = dataList.get(0);
        
        // Use dynamic usernames from context if available, otherwise use the ones from the table
        String traineeUsername = (String) testContext.getTestData("traineeUsername");
        String trainerUsername = (String) testContext.getTestData("trainerUsername");
        
        if (traineeUsername == null) {
            traineeUsername = data.get("traineeUsername");
        }
        if (trainerUsername == null) {
            trainerUsername = data.get("trainerUsername");
        }
        
        // Use training date from context if available, otherwise use the one from the table
        LocalDate trainingDate = (LocalDate) testContext.getTestData("trainingDate");
        if (trainingDate == null) {
            trainingDate = LocalDate.parse(data.get("trainingDate"));
        }
        
        DeleteTrainingRequest request = new DeleteTrainingRequest(
                traineeUsername,
                trainerUsername,
                trainingDate
        );
        
        testContext.setCurrentDeleteRequest(request);
    }

    @Given("I want to delete the training with non-existent users:")
    public void iWantToDeleteTheTrainingWithNonExistentUsers(DataTable dataTable) {
        List<Map<String, String>> dataList = dataTable.asMaps(String.class, String.class);
        Map<String, String> data = dataList.get(0);
        
        // For negative tests, always use the exact usernames from the table
        // Don't use context usernames to ensure we test with non-existent users
        DeleteTrainingRequest request = new DeleteTrainingRequest(
                data.get("traineeUsername"),
                data.get("trainerUsername"),
                LocalDate.parse(data.get("trainingDate"))
        );
        
        testContext.setCurrentDeleteRequest(request);
    }

    // When steps for actions - using unique names
    @When("I send a POST request to create training at {string}")
    public void iSendAPostRequestToCreateTraining(String endpoint) {
        CreateTrainingRequest request = testContext.getCurrentCreateRequest();
        assertNotNull(request, "Create request should be set before making POST request");

        String token = testContext.getAccessToken();

        sendPostRequest(endpoint, request, token, TrainingMutationResponse.class, 
            (status, responseBody) -> {
                testContext.setLastResponseStatus(status);
                testContext.setLastResponse(null);
            });
    }

    @When("I send a PUT request to update training at {string}")
    public void iSendAPutRequestToUpdateTraining(String endpoint) {
        UpdateTrainingRequest request = testContext.getCurrentUpdateRequest();
        assertNotNull(request, "Update request should be set before making PUT request");

        String token = testContext.getAccessToken();

        sendPutRequest(endpoint, request, token, TrainingMutationResponse.class, 
            (status, responseBody) -> {
                testContext.setLastResponseStatus(status);
                testContext.setLastResponse(null);
            });
    }

    @When("I send a DELETE request to delete training at {string}")
    public void iSendADeleteRequestToDeleteTraining(String endpoint) {
        DeleteTrainingRequest request = testContext.getCurrentDeleteRequest();
        assertNotNull(request, "Delete request should be set before making DELETE request");

        String token = testContext.getAccessToken();

        sendDeleteRequest(endpoint, request, token, TrainingMutationResponse.class, 
            (status, responseBody) -> {
                testContext.setLastResponseStatus(status);
                testContext.setLastResponse(null);
            });
    }

    // Then steps for assertions - using unique names
    @Then("the training response status should be {int}")
    public void theTrainingResponseStatusShouldBe(int expectedStatus) {
        assertEquals(expectedStatus, testContext.getLastResponseStatus());
    }

    @Then("the response should contain a valid training creation")
    public void theResponseShouldContainAValidTrainingCreation() {
        assertTrue(testContext.getLastResponseStatus() == 200);
    }

    @Then("the response should contain a valid training update")
    public void theResponseShouldContainAValidTrainingUpdate() {
        assertTrue(testContext.getLastResponseStatus() == 200);
    }

    @Then("the response should contain a valid training deletion")
    public void theResponseShouldContainAValidTrainingDeletion() {
        assertTrue(testContext.getLastResponseStatus() == 200);
    }

    @Then("the response should contain training validation errors")
    public void theResponseShouldContainTrainingValidationErrors() {
        assertTrue(testContext.getLastResponseStatus() >= 400);
    }

    @Then("the response should contain a training not found error")
    public void theResponseShouldContainATrainingNotFoundError() {
        assertEquals(404, testContext.getLastResponseStatus());
    }

    @Then("the response should contain a training authentication error")
    public void theResponseShouldContainATrainingAuthenticationError() {
        assertEquals(401, testContext.getLastResponseStatus());
    }

    @Then("the response should contain a training authorization error")
    public void theResponseShouldContainATrainingAuthorizationError() {
        assertEquals(403, testContext.getLastResponseStatus());
    }
} 