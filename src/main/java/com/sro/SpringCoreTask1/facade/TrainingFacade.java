package com.sro.SpringCoreTask1.facade;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sro.SpringCoreTask1.models.Trainee;
import com.sro.SpringCoreTask1.models.Trainer;
import com.sro.SpringCoreTask1.models.Training;
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
    public Trainee saveTrainee(Trainee trainee) {
        return traineeService.save(trainee);
    }

    public Trainee updateTrainee(Trainee trainee) {
        return traineeService.update(trainee);
    }

    public Trainee findTraineeById(Long id) {
        return traineeService.findById(id);
    }

    public List<Trainee> findAllTrainees() {
        return traineeService.findAll();
    }

    public void deleteTrainee(Long id) {
        traineeService.delete(id);
    }

    // Trainer Methods
    public Trainer saveTrainer(Trainer trainer) {
        return trainerService.save(trainer);
    }

    public Trainer updateTrainer(Trainer trainer) {
        return trainerService.update(trainer);
    }

    public Trainer findTrainerById(Long id) {    
        return trainerService.findById(id);
    }

    public List<Trainer> findAllTrainers() {
        return trainerService.findAll();
    }

    public void deleteTrainer(Long id) {
        trainerService.delete(id);
    }

    // Training Methods
    public Training saveTraining(Training training) {
        return trainingService.save(training);
    }

    public Training updateTraining(Training training) {
        return trainingService.update(training);
    }

    public Training findTrainingById(TrainingId id) {
        return trainingService.findById(id);
    }

    public List<Training> findAllTrainings() {
        return trainingService.findAll();
    }

    public void deleteTraining(TrainingId id) {
        trainingService.delete(id);
    }

}
