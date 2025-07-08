package dev.sro.workload_service.domain.repository;

import dev.sro.workload_service.domain.entity.Trainer;

import java.util.Optional;

public interface TrainerRepository {
    
    Optional<Trainer> findByUsername(String username);
    
    Trainer save(Trainer trainer);
    
    void delete(Trainer trainer);
    
    boolean existsByUsername(String username);
} 