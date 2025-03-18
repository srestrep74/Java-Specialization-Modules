package com.sro.SpringCoreTask1.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.sro.SpringCoreTask1.exception.StorageInitializationException;
import com.sro.SpringCoreTask1.util.storage.InitialData;
import com.sro.SpringCoreTask1.util.storage.JsonFileReader;

import jakarta.annotation.PostConstruct;

@Service
public class DataInitializationService {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;
    private final TrainingTypeService trainingTypeService;

    @Value("${storage.init.file}")
    Resource initDataFile;

    public DataInitializationService(TraineeService traineeService, TrainerService trainerService, TrainingService trainingService, TrainingTypeService trainingTypeService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
        this.trainingTypeService = trainingTypeService;
    }

    @PostConstruct
    public void initializeData() {
        try {
            InitialData initialData = JsonFileReader.readJsonFile(initDataFile, InitialData.class);

            initialData.getTrainingTypes().forEach(this.trainingTypeService::save);
            initialData.getTrainers().forEach(this.trainerService::save);
            initialData.getTrainees().forEach(this.traineeService::save);
            initialData.getTrainings().forEach(this.trainingService::save);
        } catch (Exception e) {
            throw new StorageInitializationException("Failed to initialize storage with data: ", e);
        }
    }
}
