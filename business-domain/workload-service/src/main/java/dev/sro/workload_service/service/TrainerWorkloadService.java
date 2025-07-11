package dev.sro.workload_service.service;

import dev.sro.workload_service.dtos.v1.request.TrainerWorkloadRequest;
import dev.sro.workload_service.dtos.v1.response.TrainerMonthlySummaryResponse;

public interface TrainerWorkloadService {
    
    void processTrainerWorkload(TrainerWorkloadRequest request);
    
    TrainerMonthlySummaryResponse getTrainerMonthlySummary(String trainerUsername);
    
    TrainerMonthlySummaryResponse getTrainerMonthlySummary(String trainerUsername, Integer year);
    
    TrainerMonthlySummaryResponse getTrainerMonthlySummary(String trainerUsername, Integer year, Integer month);
} 