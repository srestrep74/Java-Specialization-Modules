package com.sro.SpringCoreTask1.service.impl;

import com.sro.SpringCoreTask1.dto.request.TrainingTypeRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TrainingTypeResponseDTO;
import com.sro.SpringCoreTask1.entity.TrainingType;
import com.sro.SpringCoreTask1.exception.DatabaseOperationException;
import com.sro.SpringCoreTask1.exception.ResourceNotFoundException;
import com.sro.SpringCoreTask1.exception.ResourceAlreadyExistsException;
import com.sro.SpringCoreTask1.mappers.TrainingTypeMapper;
import com.sro.SpringCoreTask1.repository.TrainingTypeRepository;
import com.sro.SpringCoreTask1.service.TrainingTypeService;

import jakarta.validation.ConstraintViolationException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrainingTypeServiceImpl implements TrainingTypeService {

    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainingTypeMapper trainingTypeMapper;

    public TrainingTypeServiceImpl(TrainingTypeRepository trainingTypeRepository, TrainingTypeMapper trainingTypeMapper) {
        this.trainingTypeRepository = trainingTypeRepository;
        this.trainingTypeMapper = trainingTypeMapper;
    }

    @Override
    @Transactional
    public TrainingTypeResponseDTO save(TrainingTypeRequestDTO trainingTypeRequestDTO) {
        if (trainingTypeRequestDTO == null) {
            throw new IllegalArgumentException("TrainingTypeRequestDTO cannot be null");
        }

        try {
            TrainingType trainingType = trainingTypeMapper.toEntity(trainingTypeRequestDTO);
            TrainingType savedTrainingType = trainingTypeRepository.save(trainingType);
            return trainingTypeMapper.toDTO(savedTrainingType);
        } catch (ConstraintViolationException e) {
            throw new ResourceAlreadyExistsException("Training Type with name " + trainingTypeRequestDTO.trainingTypeName() + " already exists");
        } catch (Exception e) {
            throw new DatabaseOperationException("Error saving Training Type", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public TrainingTypeResponseDTO findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Training Type id cannot be null");
        }

        try {
            return trainingTypeRepository.findById(id)
                    .map(trainingTypeMapper::toDTO)
                    .orElseThrow(() -> new ResourceNotFoundException("Training Type not found with id: " + id));
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding Training Type by id", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingTypeResponseDTO> findAll() {
        try {
            return trainingTypeRepository.findAll().stream()
                    .map(trainingTypeMapper::toDTO)
                    .toList();
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding all Training Types", e);
        }
    }

    @Override
    @Transactional
    public TrainingTypeResponseDTO update(TrainingTypeRequestDTO trainingTypeRequestDTO) {
        if (trainingTypeRequestDTO == null) {
            throw new IllegalArgumentException("TrainingTypeRequestDTO cannot be null");
        }

        try {
            TrainingType trainingType = trainingTypeMapper.toEntity(trainingTypeRequestDTO);
            return trainingTypeRepository.update(trainingType)
                    .map(trainingTypeMapper::toDTO)
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