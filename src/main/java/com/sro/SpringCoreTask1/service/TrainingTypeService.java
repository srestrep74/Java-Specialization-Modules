package com.sro.SpringCoreTask1.service;

import com.sro.SpringCoreTask1.dto.request.TrainingTypeRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TrainingTypeResponseDTO;
import com.sro.SpringCoreTask1.dtos.v1.response.trainingType.TrainingTypeResponse;

import java.util.List;

public interface TrainingTypeService {
    TrainingTypeResponseDTO save(TrainingTypeRequestDTO trainingTypeRequestDTO);
    TrainingTypeResponseDTO findById(Long id);
    List<TrainingTypeResponse> findAll();
    void deleteById(Long id);
    TrainingTypeResponseDTO update(TrainingTypeRequestDTO trainingTypeRequestDTO);
}
