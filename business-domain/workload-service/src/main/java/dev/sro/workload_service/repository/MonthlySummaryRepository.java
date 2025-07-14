package dev.sro.workload_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.sro.workload_service.entity.MonthlySummary;

import java.util.List;
import java.util.Optional;

@Repository
public interface MonthlySummaryRepository extends JpaRepository<MonthlySummary, Long> {
    
    Optional<MonthlySummary> findByTrainer_UsernameAndYearAndMonth(
        String trainerUsername, 
        Integer year, 
        Integer month
    );
    
    List<MonthlySummary> findByTrainer_Username(String trainerUsername);
    
    List<MonthlySummary> findByTrainer_UsernameAndYear(String trainerUsername, Integer year);
} 