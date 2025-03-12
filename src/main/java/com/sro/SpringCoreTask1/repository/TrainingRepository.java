package com.sro.SpringCoreTask1.repository;


import java.util.List;

import com.sro.SpringCoreTask1.dto.TrainingFilterDTO;
import com.sro.SpringCoreTask1.entity.Training;
import com.sro.SpringCoreTask1.repository.base.BaseRepository;

public interface TrainingRepository extends BaseRepository<Training, Long> {
    List<Training> findTrainingsByFilters(TrainingFilterDTO filterDTO);
}
