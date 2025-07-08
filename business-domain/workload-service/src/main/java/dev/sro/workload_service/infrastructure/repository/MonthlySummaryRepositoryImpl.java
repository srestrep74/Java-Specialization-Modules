package dev.sro.workload_service.infrastructure.repository;

import dev.sro.workload_service.domain.entity.MonthlySummary;
import dev.sro.workload_service.domain.repository.MonthlySummaryRepository;
import dev.sro.workload_service.infrastructure.repository.jpa.MonthlySummaryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MonthlySummaryRepositoryImpl implements MonthlySummaryRepository {
    
    private final MonthlySummaryJpaRepository monthlySummaryJpaRepository;
    
    @Override
    public Optional<MonthlySummary> findByTrainerUsernameAndYearAndMonth(
        String trainerUsername, 
        Integer year, 
        Integer month
    ) {
        return monthlySummaryJpaRepository.findByTrainerUsernameAndYearAndMonth(
            trainerUsername, year, month
        );
    }
    
    @Override
    public List<MonthlySummary> findByTrainerUsername(String trainerUsername) {
        return monthlySummaryJpaRepository.findByTrainerUsername(trainerUsername);
    }
    
    @Override
    public List<MonthlySummary> findByTrainerUsernameAndYear(String trainerUsername, Integer year) {
        return monthlySummaryJpaRepository.findByTrainerUsernameAndYear(trainerUsername, year);
    }
    
    @Override
    public MonthlySummary save(MonthlySummary monthlySummary) {
        return monthlySummaryJpaRepository.save(monthlySummary);
    }
    
    @Override
    public void delete(MonthlySummary monthlySummary) {
        monthlySummaryJpaRepository.delete(monthlySummary);
    }
} 