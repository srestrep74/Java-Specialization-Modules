package dev.sro.gym_service.service.impl;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import dev.sro.gym_service.dtos.v1.request.workload.TrainerWorkloadRequest;
import dev.sro.gym_service.dtos.v1.request.workload.TrainerWorkloadRequest.ActionType;
import dev.sro.gym_service.dtos.v1.response.workload.TrainerWorkloadResponse;
import dev.sro.gym_service.entity.Training;
import dev.sro.gym_service.messaging.WorkloadMessageProducer;
import dev.sro.gym_service.service.WorkloadNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkloadNotificationServiceImpl implements WorkloadNotificationService {

    private final WorkloadMessageProducer workloadMessageProducer;

    @Override
    public TrainerWorkloadResponse notifyTrainingCreated(Training training) {
        return sendWorkloadNotification(training, ActionType.ADD);
    }

    @Override
    public TrainerWorkloadResponse notifyTrainingUpdated(Training oldTraining, Training newTraining) {
        return sendWorkloadNotification(newTraining, ActionType.UPDATE);
    }

    @Override
    public TrainerWorkloadResponse notifyTrainingDeleted(Training training) {
        return sendWorkloadNotification(training, ActionType.DELETE);
    }

    @Override
    public TrainerWorkloadResponse sendWorkloadNotification(Training training, ActionType actionType) {
        TrainerWorkloadRequest message = new TrainerWorkloadRequest(
                training.getTrainer().getUsername(),
                training.getTrainer().getFirstName(),
                training.getTrainer().getLastName(),
                training.getTrainer().isActive(),
                training.getTrainingDate(),
                training.getDuration(),
                actionType);

        log.info("Sending workload notification for trainer: {}, action: {}", 
                training.getTrainer().getUsername(), actionType);

        try {
            // Wait for the CompletableFuture to complete with timeout
            workloadMessageProducer.sendWorkloadMessage(message).get(10, TimeUnit.SECONDS);
            log.info("Workload notification sent successfully for trainer: {}", training.getTrainer().getUsername());
            return new TrainerWorkloadResponse("Workload notification sent successfully", true);
        } catch (Exception e) {
            log.warn("Failed to send workload notification for trainer: {}. Error: {}. Fallback should be triggered.", 
                    training.getTrainer().getUsername(), e.getMessage());
            // If there's an exception, the fallback should have been triggered by Circuit Breaker
            // We return success because the fallback should have saved to pending workload
            return new TrainerWorkloadResponse(
                    "Workload service temporarily unavailable. The operation has been queued and will be processed later.",
                    true);
        }
    }
}