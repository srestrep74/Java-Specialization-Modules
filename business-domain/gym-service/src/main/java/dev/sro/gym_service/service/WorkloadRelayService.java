package dev.sro.gym_service.service;

import dev.sro.gym_service.dtos.v1.request.workload.TrainerWorkloadRequest;
import dev.sro.gym_service.entity.PendingWorkload;
import dev.sro.gym_service.repository.PendingWorkloadRepository;
import dev.sro.gym_service.messaging.WorkloadMessageProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkloadRelayService {

    private final PendingWorkloadRepository pendingWorkloadRepository;
    private final WorkloadMessageProducer workloadMessageProducer;

    @Scheduled(fixedRate = 120000)
    @Transactional
    public void processPendingWorkloads() {
        log.info("Checking for pending workloads to relay...");
        List<PendingWorkload> pending = pendingWorkloadRepository.findAll();

        if (pending.isEmpty()) {
            log.info("No pending workloads found.");
            return;
        }

        log.info("Found {} pending workloads. Attempting to process...", pending.size());
        for (PendingWorkload workload : pending) {
            try {
                TrainerWorkloadRequest message = new TrainerWorkloadRequest(
                        workload.getTrainerUsername(),
                        workload.getTrainerFirstname(),
                        workload.getTrainerLastname(),
                        workload.getIsActive(),
                        workload.getTrainingDate(),
                        workload.getTrainingDuration(),
                        TrainerWorkloadRequest.ActionType.valueOf(workload.getActionType().name()));
                workloadMessageProducer.sendWorkloadMessage(message);

                pendingWorkloadRepository.deleteById(workload.getId());
                log.info("Successfully processed and removed workload for trainer: {}", workload.getTrainerUsername());

            } catch (Exception e) {
                log.warn("Failed to process workload for trainer: {}. Will retry later. Error: {}",
                        workload.getTrainerUsername(), e.getMessage());
            }
        }
    }
}