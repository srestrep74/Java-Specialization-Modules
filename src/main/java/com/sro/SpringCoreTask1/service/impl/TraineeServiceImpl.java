package com.sro.SpringCoreTask1.service.impl;

import com.sro.SpringCoreTask1.dtos.v1.request.trainee.RegisterTraineeRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.trainee.UpdateTraineeProfileRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.trainee.UpdateTraineeTrainerListRequest;
import com.sro.SpringCoreTask1.dtos.v1.response.trainee.RegisterTraineeResponse;
import com.sro.SpringCoreTask1.dtos.v1.response.trainee.TraineeProfileResponse;
import com.sro.SpringCoreTask1.dtos.v1.response.trainee.TrainerSummaryResponse;
import com.sro.SpringCoreTask1.entity.Trainee;
import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.exception.DatabaseOperationException;
import com.sro.SpringCoreTask1.exception.ResourceNotFoundException;
import com.sro.SpringCoreTask1.exception.ResourceAlreadyExistsException;
import com.sro.SpringCoreTask1.mappers.trainee.TraineeCreateMapper;
import com.sro.SpringCoreTask1.mappers.trainee.TraineeResponseMapper;
import com.sro.SpringCoreTask1.mappers.trainee.TraineeUpdateMapper;
import com.sro.SpringCoreTask1.mappers.trainer.TrainerResponseMapper;
import com.sro.SpringCoreTask1.repository.TraineeRepository;
import com.sro.SpringCoreTask1.repository.TrainerRepository;
import com.sro.SpringCoreTask1.service.TraineeService;
import com.sro.SpringCoreTask1.util.ProfileUtil;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TraineeServiceImpl implements TraineeService {

    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TraineeCreateMapper traineeCreateMapper;
    private final TraineeUpdateMapper traineeUpdateMapper;
    private final TraineeResponseMapper traineeResponseMapper;
    private final TrainerResponseMapper trainerResponseMapper;

    public TraineeServiceImpl(
            TraineeRepository traineeRepository,
            TrainerRepository trainerRepository,
            TraineeCreateMapper traineeCreateMapper,
            TraineeUpdateMapper traineeUpdateMapper,
            TraineeResponseMapper traineeResponseMapper,
            TrainerResponseMapper trainerResponseMapper) {
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
        this.traineeCreateMapper = traineeCreateMapper;
        this.traineeUpdateMapper = traineeUpdateMapper;
        this.traineeResponseMapper = traineeResponseMapper;
        this.trainerResponseMapper = trainerResponseMapper;
    }

    @Override
    @Transactional
    public RegisterTraineeResponse save(RegisterTraineeRequest traineeRequestDTO) {
        if (traineeRequestDTO == null) {
            throw new IllegalArgumentException("Trainee cannot be null");
        }

        if (traineeRequestDTO.firstName() == null || traineeRequestDTO.firstName().isEmpty() ||
            traineeRequestDTO.lastName() == null || traineeRequestDTO.lastName().isEmpty() ||
            traineeRequestDTO.address() == null || traineeRequestDTO.address().isEmpty()) {
            throw new IllegalArgumentException("Trainee first name, last name, and address cannot be null or empty");
        }
        
        String generatedUsername = ProfileUtil.generateUsername(traineeRequestDTO.firstName(), traineeRequestDTO.lastName());
        String generatedPassword = ProfileUtil.generatePassword();

        try {
            Trainee trainee = traineeCreateMapper.toEntity(traineeRequestDTO);
            trainee.setUsername(generatedUsername);
            trainee.setPassword(generatedPassword);
            Trainee savedTrainee = traineeRepository.save(trainee);

            return traineeCreateMapper.toRegisterResponse(savedTrainee);
        } catch (ConstraintViolationException e) {
            throw new ResourceAlreadyExistsException("Trainee with username " + generatedUsername + " already exists");
        } catch (Exception e) {
            throw new DatabaseOperationException("Error saving Trainee", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public TraineeProfileResponse findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Trainee id cannot be null");
        }

        try {
            return traineeRepository.findById(id)
                    .map(traineeResponseMapper::toProfileResponse)
                    .orElseThrow(() -> new ResourceNotFoundException("Trainee not found with id: " + id));
        } catch(ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding Trainee by id", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<TraineeProfileResponse> findAll() {
        try {
            return traineeRepository.findAll().stream()
                    .map(traineeResponseMapper::toProfileResponse)
                    .toList();
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding all Trainees", e);
        }
    }

    @Override
    @Transactional
    public TraineeProfileResponse update(String username, UpdateTraineeProfileRequest traineeRequestDTO) {
        if (traineeRequestDTO == null) {
            throw new IllegalArgumentException("Trainee cannot be null");
        }

        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Trainee username cannot be null or empty");
        }

        try {
            Trainee existingTrainee = traineeRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Trainee not found with username: " + username));
            Trainee trainee = traineeUpdateMapper.toEntity(traineeRequestDTO, existingTrainee);
            trainee.setId(existingTrainee.getId());
            trainee.setPassword(existingTrainee.getPassword());
            trainee.setTrainers(existingTrainee.getTrainers());
            Trainee updatedTrainee = traineeRepository.update(trainee)
                    .orElseThrow(() -> new ResourceNotFoundException("Trainee not found with id: " + trainee.getId()));
            return traineeResponseMapper.toProfileResponse(updatedTrainee);
        } catch(ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error updating Trainee", e);
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Trainee id cannot be null");
        }

        try {
            if (!traineeRepository.deleteById(id)) {
                throw new ResourceNotFoundException("Trainee not found with id: " + id);
            }
        } catch(ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error deleting Trainee by id", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public TraineeProfileResponse findByUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Trainee username cannot be null or empty");
        }

        try {
            Trainee trainee = traineeRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Trainee not found with username: " + username));
            return traineeResponseMapper.toProfileResponse(trainee);
        } catch(ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding Trainee by username", e);
        }
    }

    @Override
    @Transactional
    public void deleteByUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Trainee username cannot be null or empty");
        }

        try {
            if (!traineeRepository.deleteByUsername(username)) {
                throw new ResourceNotFoundException("Trainee not found with username: " + username);
            }
        } catch(ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error deleting Trainee by username", e);
        }
    }

    @Override
    @Transactional
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
        } catch(ResourceNotFoundException e) {
            throw e;
        } catch(ResourceAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error adding Trainer to Trainee", e);
        }
    }

    @Override
    @Transactional
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
        } catch(ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error removing Trainer from Trainee", e);
        }
    }

    @Transactional
    public List<TrainerSummaryResponse> updateTraineeTrainers(String username, UpdateTraineeTrainerListRequest updateTrainersRequest) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Trainee username cannot be null or empty");
        }
        
        try {
            Trainee trainee = traineeRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Trainee not found with username: " + username));

            List<String> requestedUsernames = updateTrainersRequest.trainers();

            Map<String, Optional<Trainer>> trainerLookup = requestedUsernames.stream()
                .collect(Collectors.toMap(
                    trainerUsername -> trainerUsername,
                    trainerUsername -> trainerRepository.findByUsername(trainerUsername)
                ));

            List<String> notFound = trainerLookup.entrySet().stream()
                .filter(entry -> entry.getValue().isEmpty())
                .map(Map.Entry::getKey)
                .toList();

            if (!notFound.isEmpty()) {
                throw new ResourceNotFoundException("Trainers not found: " + String.join(", ", notFound));
            }
            
            Set<Trainer> currentTrainers = trainee.getTrainers();
            currentTrainers.stream()
                .filter(trainer -> !requestedUsernames.contains(trainer.getUsername()))
                .forEach(trainer -> removeTrainerFromTrainee(trainee.getId(), trainer.getId()));
            
            Set<Trainer> newTrainers = trainerLookup.values().stream()
                .map(Optional::get)
                .collect(Collectors.toSet());

            newTrainers.stream()
                .filter(trainer -> !currentTrainers.contains(trainer))
                .forEach(trainer -> addTrainerToTrainee(trainee.getId(), trainer.getId()));
            
            return trainee.getTrainers().stream()
                .map(trainerResponseMapper::toSummaryResponseDTO)
                .toList();
                
        } catch(ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error updating Trainee trainers", e);
        }
    }

    @Override
    @Transactional
    public void updateActivationStatus(String username, boolean active) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Trainee username cannot be null or empty");
        }

        try {
            Trainee trainee = traineeRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Trainee not found with username: " + username));

            trainee.setActive(active);
            traineeRepository.save(trainee);
        } catch(ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error updating Trainee activation status", e);
        }
    }

    @Override
    public boolean updateTraineePassword(Long traineeId, String newPassword) {
        try {
            return traineeRepository.updatePassword(traineeId, newPassword);
        } catch(ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error updating Trainee password", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Trainer> findTrainersByTraineeId(Long traineeId) {
        if (traineeId == null) {
            throw new IllegalArgumentException("Trainee id cannot be null");
        }

        try {
            return traineeRepository.findTrainersByTraineeId(traineeId);
        } catch(ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding Trainers by Trainee ID", e);
        }
    }
}