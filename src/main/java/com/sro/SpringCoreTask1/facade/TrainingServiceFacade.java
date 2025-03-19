package com.sro.SpringCoreTask1.facade;

import java.util.List;

import com.sro.SpringCoreTask1.dto.request.TrainingRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TrainingResponseDTO;
import com.sro.SpringCoreTask1.dto.response.TrainingTypeResponseDTO;

public interface TrainingServiceFacade {
    TrainingResponseDTO createTraining(TrainingRequestDTO trainingRequestDTO);
    List<TrainingTypeResponseDTO> findAllTrainingTypes();
}
