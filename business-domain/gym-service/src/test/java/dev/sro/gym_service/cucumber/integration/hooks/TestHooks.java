package dev.sro.gym_service.cucumber.integration.hooks;

import org.springframework.beans.factory.annotation.Autowired;

import dev.sro.gym_service.cucumber.integration.WorkloadIntegrationTestContext;
import dev.sro.gym_service.entity.PendingWorkload;
import dev.sro.gym_service.entity.Trainer;
import dev.sro.gym_service.entity.Trainee;
import dev.sro.gym_service.entity.Training;
import dev.sro.gym_service.repository.PendingWorkloadRepository;
import dev.sro.gym_service.repository.TrainerRepository;
import dev.sro.gym_service.repository.TraineeRepository;
import dev.sro.gym_service.repository.TrainingRepository;
import dev.sro.gym_service.cucumber.integration.config.ActiveMQTestcontainersConfig;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
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
        log.info("Starting scenario: {}", scenario.getName());
        
        // Clear test context
        testContext.clear();
        
        // Verify ActiveMQ container is running
        verifyActiveMQContainer();
        
        // Clear all test data before each scenario
        clearTestData();
        
        // Initialize test data
        initializeTestData();
        
        log.info("Test environment setup completed for scenario: {}", scenario.getName());
    }

    @After
    public void tearDown(Scenario scenario) {
        log.info("Cleaning up after scenario: {}", scenario.getName());
        
        // Clear test context
        testContext.clear();
        
        // Clear all test data after each scenario
        clearTestData();
        
        // Clear ActiveMQ queues
        activeMQConfig.clearQueues();
        
        log.info("Cleanup completed for scenario: {}", scenario.getName());
    }

    private void clearTestData() {
        // Clear in reverse order to respect foreign key constraints
        trainingRepository.deleteAll();
        pendingWorkloadRepository.deleteAll();
        traineeRepository.deleteAll();
        trainerRepository.deleteAll();
    }



    private void initializeTestData() {
        // Create test trainers
        createTestTrainer("trainer1", "John", "Doe", true);
        createTestTrainer("trainer2", "Jane", "Smith", true);
        createTestTrainer("trainer3", "Bob", "Wilson", true);
        createTestTrainer("trainer4", "Alice", "Johnson", true);
        createTestTrainer("trainer6", "Charlie", "Brown", true);
        createTestTrainer("trainer7", "Diana", "Prince", true);

        // Create test trainees
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
                log.warn("ActiveMQ container is not running, attempting to restart...");
                activeMQConfig.startContainer();
                
                // Wait a bit for the container to be ready
                Thread.sleep(2000);
                
                if (!activeMQConfig.isContainerRunning()) {
                    log.error("Failed to start ActiveMQ container, tests may fail");
                } else {
                    log.info("ActiveMQ container restarted successfully");
                }
            } else {
                log.info("ActiveMQ container is running");
            }
        } catch (Exception e) {
            log.error("Error verifying ActiveMQ container: {}", e.getMessage());
        }
    }
} 