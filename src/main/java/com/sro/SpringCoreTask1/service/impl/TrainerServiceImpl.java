package com.sro.SpringCoreTask1.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.sro.SpringCoreTask1.dto.request.TrainerRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TrainerResponseDTO;
import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.entity.TrainingType;
import com.sro.SpringCoreTask1.mappers.TrainerMapper;
import com.sro.SpringCoreTask1.repository.TrainerRepository;
import com.sro.SpringCoreTask1.repository.TrainingTypeRepository;
import com.sro.SpringCoreTask1.service.TrainerService;

@Service
public class TrainerServiceImpl implements TrainerService {
    
    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainerMapper trainerMapper;

    public TrainerServiceImpl(TrainerRepository trainerRepository, TrainingTypeRepository trainingTypeRepository, TrainerMapper trainerMapper) {
        this.trainerRepository = trainerRepository;
        this.trainingTypeRepository = trainingTypeRepository;
        this.trainerMapper = trainerMapper;
    }

    @Override
    public TrainerResponseDTO save(TrainerRequestDTO trainerRequestDTO) {
        TrainingType trainingType = this.trainingTypeRepository.findById(trainerRequestDTO.trainingTypeId()).get();
        Trainer trainer = this.trainerMapper.toEntity(trainerRequestDTO, trainingType);
        Trainer savedTrainer = this.trainerRepository.save(trainer);
        return this.trainerMapper.toDTO(savedTrainer);
    }

    @Override
    public Optional<TrainerResponseDTO> findById(Long id) {
        return this.trainerRepository.findById(id).map(this.trainerMapper::toDTO);
    }

    @Override
    public List<TrainerResponseDTO> findAll() {
        return this.trainerRepository.findAll().stream().map(this.trainerMapper::toDTO).toList();
    }

    @Override
    public TrainerResponseDTO update(TrainerRequestDTO trainerRequestDTO) {
        TrainingType trainingType = this.trainingTypeRepository.findById(trainerRequestDTO.trainingTypeId()).get();
        Trainer trainer = this.trainerMapper.toEntity(trainerRequestDTO, trainingType);
        Trainer updatedTrainer = this.trainerRepository.save(trainer);
        return this.trainerMapper.toDTO(updatedTrainer);
    }

    @Override
    public void deleteById(Long id) {
        this.trainerRepository.deleteById(id);
    }

    @Override
    public Optional<TrainerResponseDTO> findByUsername(String username) { 
        return this.trainerRepository.findByUsername(username).map(this.trainerMapper::toDTO); 
    }

    @Override
    public List<TrainerResponseDTO> getTrainersNotAssignedToTrainee(String traineeUsername) {
        return this.trainerRepository.findTrainersNotAssignedToTrainee(traineeUsername)
                .stream()
                .map(this.trainerMapper::toDTO)
                .toList();
    }
}
