package com.sro.SpringCoreTask1.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sro.SpringCoreTask1.dto.request.TraineeRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TraineeResponseDTO;
import com.sro.SpringCoreTask1.entity.Trainee;
import com.sro.SpringCoreTask1.mappers.TraineeMapper;
import com.sro.SpringCoreTask1.repository.TraineeRepository;
import com.sro.SpringCoreTask1.service.TraineeService;

@Service
public class TraineeServiceImpl implements TraineeService{
    
    private final TraineeRepository traineeRepository;
    private final TraineeMapper traineeMapper;

    public TraineeServiceImpl(TraineeRepository traineeRepository, TraineeMapper traineeMapper) {
        this.traineeRepository = traineeRepository;
        this.traineeMapper = traineeMapper;
    }

    @Override
    @Transactional
    public TraineeResponseDTO save(TraineeRequestDTO traineeRequestDTO) {
        Trainee trainee = this.traineeMapper.toEntity(traineeRequestDTO);
        Trainee savedTrainee = this.traineeRepository.save(trainee);
        return this.traineeMapper.toDTO(savedTrainee);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TraineeResponseDTO> findById(Long id) {
        return this.traineeRepository.findById(id).map(this.traineeMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TraineeResponseDTO> findAll() {
        return this.traineeRepository.findAll().stream().map(this.traineeMapper::toDTO).toList();
    }

    @Override
    @Transactional
    public TraineeResponseDTO update(TraineeRequestDTO traineeRequestDTO) {
        Trainee trainee = this.traineeMapper.toEntity(traineeRequestDTO);
        Trainee updatedTrainee = this.traineeRepository.update(trainee);
        return this.traineeMapper.toDTO(updatedTrainee);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        this.traineeRepository.deleteById(id);
    }
}
