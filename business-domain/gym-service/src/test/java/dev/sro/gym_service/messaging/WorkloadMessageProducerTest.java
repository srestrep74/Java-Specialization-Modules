package dev.sro.gym_service.messaging;

import dev.sro.gym_service.dtos.v1.request.workload.TrainerWorkloadRequest;
import dev.sro.gym_service.entity.PendingWorkload;
import dev.sro.gym_service.entity.Trainer;
import dev.sro.gym_service.entity.enums.ActionType;
import dev.sro.gym_service.repository.PendingWorkloadRepository;
import dev.sro.gym_service.repository.TrainerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;

import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkloadMessageProducerTest {

    @Mock
    private JmsTemplate jmsTemplate;

    @Mock
    private PendingWorkloadRepository pendingWorkloadRepository;

    @Mock
    private TrainerRepository trainerRepository;

    private WorkloadMessageProducer producer;

    private TrainerWorkloadRequest validRequest;
    private Trainer mockTrainer;

    @BeforeEach
    void setUp() {
        producer = new WorkloadMessageProducer(jmsTemplate, pendingWorkloadRepository, trainerRepository);

        validRequest = new TrainerWorkloadRequest(
                "test.trainer",
                "John",
                "Doe",
                true,
                LocalDate.of(2024, 1, 15),
                60,
                TrainerWorkloadRequest.ActionType.ADD);

        mockTrainer = new Trainer();
        mockTrainer.setUsername("test.trainer");
        mockTrainer.setFirstName("John");
        mockTrainer.setLastName("Doe");
        mockTrainer.setActive(true);
    }

    @Test
    void shouldSendWorkloadMessageSuccessfully() throws ExecutionException, InterruptedException {
        doNothing().when(jmsTemplate).convertAndSend(eq("workload-queue"), eq(validRequest));

        CompletableFuture<Void> future = producer.sendWorkloadMessage(validRequest);

        assertNotNull(future);
        future.get();

        verify(jmsTemplate, times(1)).convertAndSend("workload-queue", validRequest);
        verifyNoInteractions(pendingWorkloadRepository, trainerRepository);
    }

    @Test
    void shouldHandleJmsExceptionAndTriggerFallback() throws ExecutionException, InterruptedException {
        when(trainerRepository.findByUsername("test.trainer"))
                .thenReturn(Optional.of(mockTrainer));

        when(pendingWorkloadRepository.save(any(PendingWorkload.class)))
                .thenReturn(new PendingWorkload());

        CompletableFuture<Void> future = producer.fallbackSendWorkloadMessage(validRequest,
                new RuntimeException("JMS connection failed"));

        assertNotNull(future);
        future.get();

        verify(trainerRepository, times(1)).findByUsername("test.trainer");
        verify(pendingWorkloadRepository, times(1)).save(any(PendingWorkload.class));
        verifyNoInteractions(jmsTemplate);
    }

    @Test
    void shouldHandleFallbackWithTrainerNotFound() {
        when(trainerRepository.findByUsername("test.trainer"))
                .thenReturn(Optional.empty());

        CompletableFuture<Void> future = producer.fallbackSendWorkloadMessage(validRequest,
                new RuntimeException("JMS connection failed"));

        ExecutionException exception = assertThrows(ExecutionException.class, () -> future.get());
        assertTrue(exception.getCause() instanceof RuntimeException);
        assertTrue(exception.getCause().getMessage().contains("Trainer not found: test.trainer"));

        verify(trainerRepository, times(1)).findByUsername("test.trainer");
        verifyNoInteractions(pendingWorkloadRepository, jmsTemplate);
    }

    @Test
    void shouldHandleFallbackWithDatabaseError() throws ExecutionException, InterruptedException {
        when(trainerRepository.findByUsername("test.trainer"))
                .thenReturn(Optional.of(mockTrainer));

        when(pendingWorkloadRepository.save(any(PendingWorkload.class)))
                .thenThrow(new RuntimeException("Database error"));

        CompletableFuture<Void> future = producer.fallbackSendWorkloadMessage(validRequest,
                new RuntimeException("JMS connection failed"));

        ExecutionException exception = assertThrows(ExecutionException.class, () -> future.get());
        assertTrue(exception.getCause() instanceof RuntimeException);
        assertTrue(exception.getCause().getMessage().contains("Database error"));

        verify(trainerRepository, times(1)).findByUsername("test.trainer");
        verify(pendingWorkloadRepository, times(1)).save(any(PendingWorkload.class));
    }

    @Test
    void shouldCreateCorrectPendingWorkloadInFallback() throws ExecutionException, InterruptedException {
        when(trainerRepository.findByUsername("test.trainer"))
                .thenReturn(Optional.of(mockTrainer));

        when(pendingWorkloadRepository.save(any(PendingWorkload.class)))
                .thenAnswer(invocation -> {
                    PendingWorkload saved = invocation.getArgument(0);
                    return saved;
                });

        CompletableFuture<Void> future = producer.fallbackSendWorkloadMessage(validRequest,
                new RuntimeException("JMS connection failed"));
        future.get();

        verify(pendingWorkloadRepository, times(1))
                .save(argThat(pendingWorkload -> pendingWorkload.getTrainerUsername().equals("test.trainer") &&
                        pendingWorkload.getTrainerFirstname().equals("John") &&
                        pendingWorkload.getTrainerLastname().equals("Doe") &&
                        pendingWorkload.getIsActive().equals(true) &&
                        pendingWorkload.getTrainingDate().equals(LocalDate.of(2024, 1, 15)) &&
                        pendingWorkload.getTrainingDuration().equals(60) &&
                        pendingWorkload.getActionType().equals(ActionType.ADD)));
    }

    @Test
    void shouldHandleDifferentActionTypes() throws ExecutionException, InterruptedException {
        TrainerWorkloadRequest deleteRequest = new TrainerWorkloadRequest(
                "test.trainer",
                "John",
                "Doe",
                true,
                LocalDate.of(2024, 1, 15),
                60,
                TrainerWorkloadRequest.ActionType.DELETE);

        when(trainerRepository.findByUsername("test.trainer"))
                .thenReturn(Optional.of(mockTrainer));

        when(pendingWorkloadRepository.save(any(PendingWorkload.class)))
                .thenReturn(new PendingWorkload());

        CompletableFuture<Void> future = producer.fallbackSendWorkloadMessage(deleteRequest,
                new RuntimeException("JMS connection failed"));
        future.get();

        verify(pendingWorkloadRepository, times(1))
                .save(argThat(pendingWorkload -> pendingWorkload.getActionType().equals(ActionType.DELETE)));
    }

    @Test
    void shouldHandleInactiveTrainer() throws ExecutionException, InterruptedException {
        Trainer inactiveTrainer = new Trainer();
        inactiveTrainer.setUsername("inactive.trainer");
        inactiveTrainer.setFirstName("Jane");
        inactiveTrainer.setLastName("Smith");
        inactiveTrainer.setActive(false);

        TrainerWorkloadRequest inactiveRequest = new TrainerWorkloadRequest(
                "inactive.trainer",
                "Jane",
                "Smith",
                false,
                LocalDate.of(2024, 1, 15),
                30,
                TrainerWorkloadRequest.ActionType.DELETE);

        when(trainerRepository.findByUsername("inactive.trainer"))
                .thenReturn(Optional.of(inactiveTrainer));

        when(pendingWorkloadRepository.save(any(PendingWorkload.class)))
                .thenReturn(new PendingWorkload());

        CompletableFuture<Void> future = producer.fallbackSendWorkloadMessage(inactiveRequest,
                new RuntimeException("JMS connection failed"));
        future.get();

        verify(pendingWorkloadRepository, times(1))
                .save(argThat(pendingWorkload -> pendingWorkload.getTrainerUsername().equals("inactive.trainer") &&
                        pendingWorkload.getIsActive().equals(false) &&
                        pendingWorkload.getActionType().equals(ActionType.DELETE)));
    }

    @Test
    void shouldHandleNullRequest() {
        TrainerWorkloadRequest nullRequest = null;

        CompletableFuture<Void> future = producer.sendWorkloadMessage(nullRequest);

        assertNotNull(future);

        verifyNoInteractions(jmsTemplate, trainerRepository, pendingWorkloadRepository);
    }

    @Test
    void shouldHandleFallbackMethodDirectly() throws ExecutionException, InterruptedException {
        when(trainerRepository.findByUsername("test.trainer"))
                .thenReturn(Optional.of(mockTrainer));

        when(pendingWorkloadRepository.save(any(PendingWorkload.class)))
                .thenReturn(new PendingWorkload());

        CompletableFuture<Void> future = producer.fallbackSendWorkloadMessage(validRequest,
                new RuntimeException("Test exception"));
        future.get();

        verify(trainerRepository, times(1)).findByUsername("test.trainer");
        verify(pendingWorkloadRepository, times(1)).save(any(PendingWorkload.class));
        verifyNoInteractions(jmsTemplate);
    }

    @Test
    void shouldHandleFallbackMethodWithTrainerNotFound() {
        when(trainerRepository.findByUsername("test.trainer"))
                .thenReturn(Optional.empty());

        CompletableFuture<Void> future = producer.fallbackSendWorkloadMessage(validRequest,
                new RuntimeException("Test exception"));

        ExecutionException exception = assertThrows(ExecutionException.class, () -> future.get());
        assertTrue(exception.getCause() instanceof RuntimeException);
        assertTrue(exception.getCause().getMessage().contains("Trainer not found: test.trainer"));

        verify(trainerRepository, times(1)).findByUsername("test.trainer");
        verifyNoInteractions(pendingWorkloadRepository, jmsTemplate);
    }

    @Test
    void shouldHandleConcurrentMessageSending() throws ExecutionException, InterruptedException {
        doNothing().when(jmsTemplate).convertAndSend(eq("workload-queue"), any(TrainerWorkloadRequest.class));

        CompletableFuture<Void> future1 = producer.sendWorkloadMessage(validRequest);
        CompletableFuture<Void> future2 = producer.sendWorkloadMessage(validRequest);

        future1.get();
        future2.get();

        verify(jmsTemplate, times(2)).convertAndSend("workload-queue", validRequest);
    }
}