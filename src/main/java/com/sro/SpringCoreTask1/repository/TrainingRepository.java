package com.sro.SpringCoreTask1.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.sro.SpringCoreTask1.entity.Trainee;
import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.entity.Training;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Long>, JpaSpecificationExecutor<Training> {
    boolean existsByTraineeAndTrainerAndTrainingDate(Trainee trainee, Trainer trainer, LocalDate trainingDate);
}
