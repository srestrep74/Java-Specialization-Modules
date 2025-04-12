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

    private final DataSeedService dataSeedService;

    @Value("${storage.init.file}")
    Resource initDataFile;

    public DataInitializationService(DataSeedService dataSeedService) {
        this.dataSeedService = dataSeedService;
    }

    @PostConstruct
    public void initializeData() {
        try {
            InitialData initialData = JsonFileReader.readJsonFile(initDataFile, InitialData.class);

            initialData.getTrainingTypes().forEach(this.dataSeedService::seedTrainingType);
            initialData.getTrainers().forEach(this.dataSeedService::seedTrainer);
            initialData.getTrainees().forEach(this.dataSeedService::seedTrainee);
            initialData.getTrainings().forEach(this.dataSeedService::seedTraining);
        } catch (Exception e) {
            throw new StorageInitializationException("Failed to initialize storage with data: ", e);
        }
    }
}
