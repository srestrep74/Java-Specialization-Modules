package com.sro.SpringCoreTask1.service;


import java.util.List;

import com.sro.SpringCoreTask1.dto.TraineeTrainingFilterDTO;
import com.sro.SpringCoreTask1.dto.request.TrainingRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TrainingResponseDTO;
import com.sro.SpringCoreTask1.service.base.BaseService;

public interface TrainingService extends BaseService<TrainingRequestDTO, TrainingResponseDTO, Long>{
    List<TrainingResponseDTO> findTrainingsByTraineeWithFilters(TraineeTrainingFilterDTO filterDTO);
}
