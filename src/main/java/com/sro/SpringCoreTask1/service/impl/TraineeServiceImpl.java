package com.sro.SpringCoreTask1.service.impl;

import com.sro.SpringCoreTask1.dto.request.TraineeRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TraineeResponseDTO;
import com.sro.SpringCoreTask1.entity.Trainee;
import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.exception.DatabaseOperationException;
import com.sro.SpringCoreTask1.exception.ResourceNotFoundException;
import com.sro.SpringCoreTask1.exception.ResourceAlreadyExistsException;
import com.sro.SpringCoreTask1.mappers.TraineeMapper;
import com.sro.SpringCoreTask1.repository.TraineeRepository;
import com.sro.SpringCoreTask1.repository.TrainerRepository;
import com.sro.SpringCoreTask1.service.TraineeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TraineeServiceImpl implements TraineeService {

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
        try {
            Trainee trainee = this.traineeMapper.toEntity(traineeRequestDTO);
            Trainee savedTrainee = this.traineeRepository.save(trainee);
            return this.traineeMapper.toDTO(savedTrainee);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error saving Trainee", e);
        }
    }

    @Override
    public Optional<TraineeResponseDTO> findById(Long id) {
        try {
            return this.traineeRepository.findById(id).map(this.traineeMapper::toDTO);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding Trainee by id", e);
        }
    }

    @Override
    public List<TraineeResponseDTO> findAll() {
        try {
            return this.traineeRepository.findAll().stream().map(this.traineeMapper::toDTO).toList();
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding all Trainees", e);
        }
    }

    @Override
    public TraineeResponseDTO update(TraineeRequestDTO traineeRequestDTO) {
        try {
            Trainee trainee = this.traineeMapper.toEntity(traineeRequestDTO);
            Trainee updatedTrainee = this.traineeRepository.update(trainee);
            return this.traineeMapper.toDTO(updatedTrainee);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error updating Trainee", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        try {
            this.traineeRepository.deleteById(id);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error deleting Trainee by id", e);
        }
    }

    @Override
    public Optional<TraineeResponseDTO> findByUsername(String username) {
        try {
            return this.traineeRepository.findByUsername(username).map(this.traineeMapper::toDTO);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding Trainee by username", e);
        }
    }

    @Override
    public void deleteByUsername(String username) {
        try {
            this.traineeRepository.deleteByUsername(username);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error deleting Trainee by username", e);
        }
    }

    @Override
    public void addTrainerToTrainee(Long traineeId, Long trainerId) {
        try {
            Trainee trainee = this.traineeRepository.findById(traineeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Trainee not found with id: " + traineeId));

            Trainer trainer = this.trainerRepository.findById(trainerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with id: " + trainerId));

            if (trainer.getTrainees().contains(trainee)) {
                throw new ResourceAlreadyExistsException("Trainer already assigned to Trainee");
            }

            trainer.addTrainee(trainee);
            trainerRepository.save(trainer);
        } catch (ResourceNotFoundException | ResourceAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error adding Trainer to Trainee", e);
        }
    }

    @Override
    public void removeTrainerFromTrainee(Long traineeId, Long trainerId) {
        try {
            Trainee trainee = this.traineeRepository.findById(traineeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Trainee not found with id: " + traineeId));

            Trainer trainer = this.trainerRepository.findById(trainerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with id: " + trainerId));

            if (!trainer.getTrainees().contains(trainee)) {
                throw new ResourceNotFoundException("Trainer not assigned to Trainee");
            }
            
            trainer.removeTrainee(trainee);
            trainerRepository.save(trainer);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error removing Trainer from Trainee", e);
        }
    }
}