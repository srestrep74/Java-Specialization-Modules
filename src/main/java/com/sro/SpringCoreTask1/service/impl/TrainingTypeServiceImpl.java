package com.sro.SpringCoreTask1.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sro.SpringCoreTask1.dto.request.TrainingTypeRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TrainingTypeResponseDTO;
import com.sro.SpringCoreTask1.entity.TrainingType;
import com.sro.SpringCoreTask1.mappers.TrainingTypeMapper;
import com.sro.SpringCoreTask1.repository.TrainingTypeRepository;
import com.sro.SpringCoreTask1.service.TrainingTypeService;

@Service
public class TrainingTypeServiceImpl implements TrainingTypeService{
    
    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainingTypeMapper trainingTypeMapper;

    public TrainingTypeServiceImpl(TrainingTypeRepository trainingTypeRepository, TrainingTypeMapper trainingTypeMapper) {
        this.trainingTypeRepository = trainingTypeRepository;
        this.trainingTypeMapper = trainingTypeMapper;
    }

    @Override
    @Transactional
    public TrainingTypeResponseDTO save(TrainingTypeRequestDTO trainingTypeRequestDTO) {
        TrainingType trainingType = this.trainingTypeMapper.toEntity(trainingTypeRequestDTO);
        TrainingType savedTrainingType = this.trainingTypeRepository.save(trainingType);
        return this.trainingTypeMapper.toDTO(savedTrainingType);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TrainingTypeResponseDTO> findById(Long id) {
        return this.trainingTypeRepository.findById(id).map(this.trainingTypeMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingTypeResponseDTO> findAll() {
        return this.trainingTypeRepository.findAll().stream().map(this.trainingTypeMapper::toDTO).toList();
    }

    @Override
    @Transactional
    public TrainingTypeResponseDTO update(TrainingTypeRequestDTO trainingTypeRequestDTO) {
        TrainingType trainingType = this.trainingTypeMapper.toEntity(trainingTypeRequestDTO);
        TrainingType updatedTrainingType = this.trainingTypeRepository.save(trainingType);
        return this.trainingTypeMapper.toDTO(updatedTrainingType);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        this.trainingTypeRepository.deleteById(id);
    }
}
