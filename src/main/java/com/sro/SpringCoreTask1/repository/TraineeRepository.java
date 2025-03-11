package com.sro.SpringCoreTask1.repository;

import java.util.Optional;

import com.sro.SpringCoreTask1.entity.Trainee;
import com.sro.SpringCoreTask1.repository.base.BaseRepository;

public interface TraineeRepository extends BaseRepository<Trainee, Long> {
    Optional<Trainee> findByUsername(String username);
}
