package dev.sro.gym_service.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import dev.sro.gym_service.entity.Trainee;
import dev.sro.gym_service.entity.Trainer;
import dev.sro.gym_service.entity.Training;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Long>, JpaSpecificationExecutor<Training> {
    boolean existsByTraineeAndTrainerAndTrainingDate(Trainee trainee, Trainer trainer, LocalDate trainingDate);

    Optional<Training> findByTraineeAndTrainerAndTrainingDate(Trainee trainee, Trainer trainer,
            LocalDate trainingDate);
}
