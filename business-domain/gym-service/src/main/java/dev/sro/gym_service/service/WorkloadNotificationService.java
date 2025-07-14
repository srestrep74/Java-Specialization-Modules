package dev.sro.gym_service.service;

import dev.sro.gym_service.entity.Training;
import dev.sro.gym_service.dtos.v1.request.workload.TrainerWorkloadRequest.ActionType;
import dev.sro.gym_service.dtos.v1.response.workload.TrainerWorkloadResponse;

public interface WorkloadNotificationService {
    
    TrainerWorkloadResponse notifyTrainingCreated(Training training);
    
    void notifyTrainingUpdated(Training oldTraining, Training newTraining);
    
    TrainerWorkloadResponse notifyTrainingDeleted(Training training);
    
    TrainerWorkloadResponse sendWorkloadNotification(Training training, ActionType actionType);
} 