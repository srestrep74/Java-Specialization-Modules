package dev.sro.gym_service.service.impl;

import dev.sro.gym_service.dtos.v1.request.trainingType.TrainingTypeRequestDTO;
import dev.sro.gym_service.dtos.v1.response.trainingType.TrainingTypeResponse;
import dev.sro.gym_service.entity.TrainingType;
import dev.sro.gym_service.exception.DatabaseOperationException;
import dev.sro.gym_service.exception.ResourceNotFoundException;
import dev.sro.gym_service.exception.ResourceAlreadyExistsException;
import dev.sro.gym_service.mappers.trainingType.TrainingTypeCreateMapper;
import dev.sro.gym_service.mappers.trainingType.TrainingTypeResponseMapper;
import dev.sro.gym_service.repository.TrainingTypeRepository;
import dev.sro.gym_service.service.TrainingTypeService;

import jakarta.validation.ConstraintViolationException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrainingTypeServiceImpl implements TrainingTypeService {

    private final TrainingTypeRepository trainingTypeRepository;

    private final TrainingTypeCreateMapper trainingTypeCreateMapper;
    private final TrainingTypeResponseMapper trainingTypeResponseMapper;

    public TrainingTypeServiceImpl(TrainingTypeRepository trainingTypeRepository,
            TrainingTypeCreateMapper trainingTypeCreateMapper, TrainingTypeResponseMapper trainingTypeResponseMapper) {
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
            throw new ResourceAlreadyExistsException(
                    "Training Type with name " + trainingTypeRequestDTO.trainingTypeName() + " already exists");
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
            TrainingType savedTrainingType = trainingTypeRepository.save(trainingType);
            return trainingTypeResponseMapper.mapToResponse(savedTrainingType);
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException(
                    "Training Type not found with id: " + trainingTypeRequestDTO.trainingTypeName());
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
            trainingTypeRepository.deleteById(id);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error deleting Training Type by id", e);
        }
    }
}