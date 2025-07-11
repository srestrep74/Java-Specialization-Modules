package dev.sro.workload_service.application.service;

import dev.sro.workload_service.presentation.dto.request.TrainerWorkloadRequest;
import dev.sro.workload_service.presentation.dto.response.TrainerMonthlySummaryResponse;

public interface TrainerWorkloadService {
    
    void processTrainerWorkload(TrainerWorkloadRequest request);
    
    TrainerMonthlySummaryResponse getTrainerMonthlySummary(String trainerUsername);
    
    TrainerMonthlySummaryResponse getTrainerMonthlySummary(String trainerUsername, Integer year);
    
    TrainerMonthlySummaryResponse getTrainerMonthlySummary(String trainerUsername, Integer year, Integer month);
} 