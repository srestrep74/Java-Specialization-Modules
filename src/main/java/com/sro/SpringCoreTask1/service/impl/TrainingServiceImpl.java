package com.sro.SpringCoreTask1.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sro.SpringCoreTask1.dto.request.TrainingRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TrainingResponseDTO;
import com.sro.SpringCoreTask1.entity.Training;
import com.sro.SpringCoreTask1.mappers.TrainingMapper;
import com.sro.SpringCoreTask1.repository.TrainingRepository;
import com.sro.SpringCoreTask1.service.TrainingService;

@Service
public class TrainingServiceImpl implements TrainingService{
    
    private final TrainingRepository trainingRepository;
    private final TrainingMapper trainingMapper;

    public TrainingServiceImpl(TrainingRepository trainingRepository, TrainingMapper trainingMapper) {
        this.trainingRepository = trainingRepository;
        this.trainingMapper = trainingMapper;
    }

    @Override
    @Transactional
    public TrainingResponseDTO save(TrainingRequestDTO trainingRequestDTO) {
        Training training = this.trainingMapper.toEntity(trainingRequestDTO);
        Training savedTraining = this.trainingRepository.save(training);
        return this.trainingMapper.toDTO(savedTraining);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TrainingResponseDTO> findById(Long id) { 
        return this.trainingRepository.findById(id).map(this.trainingMapper::toDTO); 
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingResponseDTO> findAll() {
        return this.trainingRepository.findAll().stream().map(this.trainingMapper::toDTO).toList();
    }

    @Override
    @Transactional
    public TrainingResponseDTO update(TrainingRequestDTO trainingRequestDTO) {
        Training training = this.trainingMapper.toEntity(trainingRequestDTO);
        Training updatedTraining = this.trainingRepository.update(training);
        return this.trainingMapper.toDTO(updatedTraining);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        this.trainingRepository.deleteById(id);
    }
}
