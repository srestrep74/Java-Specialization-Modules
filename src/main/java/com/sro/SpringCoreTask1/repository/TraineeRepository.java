package com.sro.SpringCoreTask1.repository;

import java.util.Optional;
import java.util.Set;

import com.sro.SpringCoreTask1.entity.Trainee;
import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.repository.base.BaseRepository;

public interface TraineeRepository extends BaseRepository<Trainee, Long> {
    Optional<Trainee> findByUsername(String username);
    boolean deleteByUsername(String username);
    boolean updatePassword(Long traineeId, String newPassword);
    Set<Trainer> findTrainersByTraineeId(Long traineeId);
}