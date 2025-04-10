package com.sro.SpringCoreTask1.service;

import com.sro.SpringCoreTask1.dtos.v1.request.trainingType.TrainingTypeRequestDTO;
import com.sro.SpringCoreTask1.dtos.v1.response.trainingType.TrainingTypeResponse;

import java.util.List;

public interface TrainingTypeService {
    TrainingTypeResponse save(TrainingTypeRequestDTO trainingTypeRequestDTO);
    TrainingTypeResponse findById(Long id);
    List<TrainingTypeResponse> findAll();
    void deleteById(Long id);
    TrainingTypeResponse update(TrainingTypeRequestDTO trainingTypeRequestDTO);
}
