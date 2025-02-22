package com.sro.SpringCoreTask1.dao.impl;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.sro.SpringCoreTask1.dao.TraineeDAO;
import com.sro.SpringCoreTask1.exceptions.EntityNotFoundException;
import com.sro.SpringCoreTask1.models.Trainee;

@Repository
public class TraineeDAOImpl implements TraineeDAO{
    
    private final Map<Long, Trainee> traineeStorage;

    public TraineeDAOImpl(Map<Long, Trainee> traineeStorage) {
        this.traineeStorage = traineeStorage;
    }

    @Override
    public Trainee save(Trainee trainee) {
        this.traineeStorage.put(trainee.getUserId(), trainee);
        return trainee;
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        return Optional.ofNullable(this.traineeStorage.get(id));
    }

    @Override
    public List<Trainee> findAll() {
        return new ArrayList<>(this.traineeStorage.values());
    }

    @Override
    public void delete(Long id) {
        if(this.traineeStorage.remove(id) == null) {
            throw new EntityNotFoundException("Trainee not found with id: " + id);
        }
    }

    @Override
    public Trainee update(Trainee trainee) {
        if(!this.traineeStorage.containsKey(trainee.getUserId())) {
            throw new EntityNotFoundException("Trainee not found with id: " + trainee.getUserId());
        }
        this.traineeStorage.put(trainee.getUserId(), trainee);
        return trainee;
    }

}
