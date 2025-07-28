package dev.sro.gym_service.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import dev.sro.gym_service.config.properties.StorageProperties;
import dev.sro.gym_service.exception.StorageInitializationException;
import dev.sro.gym_service.util.storage.InitialData;
import dev.sro.gym_service.util.storage.JsonFileReader;

import jakarta.annotation.PostConstruct;

@Service
@Profile("!prod")
public class DataInitializationService {

    private final DataSeedService dataSeedService;
    private final StorageProperties storageProperties;

    public DataInitializationService(DataSeedService dataSeedService, StorageProperties storageProperties) {
        this.dataSeedService = dataSeedService;
        this.storageProperties = storageProperties;
    }

    @PostConstruct
    public void initializeData() {
        try {
            InitialData initialData = JsonFileReader.readJsonFile(storageProperties.initFile(), InitialData.class);

            initialData.getTrainingTypes().forEach(this.dataSeedService::seedTrainingType);
            initialData.getTrainers().forEach(this.dataSeedService::seedTrainer);
            initialData.getTrainees().forEach(this.dataSeedService::seedTrainee);
            initialData.getTrainings().forEach(this.dataSeedService::seedTraining);
        } catch (Exception e) {
            throw new StorageInitializationException("Failed to initialize storage with data: ", e);
        }
    }
}
