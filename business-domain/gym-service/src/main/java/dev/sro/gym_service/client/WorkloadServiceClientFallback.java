package dev.sro.gym_service.client;

import dev.sro.gym_service.dtos.v1.request.workload.TrainerWorkloadRequest;
import dev.sro.gym_service.dtos.v1.response.workload.TrainerWorkloadResponse;
import dev.sro.gym_service.entity.PendingWorkload;
import dev.sro.gym_service.entity.Trainer;
import dev.sro.gym_service.entity.enums.ActionType;
import dev.sro.gym_service.repository.PendingWorkloadRepository;
import dev.sro.gym_service.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class WorkloadServiceClientFallback implements WorkloadServiceClient {

    private final PendingWorkloadRepository pendingWorkloadRepository;
    private final TrainerRepository trainerRepository;

    @Override
    public TrainerWorkloadResponse processTrainerWorkload(TrainerWorkloadRequest request) {
        log.warn(
                "Workload service is not available. Fallback executed for trainer: {} with action: {}. Saving to outbox.",
                request.trainerUsername(), request.actionType());

        try {
            Trainer trainer = trainerRepository.findByUsername(request.trainerUsername())
                    .orElseThrow(() -> new RuntimeException("Trainer not found: " + request.trainerUsername()));

            PendingWorkload pendingWorkload = PendingWorkload.builder()
                    .trainerUsername(request.trainerUsername())
                    .trainerFirstname(trainer.getFirstName())
                    .trainerLastname(trainer.getLastName())
                    .isActive(trainer.isActive())
                    .trainingDate(request.trainingDate())
                    .trainingDuration(request.trainingDuration())
                    .actionType(ActionType.valueOf(request.actionType().name()))
                    .build();

            pendingWorkloadRepository.save(pendingWorkload);
            log.info("Successfully saved workload for trainer {} to outbox.", request.trainerUsername());
        } catch (DataIntegrityViolationException e) {
            log.warn("Duplicate workload request detected for trainer {}. It's already in the outbox.",
                    request.trainerUsername());
        } catch (Exception e) {
            log.error("CRITICAL: Failed to save workload to outbox for trainer {}. Data might be lost!",
                    request.trainerUsername(), e);
        }

        return new TrainerWorkloadResponse(
                "Workload service temporarily unavailable. The operation has been queued and will be processed later.",
                true);
    }
}