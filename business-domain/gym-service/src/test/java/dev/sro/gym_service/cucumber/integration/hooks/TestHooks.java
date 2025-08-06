package dev.sro.gym_service.cucumber.integration.hooks;

import org.springframework.beans.factory.annotation.Autowired;

import dev.sro.gym_service.cucumber.integration.WorkloadIntegrationTestContext;
import dev.sro.gym_service.entity.Trainer;
import dev.sro.gym_service.entity.Trainee;
import dev.sro.gym_service.repository.PendingWorkloadRepository;
import dev.sro.gym_service.repository.TrainerRepository;
import dev.sro.gym_service.repository.TraineeRepository;
import dev.sro.gym_service.repository.TrainingRepository;
import dev.sro.gym_service.cucumber.integration.config.ActiveMQTestcontainersConfig;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

public class TestHooks {

    @Autowired
    private WorkloadIntegrationTestContext testContext;

    @Autowired
    private PendingWorkloadRepository pendingWorkloadRepository;

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private TraineeRepository traineeRepository;

    @Autowired
    private TrainingRepository trainingRepository;

    @Autowired
    private ActiveMQTestcontainersConfig activeMQConfig;

    @Before
    public void setUp(Scenario scenario) {
        testContext.clear();

        verifyActiveMQContainer();

        clearTestData();

        initializeTestData();

    }

    @After
    public void tearDown(Scenario scenario) {

        testContext.clear();

        clearTestData();

        activeMQConfig.clearQueues();

    }

    private void clearTestData() {
        trainingRepository.deleteAll();
        pendingWorkloadRepository.deleteAll();
        traineeRepository.deleteAll();
        trainerRepository.deleteAll();
    }

    private void initializeTestData() {
        createTestTrainer("trainer1", "John", "Doe", true);
        createTestTrainer("trainer2", "Jane", "Smith", true);
        createTestTrainer("trainer3", "Bob", "Wilson", true);
        createTestTrainer("trainer4", "Alice", "Johnson", true);
        createTestTrainer("trainer6", "Charlie", "Brown", true);
        createTestTrainer("trainer7", "Diana", "Prince", true);

        createTestTrainee("trainee1", "Mike", "Ross");
        createTestTrainee("trainee2", "Rachel", "Green");
        createTestTrainee("trainee3", "Chandler", "Bing");
        createTestTrainee("trainee4", "Monica", "Geller");
        createTestTrainee("trainee5", "Joey", "Tribbiani");
        createTestTrainee("trainee7", "Phoebe", "Buffay");
    }

    private void createTestTrainer(String username, String firstName, String lastName, boolean isActive) {
        Trainer trainer = new Trainer();
        trainer.setUsername(username);
        trainer.setFirstName(firstName);
        trainer.setLastName(lastName);
        trainer.setActive(isActive);
        trainer.setPassword("password123");
        trainerRepository.save(trainer);
    }

    private void createTestTrainee(String username, String firstName, String lastName) {
        Trainee trainee = new Trainee();
        trainee.setUsername(username);
        trainee.setFirstName(firstName);
        trainee.setLastName(lastName);
        trainee.setPassword("password123");
        traineeRepository.save(trainee);
    }

    private void verifyActiveMQContainer() {
        try {
            if (!activeMQConfig.isContainerRunning()) {
                activeMQConfig.startContainer();

                Thread.sleep(2000);

                if (!activeMQConfig.isContainerRunning()) {
                }
            } else {
            }
        } catch (Exception e) {
        }
    }
}