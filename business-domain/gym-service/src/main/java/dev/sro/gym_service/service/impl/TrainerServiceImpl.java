package dev.sro.gym_service.service.impl;

import dev.sro.gym_service.dtos.v1.request.trainer.RegisterTrainerRequest;
import dev.sro.gym_service.dtos.v1.request.trainer.UpdateTrainerProfileRequest;
import dev.sro.gym_service.dtos.v1.response.trainer.RegisterTrainerResponse;
import dev.sro.gym_service.dtos.v1.response.trainer.TrainerProfileResponse;
import dev.sro.gym_service.dtos.v1.response.trainer.UnassignedTrainerResponse;
import dev.sro.gym_service.entity.Trainer;
import dev.sro.gym_service.entity.TrainingType;
import dev.sro.gym_service.exception.DatabaseOperationException;
import dev.sro.gym_service.exception.ResourceAlreadyExistsException;
import dev.sro.gym_service.exception.ResourceNotFoundException;
import dev.sro.gym_service.mappers.trainer.TrainerCreateMapper;
import dev.sro.gym_service.mappers.trainer.TrainerResponseMapper;
import dev.sro.gym_service.mappers.trainer.TrainerUpdateMapper;
import dev.sro.gym_service.repository.TrainerRepository;
import dev.sro.gym_service.repository.TrainingTypeRepository;
import dev.sro.gym_service.repository.specification.TrainerSpecifications;
import dev.sro.gym_service.service.AuthService;
import dev.sro.gym_service.service.TrainerService;
import dev.sro.gym_service.util.ProfileUtil;

import jakarta.validation.ConstraintViolationException;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TrainerServiceImpl implements TrainerService {

    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final AuthService authService;

    private final TrainerCreateMapper trainerCreateMapper;
    private final TrainerUpdateMapper trainerUpdateMapper;
    private final TrainerResponseMapper trainerResponseMapper;
    private final PasswordEncoder passwordEncoder;

    public TrainerServiceImpl(TrainerRepository trainerRepository, TrainingTypeRepository trainingTypeRepository,
            AuthService authService, TrainerCreateMapper trainerCreateMapper, TrainerUpdateMapper trainerUpdateMapper,
            TrainerResponseMapper trainerResponseMapper, PasswordEncoder passwordEncoder) {
        this.trainerRepository = trainerRepository;
        this.trainingTypeRepository = trainingTypeRepository;
        this.authService = authService;
        this.trainerCreateMapper = trainerCreateMapper;
        this.trainerUpdateMapper = trainerUpdateMapper;
        this.trainerResponseMapper = trainerResponseMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public RegisterTrainerResponse save(RegisterTrainerRequest trainerRequestDTO) {
        if (trainerRequestDTO == null) {
            throw new IllegalArgumentException("Trainer cannot be null");
        }

        if (trainerRequestDTO.firstName() == null || trainerRequestDTO.firstName().isEmpty() ||
                trainerRequestDTO.lastName() == null || trainerRequestDTO.lastName().isEmpty() ||
                trainerRequestDTO.specialization() == null) {
            throw new IllegalArgumentException(
                    "Trainer first name, last name, and specialization cannot be null or empty");
        }

        String generatedUsername = ProfileUtil.generateUsername(trainerRequestDTO.firstName(),
                trainerRequestDTO.lastName(), username -> trainerRepository.existsByUsername(username));
        String generatedPassword = ProfileUtil.generatePassword();

        try {
            TrainingType trainingType = trainingTypeRepository.findById(trainerRequestDTO.specialization())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "TrainingType not found with id: " + trainerRequestDTO.specialization()));
            Trainer trainer = trainerCreateMapper.toEntity(trainerRequestDTO, trainingType);
            trainer.setUsername(generatedUsername);
            trainer.setPassword(passwordEncoder.encode(generatedPassword));
            Trainer savedTrainer = trainerRepository.save(trainer);

            return trainerCreateMapper.toRegisterResponse(savedTrainer, generatedPassword);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (ConstraintViolationException e) {
            throw new ResourceAlreadyExistsException("Trainer with username " + generatedUsername + " already exists");
        } catch (Exception e) {
            throw new DatabaseOperationException("Error saving Trainer", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public TrainerProfileResponse findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Trainer id cannot be null");
        }

        try {
            return trainerRepository.findById(id)
                    .map(trainerResponseMapper::toTrainerProfileResponse)
                    .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with id: " + id));
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding Trainer by id", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainerProfileResponse> findAll() {
        try {
            return trainerRepository.findAll().stream()
                    .map(trainerResponseMapper::toTrainerProfileResponse)
                    .toList();
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding all Trainers", e);
        }
    }

    @Override
    @Transactional
    public TrainerProfileResponse update(String username, UpdateTrainerProfileRequest trainerRequestDTO) {
        if (trainerRequestDTO == null) {
            throw new IllegalArgumentException("Trainer cannot be null");
        }

        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Trainer username cannot be null or empty");
        }

        try {
            TrainingType trainingType = trainingTypeRepository.findById(trainerRequestDTO.specialization())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "TrainingType not found with id: " + trainerRequestDTO.specialization()));
            Trainer existingTrainer = trainerRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with username: " + username));
            Trainer trainer = trainerUpdateMapper.toEntity(trainerRequestDTO, existingTrainer, trainingType);

            trainer.setPassword(existingTrainer.getPassword());
            return trainerResponseMapper.toTrainerProfileResponse(trainerRepository.save(trainer));
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error updating Trainer", e);
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Trainer id cannot be null");
        }

        try {
            if (!trainerRepository.existsById(id)) {
                throw new ResourceNotFoundException("Trainer not found with id: " + id);
            }

            trainerRepository.deleteById(id);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error deleting Trainer by id", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public TrainerProfileResponse findByUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Trainer username cannot be null or empty");
        }

        try {
            return trainerResponseMapper.toTrainerProfileResponse(trainerRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with username: " + username)));
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding Trainer by username", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UnassignedTrainerResponse> findUnassignedTrainersByTraineeUsername(String traineeUsername) {
        if (traineeUsername == null || traineeUsername.isEmpty()) {
            throw new IllegalArgumentException("Trainee username cannot be null or empty");
        }

        try {
            Specification<Trainer> spec = Specification
                    .where(TrainerSpecifications.isActive())
                    .and(TrainerSpecifications.notAssignedToTrainee(traineeUsername))
                    .and(TrainerSpecifications.withTrainingTypeFetched());

            return trainerRepository.findAll(spec).stream()
                    .map(trainerResponseMapper::toUnassignedTrainerResponse)
                    .toList();
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding Trainers not assigned to Trainee", e);
        }
    }

    @Override
    @Transactional
    public void updateActivationStatus(String username, boolean active) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Trainer username cannot be null or empty");
        }

        try {
            Trainer trainer = trainerRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with username: " + username));

            trainer.setActive(active);
            trainerRepository.save(trainer);

            authService.setCurrentUser(trainer);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error updating Trainer activation status", e);
        }
    }

    @Override
    @Transactional
    public boolean updateTrainerPassword(Long trainerId, String newPassword) {
        if (trainerId == null || newPassword == null || newPassword.isEmpty()) {
            throw new IllegalArgumentException("Trainer id and new password cannot be null or empty");
        }

        try {
            trainerRepository.updatePassword(trainerId, newPassword);
            return true;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error updating Trainer password", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Set<TrainerProfileResponse> findTrainersByTraineeId(Long traineeId) {
        if (traineeId == null) {
            throw new IllegalArgumentException("Trainee id cannot be null");
        }

        try {
            return trainerRepository.findTrainersByTraineeId(traineeId).stream()
                    .map(trainerResponseMapper::toTrainerProfileResponse)
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding Trainee Trainers", e);
        }
    }
}