package dev.sro.workload_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.sro.workload_service.entity.Trainer;

import java.util.Optional;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, String> {
    
    Optional<Trainer> findByUsername(String username);
    
    boolean existsByUsername(String username);
} 