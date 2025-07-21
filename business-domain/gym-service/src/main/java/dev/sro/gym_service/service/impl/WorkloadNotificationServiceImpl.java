package dev.sro.gym_service.service.impl;

import dev.sro.gym_service.dtos.v1.request.workload.TrainerWorkloadRequest;
import dev.sro.gym_service.dtos.v1.request.workload.TrainerWorkloadRequest.ActionType;
import dev.sro.gym_service.dtos.v1.response.workload.TrainerWorkloadResponse;
import dev.sro.gym_service.entity.Training;
import dev.sro.gym_service.messaging.WorkloadMessageProducer;
import dev.sro.gym_service.service.WorkloadNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkloadNotificationServiceImpl implements WorkloadNotificationService {

    private final WorkloadMessageProducer workloadMessageProducer;

    @Override
    public TrainerWorkloadResponse notifyTrainingCreated(Training training) {
        return sendWorkloadNotification(training, ActionType.ADD);
    }

    @Override
    public void notifyTrainingUpdated(Training oldTraining, Training newTraining) {
        if (!oldTraining.getTrainingDate().equals(newTraining.getTrainingDate()) ||
                oldTraining.getDuration() != newTraining.getDuration()) {

            sendWorkloadNotification(oldTraining, ActionType.DELETE);

            sendWorkloadNotification(newTraining, ActionType.ADD);
        }
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

        try {
            workloadMessageProducer.sendWorkloadMessage(message);
            return new TrainerWorkloadResponse("Workload notification sent successfully", true);
        } catch (Exception e) {
            return new TrainerWorkloadResponse(
                    "Workload service temporarily unavailable. The operation has been queued and will be processed later.",
                    true);
        }
    }
}