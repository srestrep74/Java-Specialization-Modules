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
import com.sro.SpringCoreTask1.util.ProfileUtil;

import jakarta.validation.ConstraintViolationException;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

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
        if (traineeRequestDTO == null) {
            throw new IllegalArgumentException("Trainee cannot be null");
        }

        if (traineeRequestDTO.firstName() == null || traineeRequestDTO.firstName().isEmpty() ||
            traineeRequestDTO.lastName() == null || traineeRequestDTO.lastName().isEmpty() ||
            traineeRequestDTO.address() == null || traineeRequestDTO.address().isEmpty()) {
            throw new IllegalArgumentException("Trainee first name, last name, and address cannot be null or empty");
        }

        try {
            Trainee trainee = traineeMapper.toEntity(traineeRequestDTO);
            trainee.setUsername(ProfileUtil.generateUsername(traineeRequestDTO.firstName(), traineeRequestDTO.lastName()));
            trainee.setPassword(ProfileUtil.generatePassword());

            if (traineeRequestDTO.trainerIds() != null && !traineeRequestDTO.trainerIds().isEmpty()) {
                for (Long trainerId : traineeRequestDTO.trainerIds()) {
                    Trainer trainer = trainerRepository.findById(trainerId)
                            .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with id: " + trainerId));
                    trainer.addTrainee(trainee);
                }
            }

            Trainee savedTrainee = traineeRepository.save(trainee);
            return traineeMapper.toDTO(savedTrainee);
        } catch (ConstraintViolationException e) {
            throw new ResourceAlreadyExistsException("Trainee with username " + traineeRequestDTO.username() + " already exists");
        } catch (Exception e) {
            throw new DatabaseOperationException("Error saving Trainee", e);
        }
    }

    @Override
    public TraineeResponseDTO findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Trainee id cannot be null");
        }

        try {
            return traineeRepository.findById(id)
                    .map(traineeMapper::toDTO)
                    .orElseThrow(() -> new ResourceNotFoundException("Trainee not found with id: " + id));
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding Trainee by id", e);
        }
    }

    @Override
    public List<TraineeResponseDTO> findAll() {
        try {
            return traineeRepository.findAll().stream()
                    .map(traineeMapper::toDTO)
                    .toList();
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding all Trainees", e);
        }
    }

    @Override
    public TraineeResponseDTO update(TraineeRequestDTO traineeRequestDTO) {
        if (traineeRequestDTO == null) {
            throw new IllegalArgumentException("Trainee cannot be null");
        }

        try {
            Trainee existingTrainee = traineeRepository.findByUsername(traineeRequestDTO.username())
                    .orElseThrow(() -> new ResourceNotFoundException("Trainee not found with username: " + traineeRequestDTO.username()));
            Trainee trainee = traineeMapper.toEntity(traineeRequestDTO);
            trainee.setId(existingTrainee.getId());
            trainee.setPassword(existingTrainee.getPassword());
            return traineeRepository.update(trainee)
                    .map(traineeMapper::toDTO)
                    .orElseThrow(() -> new ResourceNotFoundException("Trainee not found with id: " + trainee.getId()));
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error updating Trainee", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Trainee id cannot be null");
        }

        try {
            if (!traineeRepository.deleteById(id)) {
                throw new ResourceNotFoundException("Trainee not found with id: " + id);
            }
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error deleting Trainee by id", e);
        }
    }

    @Override
    public TraineeResponseDTO findByUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Trainee username cannot be null or empty");
        }

        try {
            return traineeRepository.findByUsername(username)
                    .map(traineeMapper::toDTO)
                    .orElseThrow(() -> new ResourceNotFoundException("Trainee not found with username: " + username));
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding Trainee by username", e);
        }
    }

    @Override
    public void deleteByUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Trainee username cannot be null or empty");
        }

        try {
            if (!traineeRepository.deleteByUsername(username)) {
                throw new ResourceNotFoundException("Trainee not found with username: " + username);
            }
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error deleting Trainee by username", e);
        }
    }

    @Override
    public void addTrainerToTrainee(Long traineeId, Long trainerId) {
        if (traineeId == null || trainerId == null) {
            throw new IllegalArgumentException("Trainee id and Trainer id cannot be null");
        }

        try {
            Trainee trainee = traineeRepository.findById(traineeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Trainee not found with id: " + traineeId));

            Trainer trainer = trainerRepository.findById(trainerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with id: " + trainerId));

            if (!trainer.isActive()) {
                throw new ResourceNotFoundException("Trainer with id: " + trainerId + " is not active");
            }

            if (trainer.getTrainees().contains(trainee)) {
                throw new ResourceAlreadyExistsException("Trainer already assigned to Trainee");
            }

            trainee.addTrainer(trainer);
            traineeRepository.save(trainee);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (ResourceAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error adding Trainer to Trainee", e);
        }
    }

    @Override
    public void removeTrainerFromTrainee(Long traineeId, Long trainerId) {
        if (traineeId == null || trainerId == null) {
            throw new IllegalArgumentException("Trainee id and Trainer id cannot be null");
        }

        try {
            Trainee trainee = traineeRepository.findById(traineeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Trainee not found with id: " + traineeId));

            Trainer trainer = trainerRepository.findById(trainerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with id: " + trainerId));

            if (!trainer.isActive()) {
                throw new ResourceNotFoundException("Trainer with id: " + trainerId + " is not active");
            }

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

    @Override
    public void setTraineeStatus(Long traineeId) {
        if (traineeId == null) {
            throw new IllegalArgumentException("Trainee id cannot be null");
        }

        try {
            Trainee trainee = traineeRepository.findById(traineeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Trainee not found with id: " + traineeId));

            trainee.setActive(!trainee.isActive());
            traineeRepository.save(trainee);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error setting Trainee status", e);
        }
    }

    @Override
    public boolean updateTraineePassword(Long traineeId, String newPassword) {
        try {
            return traineeRepository.updatePassword(traineeId, newPassword);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error updating Trainee password", e);
        }
    }

    @Override
    public Set<Trainer> findTrainersByTraineeId(Long traineeId) {
        if (traineeId == null) {
            throw new IllegalArgumentException("Trainee id cannot be null");
        }

        try {
            return traineeRepository.findTrainersByTraineeId(traineeId);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding Trainers by Trainee ID", e);
        }
    }
}