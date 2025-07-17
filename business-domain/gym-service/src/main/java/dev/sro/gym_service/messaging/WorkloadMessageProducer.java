package dev.sro.gym_service.messaging;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import dev.sro.gym_service.dtos.v1.request.workload.TrainerWorkloadRequest;
import dev.sro.gym_service.entity.PendingWorkload;
import dev.sro.gym_service.entity.Trainer;
import dev.sro.gym_service.entity.enums.ActionType;
import dev.sro.gym_service.repository.PendingWorkloadRepository;
import dev.sro.gym_service.repository.TrainerRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;

@Component
public class WorkloadMessageProducer {

    private final JmsTemplate jmsTemplate;
    private final PendingWorkloadRepository pendingWorkloadRepository;
    private final TrainerRepository trainerRepository;

    public WorkloadMessageProducer(JmsTemplate jmsTemplate, PendingWorkloadRepository pendingWorkloadRepository, TrainerRepository trainerRepository) {
        this.jmsTemplate = jmsTemplate;
        this.pendingWorkloadRepository = pendingWorkloadRepository;
        this.trainerRepository = trainerRepository;
    }
    
    @CircuitBreaker(name = "activemq-producer", fallbackMethod = "fallbackSendWorkloadMessage")
    @TimeLimiter(name = "activemq-producer")
    public void sendWorkloadMessage(TrainerWorkloadRequest request) {
        jmsTemplate.convertAndSend("workload-queue", request);
    }

    public void fallbackSendWorkloadMessage(TrainerWorkloadRequest request, Throwable throwable) {
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
    }
}
