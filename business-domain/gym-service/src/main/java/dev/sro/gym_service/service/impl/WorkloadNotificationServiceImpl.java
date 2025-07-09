package dev.sro.gym_service.service.impl;

import dev.sro.gym_service.client.WorkloadServiceClient;
import dev.sro.gym_service.dtos.v1.request.workload.TrainerWorkloadRequest;
import dev.sro.gym_service.dtos.v1.request.workload.TrainerWorkloadRequest.ActionType;
import dev.sro.gym_service.dtos.v1.response.workload.TrainerWorkloadResponse;
import dev.sro.gym_service.entity.Training;
import dev.sro.gym_service.service.WorkloadNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkloadNotificationServiceImpl implements WorkloadNotificationService {
    
    private final WorkloadServiceClient workloadServiceClient;
    
    @Override
    public void notifyTrainingCreated(Training training) {
        log.info("Notifying workload service about training creation: {}", training.getId());
        sendWorkloadNotification(training, ActionType.ADD);
    }
    
    @Override
    public void notifyTrainingUpdated(Training oldTraining, Training newTraining) {
        log.info("Notifying workload service about training update: {}", newTraining.getId());
        
        // If training date or duration changed, we need to handle it
        if (!oldTraining.getTrainingDate().equals(newTraining.getTrainingDate()) ||
            oldTraining.getDuration() != newTraining.getDuration()) {
            
            // Remove old training data
            sendWorkloadNotification(oldTraining, ActionType.DELETE);
            
            // Add new training data
            sendWorkloadNotification(newTraining, ActionType.ADD);
        }
    }
    
    @Override
    public void notifyTrainingDeleted(Training training) {
        log.info("Notifying workload service about training deletion: {}", training.getId());
        sendWorkloadNotification(training, ActionType.DELETE);
    }
    
    @Override
    public void sendWorkloadNotification(Training training, ActionType actionType) {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                training.getTrainer().getUsername(),
                training.getTrainer().getFirstName(),
                training.getTrainer().getLastName(),
                training.getTrainer().isActive(),
                training.getTrainingDate(),
                training.getDuration(),
                actionType
        );
        
        try {
            TrainerWorkloadResponse response = workloadServiceClient.processTrainerWorkload(request);
            log.info("Successfully notified workload service for trainer: {} with action: {}. Response: {}", 
                training.getTrainer().getUsername(), actionType, response.message());
        } catch (Exception e) {
            log.error("Failed to notify workload service for trainer: {} with action: {}. Error: {}", 
                training.getTrainer().getUsername(), actionType, e.getMessage());
            // Don't rethrow the exception - let the main operation continue
        }
    }
} 