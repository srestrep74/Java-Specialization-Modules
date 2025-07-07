package dev.sro.gym_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.sro.gym_service.entity.TrainingType;

public interface TrainingTypeRepository extends JpaRepository<TrainingType, Long> {

}
