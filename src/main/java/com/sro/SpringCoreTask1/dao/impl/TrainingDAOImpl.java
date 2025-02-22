package com.sro.SpringCoreTask1.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.sro.SpringCoreTask1.dao.TrainingDAO;
import com.sro.SpringCoreTask1.exceptions.EntityNotFoundException;
import com.sro.SpringCoreTask1.models.Training;
import com.sro.SpringCoreTask1.models.id.TrainingId;

@Repository
public class TrainingDAOImpl implements TrainingDAO{
    
    private final Map<TrainingId, Training> trainingStorage;

    public TrainingDAOImpl(Map<TrainingId, Training> trainingStorage) {
        this.trainingStorage = trainingStorage;
    }

    @Override
    public Training save(Training training) {
        this.trainingStorage.put(training.getTrainingId(), training);
        return training;
    }

    @Override
    public Optional<Training> findById(TrainingId trainingId) {
        return Optional.ofNullable(this.trainingStorage.get(trainingId));
    }

    @Override
    public List<Training> findAll() {
        return new ArrayList<>(this.trainingStorage.values());
    }

    @Override
    public void delete(TrainingId id) {
        if(this.trainingStorage.remove(id) == null) {
            throw new EntityNotFoundException("Training not found with id: " + id);
        }
    }

    @Override
    public Training update(Training training) {
        if(!this.trainingStorage.containsKey(training.getTrainingId())) {
            throw new EntityNotFoundException("Training not found with id: " + training.getTrainingId());
        }
        this.trainingStorage.put(training.getTrainingId(), training);
        return training;
    }

}

