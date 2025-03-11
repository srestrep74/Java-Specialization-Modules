package com.sro.SpringCoreTask1.repository;


import java.util.Optional;

import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.repository.base.BaseRepository;

public interface TrainerRepository extends BaseRepository<Trainer, Long> {
    Optional<Trainer> findByUsername(String username);
}
