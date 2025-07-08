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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TrainerWorkloadServiceImpl implements TrainerWorkloadService {
    
    private final TrainerRepository trainerRepository;
    private final TrainingSessionRepository trainingSessionRepository;
    private final MonthlySummaryRepository monthlySummaryRepository;
    
    @Override
    public void processTrainerWorkload(TrainerWorkloadRequest request) {
        log.info("Processing trainer workload for username: {}, action: {}", 
            request.getTrainerUsername(), request.getActionType());
        
        // Get or create trainer
        Trainer trainer = getOrCreateTrainer(request);
        
        // Create training session record
        TrainingSession trainingSession = createTrainingSession(trainer, request);
        trainingSessionRepository.save(trainingSession);
        
        // Update monthly summary
        updateMonthlySummary(trainer, request);
        
        log.info("Successfully processed trainer workload for username: {}", 
            request.getTrainerUsername());
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
        
        return buildTrainerMonthlySummaryResponse(trainer, summaries);
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
        
        return buildTrainerMonthlySummaryResponse(trainer, summaries);
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
        return buildTrainerMonthlySummaryResponse(trainer, summaries);
    }
    
    private Trainer getOrCreateTrainer(TrainerWorkloadRequest request) {
        return trainerRepository.findByUsername(request.getTrainerUsername())
            .map(trainer -> {
                // Update trainer info if needed
                trainer.updateProfile(
                    request.getTrainerFirstName(),
                    request.getTrainerLastName(),
                    request.getIsActive()
                );
                return trainerRepository.save(trainer);
            })
            .orElseGet(() -> {
                // Create new trainer
                Trainer newTrainer = Trainer.builder()
                    .username(request.getTrainerUsername())
                    .firstName(request.getTrainerFirstName())
                    .lastName(request.getTrainerLastName())
                    .isActive(request.getIsActive())
                    .build();
                return trainerRepository.save(newTrainer);
            });
    }
    
    private TrainingSession createTrainingSession(Trainer trainer, TrainerWorkloadRequest request) {
        return TrainingSession.builder()
            .trainer(trainer)
            .trainingDate(request.getTrainingDate())
            .trainingDuration(request.getTrainingDuration())
            .actionType(request.getActionType())
            .build();
    }
    
    private void updateMonthlySummary(Trainer trainer, TrainerWorkloadRequest request) {
        Integer year = request.getTrainingDate().getYear();
        Integer month = request.getTrainingDate().getMonthValue();
        
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
        
        if (request.getActionType() == ActionType.ADD) {
            summary.addDuration(request.getTrainingDuration());
        } else if (request.getActionType() == ActionType.DELETE) {
            summary.subtractDuration(request.getTrainingDuration());
        }
        
        monthlySummaryRepository.save(summary);
    }
    
    private TrainerMonthlySummaryResponse buildTrainerMonthlySummaryResponse(
        Trainer trainer, 
        List<MonthlySummary> summaries
    ) {
        Map<Integer, List<MonthlySummary>> summariesByYear = summaries.stream()
            .collect(Collectors.groupingBy(MonthlySummary::getYear));
        
        List<TrainerMonthlySummaryResponse.YearSummary> yearSummaries = summariesByYear.entrySet()
            .stream()
            .map(entry -> {
                Integer year = entry.getKey();
                List<MonthlySummary> yearSummariesList = entry.getValue();
                
                List<TrainerMonthlySummaryResponse.MonthSummary> monthSummaries = yearSummariesList
                    .stream()
                    .map(summary -> TrainerMonthlySummaryResponse.MonthSummary.builder()
                        .month(summary.getMonth())
                        .trainingSummaryDuration(summary.getTotalDuration())
                        .build())
                    .collect(Collectors.toList());
                
                return TrainerMonthlySummaryResponse.YearSummary.builder()
                    .year(year)
                    .months(monthSummaries)
                    .build();
            })
            .collect(Collectors.toList());
        
        return TrainerMonthlySummaryResponse.builder()
            .trainerUsername(trainer.getUsername())
            .trainerFirstName(trainer.getFirstName())
            .trainerLastName(trainer.getLastName())
            .trainerStatus(trainer.getIsActive())
            .years(yearSummaries)
            .build();
    }
    
    private TrainerMonthlySummaryResponse createEmptyResponse(String trainerUsername) {
        return TrainerMonthlySummaryResponse.builder()
            .trainerUsername(trainerUsername)
            .trainerFirstName("")
            .trainerLastName("")
            .trainerStatus(false)
            .years(new ArrayList<>())
            .build();
    }
} 