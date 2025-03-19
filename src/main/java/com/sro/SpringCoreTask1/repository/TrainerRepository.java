package com.sro.SpringCoreTask1.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.repository.base.BaseRepository;

public interface TrainerRepository extends BaseRepository<Trainer, Long> {
    Optional<Trainer> findByUsername(String username);
    List<Trainer> findUnassignedTrainersByTraineeUsername(String traineeUsername);
    boolean changeTrainerPassword(Long trainerId, String newPassword);
    Set<Trainer> findTrainersByTraineeId(Long traineeId);
}