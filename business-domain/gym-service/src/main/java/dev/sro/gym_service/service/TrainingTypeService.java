package dev.sro.gym_service.service;

import dev.sro.gym_service.dtos.v1.request.trainingType.TrainingTypeRequestDTO;
import dev.sro.gym_service.dtos.v1.response.trainingType.TrainingTypeResponse;

import java.util.List;

public interface TrainingTypeService {
    TrainingTypeResponse save(TrainingTypeRequestDTO trainingTypeRequestDTO);

    TrainingTypeResponse findById(Long id);

    List<TrainingTypeResponse> findAll();

    void deleteById(Long id);

    TrainingTypeResponse update(TrainingTypeRequestDTO trainingTypeRequestDTO);
}
