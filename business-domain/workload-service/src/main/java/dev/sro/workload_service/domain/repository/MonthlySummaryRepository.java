package dev.sro.workload_service.domain.repository;

import dev.sro.workload_service.domain.entity.MonthlySummary;

import java.util.List;
import java.util.Optional;

public interface MonthlySummaryRepository {
    
    Optional<MonthlySummary> findByTrainerUsernameAndYearAndMonth(
        String trainerUsername, 
        Integer year, 
        Integer month
    );
    
    List<MonthlySummary> findByTrainerUsername(String trainerUsername);
    
    List<MonthlySummary> findByTrainerUsernameAndYear(String trainerUsername, Integer year);
    
    MonthlySummary save(MonthlySummary monthlySummary);
    
    void delete(MonthlySummary monthlySummary);
} 