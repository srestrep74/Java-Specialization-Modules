package com.sro.SpringCoreTask1.service.impl;

import com.sro.SpringCoreTask1.dto.request.TrainingTypeRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TrainingTypeResponseDTO;
import com.sro.SpringCoreTask1.entity.TrainingType;
import com.sro.SpringCoreTask1.mappers.TrainingTypeMapper;
import com.sro.SpringCoreTask1.repository.TrainingTypeRepository;
import com.sro.SpringCoreTask1.service.TrainingTypeService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TrainingTypeServiceImpl implements TrainingTypeService {
    
    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainingTypeMapper trainingTypeMapper;
    
    public TrainingTypeServiceImpl(TrainingTypeRepository trainingTypeRepository, TrainingTypeMapper trainingTypeMapper) {
        this.trainingTypeRepository = trainingTypeRepository;
        this.trainingTypeMapper = trainingTypeMapper;
    }
    
    @Override
    public TrainingTypeResponseDTO save(TrainingTypeRequestDTO dto) {
        TrainingType trainingType = trainingTypeMapper.toEntity(dto);
        TrainingType savedEntity = trainingTypeRepository.save(trainingType);
        return trainingTypeMapper.toDTO(savedEntity);
    }
    
    @Override
    public Optional<TrainingTypeResponseDTO> findById(Long id) {
        return trainingTypeRepository.findById(id)
                .map(trainingTypeMapper::toDTO);
    }
    
    @Override
    public List<TrainingTypeResponseDTO> findAll() {
        return trainingTypeRepository.findAll().stream()
                .map(trainingTypeMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public void deleteById(Long id) {
        trainingTypeRepository.deleteById(id);
    }
    
    @Override
    public TrainingTypeResponseDTO update(TrainingTypeRequestDTO dto) {
        TrainingType trainingType = trainingTypeMapper.toEntity(dto);
        TrainingType updatedEntity = trainingTypeRepository.update(trainingType);
        return trainingTypeMapper.toDTO(updatedEntity);
    }
}