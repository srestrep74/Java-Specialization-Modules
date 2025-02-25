package com.sro.SpringCoreTask1.facade;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sro.SpringCoreTask1.dto.TraineeDTO;
import com.sro.SpringCoreTask1.dto.TrainerDTO;
import com.sro.SpringCoreTask1.dto.TrainingDTO;
import com.sro.SpringCoreTask1.models.id.TrainingId;
import com.sro.SpringCoreTask1.service.TraineeService;
import com.sro.SpringCoreTask1.service.TrainerService;
import com.sro.SpringCoreTask1.service.TrainingService;

@Service
public class TrainingFacade {
    
    private final TrainingService trainingService;
    private final TraineeService traineeService;
    private final TrainerService trainerService;

    public TrainingFacade(TrainingService trainingService, TraineeService traineeService, TrainerService trainerService) {
        this.trainingService = trainingService; 
        this.traineeService = traineeService;
        this.trainerService = trainerService;
    }

    // Trainee Methods
    public TraineeDTO saveTrainee(TraineeDTO trainee) {
        return traineeService.save(trainee);
    }

    public TraineeDTO updateTrainee(TraineeDTO trainee) {
        return traineeService.update(trainee);
    }

    public TraineeDTO findTraineeById(Long id) {
        return traineeService.findById(id);
    }

    public List<TraineeDTO> findAllTrainees() {
        return traineeService.findAll();
    }

    public void deleteTrainee(Long id) {
        traineeService.delete(id);
    }

    // Trainer Methods
    public TrainerDTO saveTrainer(TrainerDTO trainer) {
        return trainerService.save(trainer);
    }

    public TrainerDTO updateTrainer(TrainerDTO trainer) {
        return trainerService.update(trainer);
    }

    public TrainerDTO findTrainerById(Long id) {    
        return trainerService.findById(id);
    }

    public List<TrainerDTO> findAllTrainers() {
        return trainerService.findAll();
    }

    public void deleteTrainer(Long id) {
        trainerService.delete(id);
    }

    // Training Methods
    public TrainingDTO saveTraining(TrainingDTO training) {
        return trainingService.save(training);
    }

    public TrainingDTO updateTraining(TrainingDTO training) {
        return trainingService.update(training);
    }

    public TrainingDTO findTrainingById(TrainingId id) {
        return trainingService.findById(id);
    }

    public List<TrainingDTO> findAllTrainings() {
        return trainingService.findAll();
    }

    public void deleteTraining(TrainingId id) {
        trainingService.delete(id);
    }

}
