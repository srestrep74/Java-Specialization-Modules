package com.sro.SpringCoreTask1.facade;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.sro.SpringCoreTask1.dto.TraineeTrainingFilterDTO;
import com.sro.SpringCoreTask1.dto.TrainerTrainingFilterDTO;
import com.sro.SpringCoreTask1.dto.request.TraineeRequestDTO;
import com.sro.SpringCoreTask1.dto.request.TrainerRequestDTO;
import com.sro.SpringCoreTask1.dto.request.TrainingRequestDTO;
import com.sro.SpringCoreTask1.dto.response.TraineeResponseDTO;
import com.sro.SpringCoreTask1.dto.response.TrainerResponseDTO;
import com.sro.SpringCoreTask1.dto.response.TrainingResponseDTO;
import com.sro.SpringCoreTask1.dto.response.TrainingTypeResponseDTO;
import com.sro.SpringCoreTask1.exception.DatabaseOperationException;
import com.sro.SpringCoreTask1.exception.ResourceNotFoundException;
import com.sro.SpringCoreTask1.exception.ResourceAlreadyExistsException;
import com.sro.SpringCoreTask1.service.AuthService;
import com.sro.SpringCoreTask1.service.TraineeService;
import com.sro.SpringCoreTask1.service.TrainerService;
import com.sro.SpringCoreTask1.service.TrainingService;
import com.sro.SpringCoreTask1.service.TrainingTypeService;

@Component
public class SystemServiceFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;
    private final TrainingTypeService trainingTypeService;
    private final AuthService authService;

    public SystemServiceFacade(
            TraineeService traineeService,
            TrainerService trainerService,
            TrainingService trainingService,
            TrainingTypeService trainingTypeService,
            AuthService authService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
        this.trainingTypeService = trainingTypeService;
        this.authService = authService;
    }

    public TrainerResponseDTO createTrainerProfile(TrainerRequestDTO trainerRequestDTO) {
        if (trainerRequestDTO == null) {
            throw new IllegalArgumentException("TrainerRequestDTO cannot be null");
        }
        try {
            return trainerService.save(trainerRequestDTO);
        } catch (ResourceAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error creating Trainer profile", e);
        }
    }

    public TraineeResponseDTO createTraineeProfile(TraineeRequestDTO traineeRequestDTO) {
        if (traineeRequestDTO == null) {
            throw new IllegalArgumentException("TraineeRequestDTO cannot be null");
        }
        try {
            return traineeService.save(traineeRequestDTO);
        } catch (ResourceAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error creating Trainee profile", e);
        }
    }

    public boolean authenticateTrainee(String username, String password) {
        if (username == null || password == null) {
            throw new IllegalArgumentException("Username and password cannot be null");
        }
        try {
            return authService.authenticateTrainee(username, password);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error authenticating Trainee", e);
        }
    }

    public boolean authenticateTrainer(String username, String password) {
        if (username == null || password == null) {
            throw new IllegalArgumentException("Username and password cannot be null");
        }
        try {
            return authService.authenticateTrainer(username, password);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error authenticating Trainer", e);
        }
    }

    public Long getCurrentTraineeId() {
        try {
            return authService.getCurrentTraineeId();
        } catch (Exception e) {
            throw new DatabaseOperationException("Error getting current Trainee ID", e);
        }
    }

    public Long getCurrentTrainerId() {
        try {
            return authService.getCurrentTrainerId();
        } catch (Exception e) {
            throw new DatabaseOperationException("Error getting current Trainer ID", e);
        }
    }

    public boolean isTraineeAuthenticated() {
        try {
            return authService.isTraineeAuthenticated();
        } catch (Exception e) {
            throw new DatabaseOperationException("Error checking Trainee authentication status", e);
        }
    }

    public boolean isTrainerAuthenticated() {
        try {
            return authService.isTrainerAuthenticated();
        } catch (Exception e) {
            throw new DatabaseOperationException("Error checking Trainer authentication status", e);
        }
    }

    public void logout() {
        try {
            authService.logout();
        } catch (Exception e) {
            throw new DatabaseOperationException("Error logging out", e);
        }
    }

    public boolean validateTraineeCredentials(String username, String password) {
        if (username == null || password == null) {
            throw new IllegalArgumentException("Username and password cannot be null");
        }
        try {
            return authService.authenticateTrainee(username, password);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error validating Trainee credentials", e);
        }
    }

    public boolean validateTrainerCredentials(String username, String password) {
        if (username == null || password == null) {
            throw new IllegalArgumentException("Username and password cannot be null");
        }
        try {
            return authService.authenticateTrainer(username, password);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error validating Trainer credentials", e);
        }
    }

    public TraineeResponseDTO selectTraineeProfileByUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        try {
            return traineeService.findByUsername(username);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding Trainee by username", e);
        }
    }

    public TrainerResponseDTO selectTrainerProfileByUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        try {
            return trainerService.findByUsername(username);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding Trainer by username", e);
        }
    }

    public TrainerResponseDTO getTrainerById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Trainer ID cannot be null");
        }
        try {
            return trainerService.findById(id);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding Trainer by ID", e);
        }
    }

    public TraineeResponseDTO getTraineeById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Trainee ID cannot be null");
        }
        try {
            return traineeService.findById(id);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding Trainee by ID", e);
        }
    }

    public boolean changeTraineePassword(Long traineeId, String newPassword) {
        if (traineeId == null || newPassword == null || newPassword.isEmpty()) {
            throw new IllegalArgumentException("Trainee ID and new password cannot be null or empty");
        }
        try {
            return traineeService.updateTraineePassword(traineeId, newPassword);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error updating Trainee password", e);
        }
    }

    public boolean changeTrainerPassword(Long trainerId, String newPassword) {
        if (trainerId == null || newPassword == null || newPassword.isEmpty()) {
            throw new IllegalArgumentException("Trainer ID and new password cannot be null or empty");
        }
        try {
            return trainerService.updateTrainerPassword(trainerId, newPassword);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error updating Trainer password", e);
        }
    }

    public TrainerResponseDTO updateTrainerProfile(TrainerRequestDTO trainerRequestDTO) {
        if (trainerRequestDTO == null) {
            throw new IllegalArgumentException("TrainerRequestDTO cannot be null");
        }
        try {
            return trainerService.update(trainerRequestDTO);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error updating Trainer profile", e);
        }
    }

    public TraineeResponseDTO updateTraineeProfile(TraineeRequestDTO traineeRequestDTO) {
        if (traineeRequestDTO == null) {
            throw new IllegalArgumentException("TraineeRequestDTO cannot be null");
        }
        try {
            return traineeService.update(traineeRequestDTO);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error updating Trainee profile", e);
        }
    }

    public void setTraineeStatus(Long traineeId) {
        if (traineeId == null) {
            throw new IllegalArgumentException("Trainee ID cannot be null");
        }
        try {
            traineeService.setTraineeStatus(traineeId);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error setting Trainee status", e);
        }
    }

    public void toggleTrainerStatus(Long trainerId) {
        if (trainerId == null) {
            throw new IllegalArgumentException("Trainer ID cannot be null");
        }
        try {
            trainerService.toggleTrainerStatus(trainerId);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error setting Trainer status", e);
        }
    }

    public void deleteTraineeProfileByUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        try {
            traineeService.deleteByUsername(username);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error deleting Trainee profile by username", e);
        }
    }

    public List<TrainingResponseDTO> getTraineeTrainingsList(String traineeUsername, TraineeTrainingFilterDTO filterDTO) {
        if (traineeUsername == null || traineeUsername.isEmpty()) {
            throw new IllegalArgumentException("Trainee username cannot be null or empty");
        }
        try {
            return trainingService.findTrainingsByTraineeWithFilters(filterDTO);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error getting Trainee trainings list", e);
        }
    }

    public List<TrainingResponseDTO> getTrainerTrainingsList(String trainerUsername, TrainerTrainingFilterDTO filterDTO) {
        if (trainerUsername == null || trainerUsername.isEmpty()) {
            throw new IllegalArgumentException("Trainer username cannot be null or empty");
        }
        try {
            return trainingService.findTrainingsByTrainerWithFilters(filterDTO);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error getting Trainer trainings list", e);
        }
    }

    public TrainingResponseDTO addTraining(TrainingRequestDTO trainingRequestDTO) {
        if (trainingRequestDTO == null) {
            throw new IllegalArgumentException("TrainingRequestDTO cannot be null");
        }
        try {
            return trainingService.save(trainingRequestDTO);
        } catch (ResourceAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error adding Training", e);
        }
    }

    public List<TrainerResponseDTO> findUnassignedTrainersByTraineeUsername(String traineeUsername) {
        if (traineeUsername == null || traineeUsername.isEmpty()) {
            throw new IllegalArgumentException("Trainee username cannot be null or empty");
        }
        try {
            return trainerService.findUnassignedTrainersByTraineeUsername(traineeUsername);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error getting Trainers not assigned to Trainee", e);
        }
    }

    public List<TrainingTypeResponseDTO> getTrainingTypes() {
        try {
            return trainingTypeService.findAll();
        } catch (Exception e) {
            throw new DatabaseOperationException("Error getting Training Types", e);
        }
    }

    public void updateTraineeTrainersList(Long traineeId, Long trainerId, String action) {
        if (traineeId == null || trainerId == null || action == null) {
            throw new IllegalArgumentException("Trainee ID, Trainer ID, and action cannot be null");
        }
        if (!action.equalsIgnoreCase("add") && !action.equalsIgnoreCase("remove")) {
            throw new IllegalArgumentException("Action must be 'add' or 'remove'");
        }
        try {
            if (action.equalsIgnoreCase("add")) {
                traineeService.addTrainerToTrainee(traineeId, trainerId);
            } else {
                traineeService.removeTrainerFromTrainee(traineeId, trainerId);
            }
        } catch (ResourceNotFoundException | ResourceAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error updating Trainee's Trainers list", e);
        }
    }

    public Set<TrainerResponseDTO> findTrainersByTraineeId(Long traineeId) {
        if (traineeId == null) {
            throw new IllegalArgumentException("Trainee ID cannot be null");
        }
        try {
            return trainerService.findTrainersByTraineeId(traineeId);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error getting Trainee's Trainers", e);
        }
    }
}