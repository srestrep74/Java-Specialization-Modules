package dev.sro.gym_service.service;

import dev.sro.gym_service.entity.Training;
import dev.sro.gym_service.dtos.v1.request.workload.TrainerWorkloadRequest.ActionType;

public interface WorkloadNotificationService {
    
    void notifyTrainingCreated(Training training);
    
    void notifyTrainingUpdated(Training oldTraining, Training newTraining);
    
    void notifyTrainingDeleted(Training training);
    
    void sendWorkloadNotification(Training training, ActionType actionType);
} 