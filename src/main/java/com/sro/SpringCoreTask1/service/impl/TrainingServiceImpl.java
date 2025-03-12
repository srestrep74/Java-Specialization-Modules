package com.sro.SpringCoreTask1.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sro.SpringCoreTask1.dto.TrainingFilterDTO;
import com.sro.SpringCoreTask1.dto.request.TrainingRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TrainingResponseDTO;
import com.sro.SpringCoreTask1.entity.Trainee;
import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.entity.Training;
import com.sro.SpringCoreTask1.entity.TrainingType;
import com.sro.SpringCoreTask1.mappers.TrainingMapper;
import com.sro.SpringCoreTask1.repository.TraineeRepository;
import com.sro.SpringCoreTask1.repository.TrainerRepository;
import com.sro.SpringCoreTask1.repository.TrainingRepository;
import com.sro.SpringCoreTask1.repository.TrainingTypeRepository;
import com.sro.SpringCoreTask1.service.TrainingService;

@Service
public class TrainingServiceImpl implements TrainingService{
    
    private final TrainingRepository trainingRepository;
    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainingMapper trainingMapper;

    public TrainingServiceImpl(TrainingRepository trainingRepository, TrainerRepository trainerRepository, TraineeRepository traineeRepository, TrainingTypeRepository trainingTypeRepository, TrainingMapper trainingMapper) {
        this.trainingRepository = trainingRepository;
        this.trainerRepository = trainerRepository;
        this.traineeRepository = traineeRepository;
        this.trainingTypeRepository = trainingTypeRepository;
        this.trainingMapper = trainingMapper;
    }

    @Override
    @Transactional
    public TrainingResponseDTO save(TrainingRequestDTO trainingRequestDTO) {
        Trainee trainee = this.traineeRepository.findById(trainingRequestDTO.traineeId()).get();
        Trainer trainer = this.trainerRepository.findById(trainingRequestDTO.trainerId()).get();
        TrainingType trainingType = this.trainingTypeRepository.findById(trainingRequestDTO.trainingTypeId()).get();
        Training training = this.trainingMapper.toEntity(trainingRequestDTO, trainee, trainer, trainingType);
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
        Trainee trainee = this.traineeRepository.findById(trainingRequestDTO.traineeId()).get();
        Trainer trainer = this.trainerRepository.findById(trainingRequestDTO.trainerId()).get();
        TrainingType trainingType = this.trainingTypeRepository.findById(trainingRequestDTO.trainingTypeId()).get();
        Training training = this.trainingMapper.toEntity(trainingRequestDTO, trainee, trainer, trainingType);
        Training updatedTraining = this.trainingRepository.update(training);
        return this.trainingMapper.toDTO(updatedTraining);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        this.trainingRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingResponseDTO> findTrainingsByFilters(TrainingFilterDTO filterDTO){
        if(filterDTO == null){
            throw new IllegalArgumentException("The filter can't be null");
        }

        return this.trainingRepository.findTrainingsByFilters(filterDTO).stream().map(this.trainingMapper::toDTO).toList();
    }
}
