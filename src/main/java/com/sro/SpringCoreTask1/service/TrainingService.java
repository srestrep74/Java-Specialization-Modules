package com.sro.SpringCoreTask1.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sro.SpringCoreTask1.dao.TrainingDAO;
import com.sro.SpringCoreTask1.models.Training;
import com.sro.SpringCoreTask1.models.id.TrainingId;

@Service
public class TrainingService {
    
    @Autowired
    private TrainingDAO trainingDAO;

    public Training save(Training training) {
        return trainingDAO.save(training);
    }

    public Training findById(TrainingId id) {
        return this.trainingDAO.findById(id).get();
    }

    public List<Training> findAll() {
        return this.trainingDAO.findAll();
    }

    public void delete(TrainingId id) {
        this.trainingDAO.delete(id);
    }

    public Training update(Training training) {
        return this.trainingDAO.update(training);
    }
}
