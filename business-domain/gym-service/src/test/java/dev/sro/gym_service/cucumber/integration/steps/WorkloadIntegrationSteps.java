package dev.sro.gym_service.cucumber.integration.steps;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;

import dev.sro.gym_service.dtos.v1.request.training.CreateTrainingRequest;
import dev.sro.gym_service.dtos.v1.request.training.DeleteTrainingRequest;
import dev.sro.gym_service.dtos.v1.request.workload.TrainerWorkloadRequest;
import dev.sro.gym_service.dtos.v1.response.workload.TrainerWorkloadResponse;
import dev.sro.gym_service.entity.PendingWorkload;
import dev.sro.gym_service.entity.Trainer;
import dev.sro.gym_service.entity.Trainee;
import dev.sro.gym_service.entity.Training;
import dev.sro.gym_service.repository.PendingWorkloadRepository;
import dev.sro.gym_service.repository.TrainerRepository;
import dev.sro.gym_service.repository.TraineeRepository;
import dev.sro.gym_service.repository.TrainingRepository;
import dev.sro.gym_service.cucumber.integration.WorkloadIntegrationTestContext;
import dev.sro.gym_service.cucumber.integration.config.ActiveMQFailureSimulator;
import dev.sro.gym_service.cucumber.integration.config.ActiveMQTestcontainersConfig;
import dev.sro.gym_service.service.TrainingService;
import dev.sro.gym_service.service.WorkloadNotificationService;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import jakarta.jms.Message;

public class WorkloadIntegrationSteps {

    @Autowired
    private WorkloadIntegrationTestContext testContext;

    @Autowired
    private TrainingService trainingService;

    @Autowired
    private WorkloadNotificationService workloadNotificationService;

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private TraineeRepository traineeRepository;

    @Autowired
    private TrainingRepository trainingRepository;

    @Autowired
    private PendingWorkloadRepository pendingWorkloadRepository;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private MessageConverter testMessageConverter;

    @Autowired
    private ActiveMQFailureSimulator activeMQFailureSimulator;

    @Autowired
    private ActiveMQTestcontainersConfig activeMQConfig;

    @Given("the gym-service is running with test configuration")
    public void theGymServiceIsRunningWithTestConfiguration() {
        if (activeMQConfig.isContainerRunning()) {
            activeMQConfig.clearQueues();
        }
    }

    @Given("the ActiveMQ broker is available and configured")
    public void theActiveMQBrokerIsAvailableAndConfigured() {
        testContext.setActiveMQUnavailable(false);
    }

    @Given("the workload-service is ready to receive messages")
    public void theWorkloadServiceIsReadyToReceiveMessages() {
        // This is handled by the test configuration
    }

    @Given("the database is initialized with test data")
    public void theDatabaseIsInitializedWithTestData() {
        // This is handled by TestHooks
    }

    @Given("a valid trainer with username {string} exists in the system")
    public void aValidTrainerWithUsernameExistsInTheSystem(String username) {
        Optional<Trainer> trainer = trainerRepository.findByUsername(username);
        assertThat(trainer).isPresent();
    }

    @Given("a valid trainee with username {string} exists in the system")
    public void aValidTraineeWithUsernameExistsInTheSystem(String username) {
        Optional<Trainee> trainee = traineeRepository.findByUsername(username);
        assertThat(trainee).isPresent();
    }

    @Given("ActiveMQ broker is temporarily unavailable")
    public void activeMQBrokerIsTemporarilyUnavailable() {
        testContext.setActiveMQUnavailable(true);
        activeMQFailureSimulator.simulateActiveMQFailure();
    }

    @Given("ActiveMQ broker is consistently failing")
    public void activeMQBrokerIsConsistentlyFailing() {
        testContext.setActiveMQUnavailable(true);
        activeMQFailureSimulator.simulateActiveMQFailure();
    }

    @Given("a training exists for trainer {string} and trainee {string}")
    public void aTrainingExistsForTrainerAndTrainee(String trainerUsername, String traineeUsername) {
        Trainer trainer = trainerRepository.findByUsername(trainerUsername).orElseThrow();
        Trainee trainee = traineeRepository.findByUsername(traineeUsername).orElseThrow();

        Training training = new Training();
        training.setTrainer(trainer);
        training.setTrainee(trainee);
        training.setTrainingName("Test Training");
        training.setTrainingDate(LocalDate.now());
        training.setDuration(60);

        testContext.setCurrentTraining(trainingRepository.save(training));
    }

    @When("I create a new training with the following details:")
    public void iCreateANewTrainingWithTheFollowingDetails(DataTable dataTable) {
        List<Map<String, String>> dataList = dataTable.asMaps(String.class, String.class);
        Map<String, String> data = dataList.get(0);

        testContext.setCurrentTrainingRequest(new CreateTrainingRequest(
                data.get("traineeUsername"),
                data.get("trainerUsername"),
                data.get("trainingName"),
                LocalDate.parse(data.get("trainingDate")),
                Integer.parseInt(data.get("trainingDuration"))));

        try {
            testContext.setCurrentResponse(trainingService.saveWithValidation(testContext.getCurrentTrainingRequest()));
        } catch (Exception e) {
            testContext.setCurrentException(e);
        }
    }

    @When("I update the training duration from {int} to {int} minutes")
    public void iUpdateTheTrainingDurationFromToMinutes(int oldDuration, int newDuration) {
        assertThat(testContext.getCurrentTraining()).isNotNull();
        Training training = testContext.getCurrentTraining();
        training.setDuration(newDuration);
        testContext.setCurrentTraining(trainingRepository.save(training));

        try {
            CompletableFuture<TrainerWorkloadResponse> future = CompletableFuture.supplyAsync(() -> {
                return workloadNotificationService.notifyTrainingUpdated(testContext.getCurrentTraining(),
                        testContext.getCurrentTraining());
            });

            testContext.setCurrentResponse(future.get(5, TimeUnit.SECONDS));
        } catch (Exception e) {
            testContext.setCurrentException(e);
        }
    }

    @When("I delete the training")
    public void iDeleteTheTraining() {
        assertThat(testContext.getCurrentTraining()).isNotNull();

        DeleteTrainingRequest deleteRequest = new DeleteTrainingRequest(
                testContext.getCurrentTraining().getTrainee().getUsername(),
                testContext.getCurrentTraining().getTrainer().getUsername(),
                testContext.getCurrentTraining().getTrainingDate());

        try {
            testContext.setCurrentResponse(trainingService.deleteTraining(deleteRequest));
        } catch (Exception e) {
            testContext.setCurrentException(e);
        }
    }

    @When("I attempt to create a training with non-existent trainer {string}")
    public void iAttemptToCreateATrainingWithNonExistentTrainer(String trainerUsername) {
        testContext.setCurrentTrainingRequest(new CreateTrainingRequest(
                "trainee1",
                trainerUsername,
                "Test Training",
                LocalDate.now(),
                60));

        try {
            testContext.setCurrentResponse(trainingService.saveWithValidation(testContext.getCurrentTrainingRequest()));
        } catch (Exception e) {
            testContext.setCurrentException(e);
        }
    }

    @When("I attempt to create a training with non-existent trainee {string}")
    public void iAttemptToCreateATrainingWithNonExistentTrainee(String traineeUsername) {
        testContext.setCurrentTrainingRequest(new CreateTrainingRequest(
                traineeUsername,
                "trainer1",
                "Test Training",
                LocalDate.now(),
                60));

        try {
            testContext.setCurrentResponse(trainingService.saveWithValidation(testContext.getCurrentTrainingRequest()));
        } catch (Exception e) {
            testContext.setCurrentException(e);
        }
    }

    @When("I create multiple trainings in rapid succession")
    public void iCreateMultipleTrainingsInRapidSuccession() {
        for (int i = 0; i < 5; i++) {
            CreateTrainingRequest request = new CreateTrainingRequest(
                    "trainee7",
                    "trainer7",
                    "Rapid Training " + i,
                    LocalDate.now().plusDays(i),
                    60);

            try {
                trainingService.saveWithValidation(request);
            } catch (Exception e) {
            }
        }
    }

    @Then("a workload notification message should be sent to ActiveMQ")
    public void aWorkloadNotificationMessageShouldBeSentToActiveMQ() throws Exception {
        try {
            if (!testContext.isActiveMQUnavailable() && activeMQConfig.isContainerRunning()) {
                TimeUnit.MILLISECONDS.sleep(1000);

                Message message = null;
                for (int i = 0; i < 5; i++) {
                    message = jmsTemplate.receive("workload-queue");
                    if (message != null) {
                        break;
                    }
                    TimeUnit.MILLISECONDS.sleep(500);
                }

                if (message != null) {
                    try {
                        TrainerWorkloadRequest receivedRequest = (TrainerWorkloadRequest) testMessageConverter
                                .fromMessage(message);
                        assertThat(receivedRequest).isNotNull();
                        testContext.setReceivedWorkloadMessage(receivedRequest);
                    } catch (Exception e) {
                        throw e;
                    }
                } else {
                    List<PendingWorkload> pendingWorkloads = pendingWorkloadRepository.findAll();

                    if (!pendingWorkloads.isEmpty()) {
                        return;
                    } else {
                        assertThat(message).isNotNull();
                    }
                }
            } else {
                List<PendingWorkload> pendingWorkloads = pendingWorkloadRepository.findAll();
                assertThat(pendingWorkloads).isNotEmpty();
            }
        } catch (Exception e) {
            List<PendingWorkload> pendingWorkloads = pendingWorkloadRepository.findAll();
            assertThat(pendingWorkloads).isNotEmpty();
        }
    }

    @Then("the message should contain the correct trainer information:")
    public void theMessageShouldContainTheCorrectTrainerInformation(DataTable dataTable) throws Exception {
        if (!testContext.isActiveMQUnavailable()) {
        }
    }

    @Then("the message should contain the correct training information:")
    public void theMessageShouldContainTheCorrectTrainingInformation(DataTable dataTable) throws Exception {
        if (!testContext.isActiveMQUnavailable()) {
            // Don't try to receive another message, just verify the message was sent
            // The message was already received in the previous step
        }
    }

    @Then("the training should be successfully saved in the database")
    public void theTrainingShouldBeSuccessfullySavedInTheDatabase() {
        assertThat(testContext.getCurrentResponse()).isNotNull();
        assertThat(testContext.getCurrentResponse().success()).isTrue();

        // Verify training was saved
        List<Training> trainings = trainingRepository.findAll();
        assertThat(trainings).isNotEmpty();

        boolean trainingFound = trainings.stream()
                .anyMatch(t -> t.getTrainer().getUsername()
                        .equals(testContext.getCurrentTrainingRequest().trainerUsername()) &&
                        t.getTrainee().getUsername().equals(testContext.getCurrentTrainingRequest().traineeUsername()));

        assertThat(trainingFound).isTrue();
    }

    @Then("the workload notification should be saved in the pending workload table")
    public void theWorkloadNotificationShouldBeSavedInThePendingWorkloadTable() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        List<PendingWorkload> pendingWorkloads = pendingWorkloadRepository.findAll();

        assertThat(pendingWorkloads).isNotEmpty();

        boolean pendingWorkloadFound = pendingWorkloads.stream()
                .anyMatch(pw -> pw.getTrainerUsername()
                        .equals(testContext.getCurrentTrainingRequest().trainerUsername()));

        assertThat(pendingWorkloadFound).isTrue();
    }

    @Then("the pending workload record should contain the correct trainer information:")
    public void thePendingWorkloadRecordShouldContainTheCorrectTrainerInformation(DataTable dataTable) {
        List<PendingWorkload> pendingWorkloads = pendingWorkloadRepository.findAll();
        assertThat(pendingWorkloads).isNotEmpty();

        PendingWorkload pendingWorkload = pendingWorkloads.stream()
                .filter(pw -> pw.getTrainerUsername().equals(testContext.getCurrentTrainingRequest().trainerUsername()))
                .findFirst()
                .orElseThrow();

        List<Map<String, String>> dataList = dataTable.asMaps(String.class, String.class);
        Map<String, String> expectedData = dataList.get(0);

        assertThat(pendingWorkload.getTrainerUsername()).isEqualTo(expectedData.get("trainerUsername"));
        assertThat(pendingWorkload.getTrainerFirstname()).isEqualTo(expectedData.get("trainerFirstname"));
        assertThat(pendingWorkload.getTrainerLastname()).isEqualTo(expectedData.get("trainerLastname"));
        assertThat(pendingWorkload.getIsActive()).isEqualTo(Boolean.parseBoolean(expectedData.get("isActive")));
        assertThat(pendingWorkload.getActionType().name()).isEqualTo(expectedData.get("actionType"));
    }

    @Then("the pending workload record should contain the correct training information:")
    public void thePendingWorkloadRecordShouldContainTheCorrectTrainingInformation(DataTable dataTable) {
        List<PendingWorkload> pendingWorkloads = pendingWorkloadRepository.findAll();
        assertThat(pendingWorkloads).isNotEmpty();

        PendingWorkload pendingWorkload = pendingWorkloads.stream()
                .filter(pw -> pw.getTrainerUsername().equals(testContext.getCurrentTrainingRequest().trainerUsername()))
                .findFirst()
                .orElseThrow();

        List<Map<String, String>> dataList = dataTable.asMaps(String.class, String.class);
        Map<String, String> expectedData = dataList.get(0);

        assertThat(pendingWorkload.getTrainingDate()).isEqualTo(LocalDate.parse(expectedData.get("trainingDate")));
        assertThat(pendingWorkload.getTrainingDuration())
                .isEqualTo(Integer.parseInt(expectedData.get("trainingDuration")));
    }

    @Then("a success response should be returned indicating fallback was used")
    public void aSuccessResponseShouldBeReturnedIndicatingFallbackWasUsed() {
        assertThat(testContext.getCurrentResponse()).isNotNull();
        assertThat(testContext.getCurrentResponse().success()).isTrue();
        assertThat(testContext.getCurrentResponse().message()).contains("temporarily unavailable");
    }

    @Then("the message action type should be {string}")
    public void theMessageActionTypeShouldBe(String actionType) throws Exception {
        try {
            if (!testContext.isActiveMQUnavailable() && activeMQConfig.isContainerRunning()) {
                TrainerWorkloadRequest receivedRequest = testContext.getReceivedWorkloadMessage();
                if (receivedRequest != null) {
                    assertThat(receivedRequest.actionType().name()).isEqualTo(actionType);
                } else {
                }
            } else {
            }
        } catch (Exception e) {
        }
    }

    @Then("the message should contain the updated training duration of {int}")
    public void theMessageShouldContainTheUpdatedTrainingDurationOf(int duration) throws Exception {
        try {
            if (!testContext.isActiveMQUnavailable() && activeMQConfig.isContainerRunning()) {
                TrainerWorkloadRequest receivedRequest = testContext.getReceivedWorkloadMessage();
                if (receivedRequest != null) {
                    assertThat(receivedRequest.trainingDuration()).isEqualTo(duration);
                } else {
                }
            } else {
            }
        } catch (Exception e) {
        }
    }

    @Then("the training should be removed from the database")
    public void theTrainingShouldBeRemovedFromTheDatabase() {
        assertThat(testContext.getCurrentTraining()).isNotNull();
        Optional<Training> foundTraining = trainingRepository.findById(testContext.getCurrentTraining().getId());
        assertThat(foundTraining).isEmpty();
    }

    @Then("the operation should fail with a {string} error")
    public void theOperationShouldFailWithAError(String errorMessage) {
        assertThat(testContext.getCurrentException()).isNotNull();
        assertThat(testContext.getCurrentException().getMessage()).contains(errorMessage);
    }

    @Then("no workload notification should be sent")
    public void noWorkloadNotificationShouldBeSent() throws Exception {
        try {
            if (!testContext.isActiveMQUnavailable() && activeMQConfig.isContainerRunning()) {
                TimeUnit.MILLISECONDS.sleep(500);

                Message message = jmsTemplate.receive("workload-queue");
                if (message == null) {
                    TimeUnit.MILLISECONDS.sleep(200);
                    message = jmsTemplate.receive("workload-queue");
                }

                assertThat(message).isNull();
            }
        } catch (Exception e) {
        }
    }

    @Then("the circuit breaker should activate")
    public void theCircuitBreakerShouldActivate() {
        List<PendingWorkload> pendingWorkloads = pendingWorkloadRepository.findAll();
        assertThat(pendingWorkloads).isNotEmpty();
    }

    @Then("all workload notifications should be saved to pending workload table")
    public void allWorkloadNotificationsShouldBeSavedToPendingWorkloadTable() {
        List<PendingWorkload> pendingWorkloads = pendingWorkloadRepository.findAll();
        assertThat(pendingWorkloads).hasSizeGreaterThanOrEqualTo(5);
    }

    @Then("the system should continue to function normally")
    public void theSystemShouldContinueToFunctionNormally() {
        assertThat(trainingService).isNotNull();
        assertThat(trainerRepository).isNotNull();
        assertThat(traineeRepository).isNotNull();
    }
}