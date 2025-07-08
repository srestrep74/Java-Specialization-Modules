package dev.sro.workload_service.infrastructure.repository.jpa;

import dev.sro.workload_service.domain.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrainerJpaRepository extends JpaRepository<Trainer, String> {
    
    Optional<Trainer> findByUsername(String username);
    
    boolean existsByUsername(String username);
} 