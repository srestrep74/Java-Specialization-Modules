package com.sro.SpringCoreTask1.dao.impl;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.sro.SpringCoreTask1.dao.TrainerDAO;
import com.sro.SpringCoreTask1.exceptions.EntityNotFoundException;
import com.sro.SpringCoreTask1.models.Trainer;

@Repository
public class TrainerDAOImpl implements TrainerDAO{
    
    private final Map<Long, Trainer> trainerStorage;

    public TrainerDAOImpl(Map<Long, Trainer> trainerStorage) {
        this.trainerStorage = trainerStorage;
    }

    @Override
    public Trainer save(Trainer trainer) {
        this.trainerStorage.put(trainer.getUserId(), trainer);
        return trainer;
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        return Optional.ofNullable(this.trainerStorage.get(id));
    }

    @Override
    public List<Trainer> findAll() {
        return new ArrayList<>(this.trainerStorage.values());
    }

    @Override
    public void delete(Long id) {
        if(this.trainerStorage.remove(id) == null) {
            throw new EntityNotFoundException("Trainer not found with id: " + id);
        }
    }

    @Override
    public Trainer update(Trainer trainer) {
        if(!this.trainerStorage.containsKey(trainer.getUserId())) {
            throw new EntityNotFoundException("Trainer not found with id: " + trainer.getUserId());
        }
        this.trainerStorage.put(trainer.getUserId(), trainer);
        return trainer;
    }

}
