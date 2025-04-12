package com.sro.SpringCoreTask1.service.impl;

import com.sro.SpringCoreTask1.dtos.v1.request.trainingType.TrainingTypeRequestDTO;
import com.sro.SpringCoreTask1.dtos.v1.response.trainingType.TrainingTypeResponse;
import com.sro.SpringCoreTask1.entity.TrainingType;
import com.sro.SpringCoreTask1.exception.DatabaseOperationException;
import com.sro.SpringCoreTask1.exception.ResourceNotFoundException;
import com.sro.SpringCoreTask1.exception.ResourceAlreadyExistsException;
import com.sro.SpringCoreTask1.mappers.trainingType.TrainingTypeCreateMapper;
import com.sro.SpringCoreTask1.mappers.trainingType.TrainingTypeResponseMapper;
import com.sro.SpringCoreTask1.repository.TrainingTypeRepository;
import com.sro.SpringCoreTask1.service.TrainingTypeService;

import jakarta.validation.ConstraintViolationException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrainingTypeServiceImpl implements TrainingTypeService {

    private final TrainingTypeRepository trainingTypeRepository;

    private final TrainingTypeCreateMapper trainingTypeCreateMapper;
    private final TrainingTypeResponseMapper trainingTypeResponseMapper;
    
    public TrainingTypeServiceImpl(TrainingTypeRepository trainingTypeRepository, TrainingTypeCreateMapper trainingTypeCreateMapper, TrainingTypeResponseMapper trainingTypeResponseMapper) {
        this.trainingTypeRepository = trainingTypeRepository;
        this.trainingTypeCreateMapper = trainingTypeCreateMapper;
        this.trainingTypeResponseMapper = trainingTypeResponseMapper;
    }

    @Override
    @Transactional
    public TrainingTypeResponse save(TrainingTypeRequestDTO trainingTypeRequestDTO) {
        if (trainingTypeRequestDTO == null) {
            throw new IllegalArgumentException("TrainingTypeRequestDTO cannot be null");
        }

        try {
            TrainingType trainingType = trainingTypeCreateMapper.toEntity(trainingTypeRequestDTO);
            TrainingType savedTrainingType = trainingTypeRepository.save(trainingType);
            return trainingTypeResponseMapper.mapToResponse(savedTrainingType);
        } catch (ConstraintViolationException e) {
            throw new ResourceAlreadyExistsException("Training Type with name " + trainingTypeRequestDTO.trainingTypeName() + " already exists");
        } catch (Exception e) {
            throw new DatabaseOperationException("Error saving Training Type", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public TrainingTypeResponse findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Training Type id cannot be null");
        }

        try {
            return trainingTypeRepository.findById(id)
                    .map(trainingTypeResponseMapper::mapToResponse)
                    .orElseThrow(() -> new ResourceNotFoundException("Training Type not found with id: " + id));
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding Training Type by id", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingTypeResponse> findAll() {
        try {
            return trainingTypeRepository.findAll().stream()
                    .map(trainingTypeResponseMapper::mapToResponse)
                    .toList();
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding all Training Types", e);
        }
    }

    @Override
    @Transactional
    public TrainingTypeResponse update(TrainingTypeRequestDTO trainingTypeRequestDTO) {
        if (trainingTypeRequestDTO == null) {
            throw new IllegalArgumentException("TrainingTypeRequestDTO cannot be null");
        }

        try {
            TrainingType trainingType = trainingTypeCreateMapper.toEntity(trainingTypeRequestDTO);
            return trainingTypeRepository.update(trainingType)
                    .map(trainingTypeResponseMapper::mapToResponse)
                    .orElseThrow(() -> new ResourceNotFoundException("Training Type not found with id: " + trainingType.getId()));
        } catch (Exception e) {
            throw new DatabaseOperationException("Error updating Training Type", e);
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Training Type id cannot be null");
        }

        try {
            if (!trainingTypeRepository.deleteById(id)) {
                throw new ResourceNotFoundException("Training Type not found with id: " + id);
            }
        } catch (Exception e) {
            throw new DatabaseOperationException("Error deleting Training Type by id", e);
        }
    }
}