package com.sro.SpringCoreTask1.util.Storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.sro.SpringCoreTask1.dao.TraineeDAO;
import com.sro.SpringCoreTask1.dao.TrainerDAO;
import com.sro.SpringCoreTask1.dao.TrainingDAO;
import com.sro.SpringCoreTask1.exceptions.StorageInitializationException;


@Component
public class StorageInitializer implements CommandLineRunner{
    
    @Value("${storage.init.file}")
    private Resource initDataFile;

    private final TrainerDAO trainerDAO;
    private final TraineeDAO traineeDAO;
    private final TrainingDAO trainingDAO;

    public StorageInitializer(TrainerDAO trainerDAO, TraineeDAO traineeDAO, TrainingDAO trainingDAO) {
        this.trainerDAO = trainerDAO;
        this.traineeDAO = traineeDAO;
        this.trainingDAO = trainingDAO;
    }

    @Override
    public void run(String ...args){
        try {
            InitialData initialData = JsonFileReader.readJsonFile(initDataFile, InitialData.class);

            initialData.getTrainers().forEach(trainer -> 
                this.trainerDAO.save(trainer));
            
            initialData.getTrainees().forEach(trainee ->
                this.traineeDAO.save(trainee));

            initialData.getTrainings().forEach(training -> 
                this.trainingDAO.save(training));

        }catch (Exception e) {
            throw new StorageInitializationException("Failed to initialize storage with data: ", e);
        }
    }
}
