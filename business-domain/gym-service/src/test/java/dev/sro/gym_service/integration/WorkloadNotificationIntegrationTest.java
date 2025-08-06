package dev.sro.gym_service.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import dev.sro.gym_service.dtos.v1.request.training.CreateTrainingRequest;
import dev.sro.gym_service.dtos.v1.request.workload.TrainerWorkloadRequest;
import dev.sro.gym_service.dtos.v1.response.workload.TrainerWorkloadResponse;
import dev.sro.gym_service.entity.PendingWorkload;
import dev.sro.gym_service.entity.Trainer;
import dev.sro.gym_service.entity.Trainee;
import dev.sro.gym_service.entity.Training;
import dev.sro.gym_service.entity.enums.ActionType;
import dev.sro.gym_service.repository.PendingWorkloadRepository;
import dev.sro.gym_service.repository.TrainerRepository;
import dev.sro.gym_service.repository.TraineeRepository;
import dev.sro.gym_service.repository.TrainingRepository;
import dev.sro.gym_service.service.TrainingService;
import dev.sro.gym_service.service.WorkloadNotificationService;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class WorkloadNotificationIntegrationTest {

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

    @MockBean
    private JmsTemplate jmsTemplate;

    private Trainer testTrainer;
    private Trainee testTrainee;
    private Training testTraining;

    @BeforeEach
    void setUp() {
        // Create test data
        testTrainer = createTestTrainer("test.trainer", "Test", "Trainer", true);
        testTrainee = createTestTrainee("test.trainee", "Test", "Trainee");
        
        testTraining = new Training();
        testTraining.setTrainer(testTrainer);
        testTraining.setTrainee(testTrainee);
        testTraining.setTrainingName("Test Training");
        testTraining.setTrainingDate(LocalDate.now());
        testTraining.setDuration(60);
        testTraining = trainingRepository.save(testTraining);
    }

    @Test
    void testSuccessfulWorkloadNotification() {
        // Given
        CreateTrainingRequest request = new CreateTrainingRequest(
            testTrainee.getUsername(),
            testTrainer.getUsername(),
            "Yoga Session",
            LocalDate.now(),
            60
        );

        // When
        TrainerWorkloadResponse response = trainingService.saveWithValidation(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.success()).isTrue();
        assertThat(response.message()).contains("successfully");
        
        // Verify JMS message was sent
        verify(jmsTemplate, times(1)).convertAndSend("workload-queue", any(TrainerWorkloadRequest.class));
    }

    @Test
    void testFallbackWhenActiveMQFails() {
        // Given
        doThrow(new RuntimeException("ActiveMQ connection failed"))
            .when(jmsTemplate).convertAndSend(any(String.class), any(TrainerWorkloadRequest.class));

        CreateTrainingRequest request = new CreateTrainingRequest(
            testTrainee.getUsername(),
            testTrainer.getUsername(),
            "Pilates Session",
            LocalDate.now(),
            45
        );

        // When
        TrainerWorkloadResponse response = trainingService.saveWithValidation(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.success()).isTrue();
        assertThat(response.message()).contains("temporarily unavailable");

        // Verify pending workload was saved
        List<PendingWorkload> pendingWorkloads = pendingWorkloadRepository.findAll();
        assertThat(pendingWorkloads).isNotEmpty();
        
        PendingWorkload pendingWorkload = pendingWorkloads.stream()
            .filter(pw -> pw.getTrainerUsername().equals(testTrainer.getUsername()))
            .findFirst()
            .orElseThrow();
        
        assertThat(pendingWorkload.getTrainerUsername()).isEqualTo(testTrainer.getUsername());
        assertThat(pendingWorkload.getTrainerFirstname()).isEqualTo(testTrainer.getFirstName());
        assertThat(pendingWorkload.getTrainerLastname()).isEqualTo(testTrainer.getLastName());
        assertThat(pendingWorkload.getIsActive()).isEqualTo(testTrainer.isActive());
        assertThat(pendingWorkload.getActionType()).isEqualTo(ActionType.ADD);
        assertThat(pendingWorkload.getTrainingDuration()).isEqualTo(45);
    }

    @Test
    void testTrainingUpdateNotification() {
        // Given
        Training oldTraining = testTraining;
        Training updatedTraining = new Training();
        updatedTraining.setId(oldTraining.getId());
        updatedTraining.setTrainer(oldTraining.getTrainer());
        updatedTraining.setTrainee(oldTraining.getTrainee());
        updatedTraining.setTrainingName(oldTraining.getTrainingName());
        updatedTraining.setTrainingDate(oldTraining.getTrainingDate());
        updatedTraining.setDuration(90); // Updated duration

        // When
        TrainerWorkloadResponse response = workloadNotificationService.notifyTrainingUpdated(oldTraining, updatedTraining);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.success()).isTrue();
        
        // Verify JMS message was sent with UPDATE action
        verify(jmsTemplate, times(1)).convertAndSend("workload-queue", any(TrainerWorkloadRequest.class));
    }

    @Test
    void testTrainingDeleteNotification() {
        // Given
        Training trainingToDelete = testTraining;

        // When
        TrainerWorkloadResponse response = workloadNotificationService.notifyTrainingDeleted(trainingToDelete);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.success()).isTrue();
        
        // Verify JMS message was sent with DELETE action
        verify(jmsTemplate, times(1)).convertAndSend("workload-queue", any(TrainerWorkloadRequest.class));
    }

    @Test
    void testInvalidTrainerHandling() {
        // Given
        CreateTrainingRequest request = new CreateTrainingRequest(
            testTrainee.getUsername(),
            "nonexistent.trainer",
            "Test Training",
            LocalDate.now(),
            60
        );

        // When & Then
        try {
            trainingService.saveWithValidation(request);
        } catch (Exception e) {
            assertThat(e.getMessage()).contains("Trainer not found");
        }
        
        // Verify no JMS message was sent
        verify(jmsTemplate, times(0)).convertAndSend(any(String.class), any(TrainerWorkloadRequest.class));
    }

    @Test
    void testInvalidTraineeHandling() {
        // Given
        CreateTrainingRequest request = new CreateTrainingRequest(
            "nonexistent.trainee",
            testTrainer.getUsername(),
            "Test Training",
            LocalDate.now(),
            60
        );

        // When & Then
        try {
            trainingService.saveWithValidation(request);
        } catch (Exception e) {
            assertThat(e.getMessage()).contains("Trainee not found");
        }
        
        // Verify no JMS message was sent
        verify(jmsTemplate, times(0)).convertAndSend(any(String.class), any(TrainerWorkloadRequest.class));
    }

    @Test
    void testPendingWorkloadPersistence() {
        // Given
        doThrow(new RuntimeException("ActiveMQ connection failed"))
            .when(jmsTemplate).convertAndSend(any(String.class), any(TrainerWorkloadRequest.class));

        // Clear existing pending workloads
        pendingWorkloadRepository.deleteAll();

        CreateTrainingRequest request = new CreateTrainingRequest(
            testTrainee.getUsername(),
            testTrainer.getUsername(),
            "Strength Training",
            LocalDate.of(2024, 1, 15),
            120
        );

        // When
        trainingService.saveWithValidation(request);

        // Then
        List<PendingWorkload> pendingWorkloads = pendingWorkloadRepository.findAll();
        assertThat(pendingWorkloads).hasSize(1);
        
        PendingWorkload pendingWorkload = pendingWorkloads.get(0);
        assertThat(pendingWorkload.getTrainerUsername()).isEqualTo(testTrainer.getUsername());
        assertThat(pendingWorkload.getTrainingDate()).isEqualTo(LocalDate.of(2024, 1, 15));
        assertThat(pendingWorkload.getTrainingDuration()).isEqualTo(120);
        assertThat(pendingWorkload.getActionType()).isEqualTo(ActionType.ADD);
        assertThat(pendingWorkload.getCreatedAt()).isNotNull();
    }

    private Trainer createTestTrainer(String username, String firstName, String lastName, boolean isActive) {
        Trainer trainer = new Trainer();
        trainer.setUsername(username);
        trainer.setFirstName(firstName);
        trainer.setLastName(lastName);
        trainer.setActive(isActive);
        trainer.setPassword("password123");
        return trainerRepository.save(trainer);
    }

    private Trainee createTestTrainee(String username, String firstName, String lastName) {
        Trainee trainee = new Trainee();
        trainee.setUsername(username);
        trainee.setFirstName(firstName);
        trainee.setLastName(lastName);
        trainee.setPassword("password123");
        return traineeRepository.save(trainee);
    }
} 