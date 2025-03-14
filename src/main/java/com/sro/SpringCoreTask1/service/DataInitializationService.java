package com.sro.SpringCoreTask1.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.sro.SpringCoreTask1.exception.StorageInitializationException;
import com.sro.SpringCoreTask1.repository.TraineeRepository;
import com.sro.SpringCoreTask1.repository.TrainerRepository;
import com.sro.SpringCoreTask1.repository.TrainingRepository;
import com.sro.SpringCoreTask1.repository.TrainingTypeRepository;
import com.sro.SpringCoreTask1.util.storage.InitialData;
import com.sro.SpringCoreTask1.util.storage.JsonFileReader;

import jakarta.annotation.PostConstruct;

@Service
public class DataInitializationService {

    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;
    private final TrainingRepository trainingRepository;
    private final TrainingTypeRepository trainingTypeRepository;

    @Value("${storage.init.file}")
    Resource initDataFile;

    public DataInitializationService(TrainerRepository trainerRepository, TraineeRepository traineeRepository, TrainingRepository trainingRepository, TrainingTypeRepository trainingTypeRepository) {
        this.trainerRepository = trainerRepository;
        this.traineeRepository = traineeRepository;
        this.trainingRepository = trainingRepository;
        this.trainingTypeRepository = trainingTypeRepository;
    }

    @PostConstruct
    public void initializeData() {
        try {
            InitialData initialData = JsonFileReader.readJsonFile(initDataFile, InitialData.class);

            initialData.getTrainingTypes().forEach(this.trainingTypeRepository::save);
            initialData.getTrainees().forEach(this.traineeRepository::save);
            initialData.getTrainers().forEach(this.trainerRepository::save);
            initialData.getTrainings().forEach(this.trainingRepository::save);
        } catch (Exception e) {
            throw new StorageInitializationException("Failed to initialize storage with data: ", e);
        }
    }
}
