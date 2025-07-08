package dev.sro.workload_service.infrastructure.repository;

import dev.sro.workload_service.domain.entity.Trainer;
import dev.sro.workload_service.domain.repository.TrainerRepository;
import dev.sro.workload_service.infrastructure.repository.jpa.TrainerJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TrainerRepositoryImpl implements TrainerRepository {
    
    private final TrainerJpaRepository trainerJpaRepository;
    
    @Override
    public Optional<Trainer> findByUsername(String username) {
        return trainerJpaRepository.findByUsername(username);
    }
    
    @Override
    public Trainer save(Trainer trainer) {
        return trainerJpaRepository.save(trainer);
    }
    
    @Override
    public void delete(Trainer trainer) {
        trainerJpaRepository.delete(trainer);
    }
    
    @Override
    public boolean existsByUsername(String username) {
        return trainerJpaRepository.existsByUsername(username);
    }
} 