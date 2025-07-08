package dev.sro.workload_service.infrastructure.repository.jpa;

import dev.sro.workload_service.domain.entity.MonthlySummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MonthlySummaryJpaRepository extends JpaRepository<MonthlySummary, Long> {
    
    Optional<MonthlySummary> findByTrainerUsernameAndYearAndMonth(
        String trainerUsername, 
        Integer year, 
        Integer month
    );
    
    List<MonthlySummary> findByTrainerUsername(String trainerUsername);
    
    List<MonthlySummary> findByTrainerUsernameAndYear(String trainerUsername, Integer year);
} 