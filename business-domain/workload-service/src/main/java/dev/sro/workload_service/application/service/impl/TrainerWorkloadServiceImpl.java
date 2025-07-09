package dev.sro.workload_service.application.service.impl;

import dev.sro.workload_service.application.dto.request.TrainerWorkloadRequest;
import dev.sro.workload_service.application.dto.response.TrainerMonthlySummaryResponse;
import dev.sro.workload_service.application.service.TrainerWorkloadService;
import dev.sro.workload_service.domain.entity.MonthlySummary;
import dev.sro.workload_service.domain.entity.Trainer;
import dev.sro.workload_service.domain.entity.TrainingSession;
import dev.sro.workload_service.domain.enums.ActionType;
import dev.sro.workload_service.domain.repository.MonthlySummaryRepository;
import dev.sro.workload_service.domain.repository.TrainerRepository;
import dev.sro.workload_service.domain.repository.TrainingSessionRepository;
import dev.sro.workload_service.application.mapper.TrainerMapper;
import dev.sro.workload_service.application.mapper.TrainingSessionMapper;
import dev.sro.workload_service.application.mapper.TrainerMonthlySummaryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TrainerWorkloadServiceImpl implements TrainerWorkloadService {
    
    private final TrainerRepository trainerRepository;
    private final TrainingSessionRepository trainingSessionRepository;
    private final MonthlySummaryRepository monthlySummaryRepository;
    private final TrainerMapper trainerMapper;
    private final TrainingSessionMapper trainingSessionMapper;
    private final TrainerMonthlySummaryMapper trainerMonthlySummaryMapper;
    
    @Override
    public void processTrainerWorkload(TrainerWorkloadRequest request) {
        log.info("Processing trainer workload for username: {}, action: {}", 
            request.trainerUsername(), request.actionType());
        
        Trainer trainer = getOrCreateTrainer(request);
        
        TrainingSession trainingSession = trainingSessionMapper.toTrainingSession(request);
        trainingSession.setTrainer(trainer);
        trainingSessionRepository.save(trainingSession);
        
        updateMonthlySummary(trainer, request);
        
        log.info("Successfully processed trainer workload for username: {}", 
            request.trainerUsername());
    }
    
    @Override
    @Transactional(readOnly = true)
    public TrainerMonthlySummaryResponse getTrainerMonthlySummary(String trainerUsername) {
        log.info("Getting trainer monthly summary for username: {}", trainerUsername);
        
        Optional<Trainer> trainerOpt = trainerRepository.findByUsername(trainerUsername);
        if (trainerOpt.isEmpty()) {
            log.warn("Trainer not found with username: {}", trainerUsername);
            return createEmptyResponse(trainerUsername);
        }
        
        Trainer trainer = trainerOpt.get();
        List<MonthlySummary> summaries = monthlySummaryRepository.findByTrainerUsername(trainerUsername);
        
        return trainerMonthlySummaryMapper.toResponse(trainer, summaries);
    }
    
    @Override
    @Transactional(readOnly = true)
    public TrainerMonthlySummaryResponse getTrainerMonthlySummary(String trainerUsername, Integer year) {
        log.info("Getting trainer monthly summary for username: {}, year: {}", trainerUsername, year);
        
        Optional<Trainer> trainerOpt = trainerRepository.findByUsername(trainerUsername);
        if (trainerOpt.isEmpty()) {
            log.warn("Trainer not found with username: {}", trainerUsername);
            return createEmptyResponse(trainerUsername);
        }
        
        Trainer trainer = trainerOpt.get();
        List<MonthlySummary> summaries = monthlySummaryRepository.findByTrainerUsernameAndYear(trainerUsername, year);
        
        return trainerMonthlySummaryMapper.toResponse(trainer, summaries);
    }
    
    @Override
    @Transactional(readOnly = true)
    public TrainerMonthlySummaryResponse getTrainerMonthlySummary(String trainerUsername, Integer year, Integer month) {
        log.info("Getting trainer monthly summary for username: {}, year: {}, month: {}", 
            trainerUsername, year, month);
        
        Optional<Trainer> trainerOpt = trainerRepository.findByUsername(trainerUsername);
        if (trainerOpt.isEmpty()) {
            log.warn("Trainer not found with username: {}", trainerUsername);
            return createEmptyResponse(trainerUsername);
        }
        
        Trainer trainer = trainerOpt.get();
        Optional<MonthlySummary> summaryOpt = monthlySummaryRepository
            .findByTrainerUsernameAndYearAndMonth(trainerUsername, year, month);
        
        List<MonthlySummary> summaries = summaryOpt.map(List::of).orElse(List.of());
        return trainerMonthlySummaryMapper.toResponse(trainer, summaries);
    }
    
    private Trainer getOrCreateTrainer(TrainerWorkloadRequest request) {
        return trainerRepository.findByUsername(request.trainerUsername())
            .map(trainer -> {
                trainer.updateProfile(
                    request.trainerFirstName(),
                    request.trainerLastName(),
                    request.isActive()
                );
                return trainerRepository.save(trainer);
            })
            .orElseGet(() -> {
                Trainer newTrainer = trainerMapper.toTrainer(request);
                return trainerRepository.save(newTrainer);
            });
    }
    
    private void updateMonthlySummary(Trainer trainer, TrainerWorkloadRequest request) {
        Integer year = request.trainingDate().getYear();
        Integer month = request.trainingDate().getMonthValue();
        
        MonthlySummary summary = monthlySummaryRepository
            .findByTrainerUsernameAndYearAndMonth(trainer.getUsername(), year, month)
            .orElseGet(() -> {
                MonthlySummary newSummary = MonthlySummary.builder()
                    .trainer(trainer)
                    .year(year)
                    .month(month)
                    .totalDuration(0)
                    .build();
                return monthlySummaryRepository.save(newSummary);
            });
        
        if (request.actionType() == ActionType.ADD) {
            summary.addDuration(request.trainingDuration());
        } else if (request.actionType() == ActionType.DELETE) {
            summary.subtractDuration(request.trainingDuration());
        }
        
        monthlySummaryRepository.save(summary);
    }
    
    private TrainerMonthlySummaryResponse createEmptyResponse(String trainerUsername) {
        return new TrainerMonthlySummaryResponse(trainerUsername, null, null, null, new ArrayList<>());
    }
} 