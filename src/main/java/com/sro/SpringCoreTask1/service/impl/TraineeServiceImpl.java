package com.sro.SpringCoreTask1.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.sro.SpringCoreTask1.dto.request.TraineeRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TraineeResponseDTO;
import com.sro.SpringCoreTask1.entity.Trainee;
import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.mappers.TraineeMapper;
import com.sro.SpringCoreTask1.repository.TraineeRepository;
import com.sro.SpringCoreTask1.repository.TrainerRepository;
import com.sro.SpringCoreTask1.service.TraineeService;

@Service
public class TraineeServiceImpl implements TraineeService{
    
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TraineeMapper traineeMapper;

    public TraineeServiceImpl(TraineeRepository traineeRepository, TrainerRepository trainerRepository, TraineeMapper traineeMapper) {
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
        this.traineeMapper = traineeMapper;
    }

    @Override
    public TraineeResponseDTO save(TraineeRequestDTO traineeRequestDTO) {
        Trainee trainee = this.traineeMapper.toEntity(traineeRequestDTO);
        Trainee savedTrainee = this.traineeRepository.save(trainee);
        return this.traineeMapper.toDTO(savedTrainee);
    }

    @Override
    public Optional<TraineeResponseDTO> findById(Long id) {
        return this.traineeRepository.findById(id).map(this.traineeMapper::toDTO);
    }

    @Override
    public List<TraineeResponseDTO> findAll() {
        return this.traineeRepository.findAll().stream().map(this.traineeMapper::toDTO).toList();
    }

    @Override
    public TraineeResponseDTO update(TraineeRequestDTO traineeRequestDTO) {
        Trainee trainee = this.traineeMapper.toEntity(traineeRequestDTO);
        Trainee updatedTrainee = this.traineeRepository.update(trainee);
        return this.traineeMapper.toDTO(updatedTrainee);
    }

    @Override
    public void deleteById(Long id) {
        this.traineeRepository.deleteById(id);
    }

    @Override
    public Optional<TraineeResponseDTO> findByUsername(String username) {
        return this.traineeRepository.findByUsername(username).map(this.traineeMapper::toDTO);
    }

    @Override
    public void deleteByUsername(String username) {
        this.traineeRepository.deleteByUsername(username);
    }

    @Override
    public void addTrainerToTrainee(Long traineeId, Long trainerId){
        Trainee trainee = this.traineeRepository.findById(traineeId).orElseThrow(
            () -> new IllegalArgumentException("Trainee not found")
        );

        Trainer trainer = this.trainerRepository.findById(trainerId).orElseThrow(
            () -> new IllegalArgumentException("Trainer not found")
        );

        if(!trainer.getTrainees().contains(trainee)){
            trainee.getTrainers().add(trainer);
            trainer.getTrainees().add(trainee);

            this.traineeRepository.save(trainee);
            this.trainerRepository.save(trainer);
        }
    }

    @Override
    public void removeTrainerFromTrainee(Long traineeId, Long trainerId){
        Trainee trainee = this.traineeRepository.findById(traineeId).orElseThrow(
            () -> new IllegalArgumentException("Trainee not found")
        );

        Trainer trainer = this.trainerRepository.findById(trainerId).orElseThrow(
            () -> new IllegalArgumentException("Trainer not found")
        );

        if(trainer.getTrainees().contains(trainee)){
            trainer.getTrainees().remove(trainee);
            trainee.getTrainers().remove(trainer);

            this.traineeRepository.save(trainee);
            this.trainerRepository.save(trainer);
        }
    }
}