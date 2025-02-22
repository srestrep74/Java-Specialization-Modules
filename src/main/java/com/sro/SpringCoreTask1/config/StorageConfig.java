package com.sro.SpringCoreTask1.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sro.SpringCoreTask1.models.Trainee;
import com.sro.SpringCoreTask1.models.Trainer;
import com.sro.SpringCoreTask1.models.Training;
import com.sro.SpringCoreTask1.models.id.TrainingId;

@Configuration
public class StorageConfig {
    
    @Bean
    public Map<Long, Trainer> trainerStorage() {
        return new HashMap<>();
    }

    @Bean
    public Map<Long, Trainee> traineeStorage() {
        return new HashMap<>();
    }

    @Bean
    public Map<TrainingId, Training> trainingStorage() {
        return new HashMap<>();
    }
}
