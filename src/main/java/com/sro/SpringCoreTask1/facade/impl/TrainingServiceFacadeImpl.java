package com.sro.SpringCoreTask1.facade.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.sro.SpringCoreTask1.dto.request.TrainingRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TrainingResponseDTO;
import com.sro.SpringCoreTask1.dto.response.TrainingTypeResponseDTO;
import com.sro.SpringCoreTask1.exception.DatabaseOperationException;
import com.sro.SpringCoreTask1.exception.ResourceAlreadyExistsException;
import com.sro.SpringCoreTask1.facade.TrainingServiceFacade;
import com.sro.SpringCoreTask1.service.TrainingService;
import com.sro.SpringCoreTask1.service.TrainingTypeService;

@Component
public class TrainingServiceFacadeImpl implements TrainingServiceFacade{
    
    private final TrainingService trainingService;
    private final TrainingTypeService trainingTypeService;
    
    public TrainingServiceFacadeImpl(
            TrainingService trainingService,
            TrainingTypeService trainingTypeService) {
        this.trainingService = trainingService;
        this.trainingTypeService = trainingTypeService;
    }
    
    @Override
    public TrainingResponseDTO createTraining(TrainingRequestDTO trainingRequestDTO) {
        if (trainingRequestDTO == null) {
            throw new IllegalArgumentException("TrainingRequestDTO cannot be null");
        }
        try {
            return trainingService.save(trainingRequestDTO);
        } catch (ResourceAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error adding Training", e);
        }
    }
    
    @Override
    public List<TrainingTypeResponseDTO> findAllTrainingTypes() {
        try {
            return trainingTypeService.findAll();
        } catch (Exception e) {
            throw new DatabaseOperationException("Error getting Training Types", e);
        }
    }
}