package com.sro.SpringCoreTask1.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sro.SpringCoreTask1.entity.TrainingType;

public interface TrainingTypeRepository extends JpaRepository<TrainingType, Long> {
    
    Optional<TrainingType> findByTrainingTypeName(String trainingTypeName);

    boolean existsByTrainingTypeName(String trainingTypeName);
}
