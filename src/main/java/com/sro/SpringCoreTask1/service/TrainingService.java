package com.sro.SpringCoreTask1.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sro.SpringCoreTask1.dao.TrainingDAO;
import com.sro.SpringCoreTask1.dto.TrainingDTO;
import com.sro.SpringCoreTask1.mappers.TrainingMapper;
import com.sro.SpringCoreTask1.models.Training;
import com.sro.SpringCoreTask1.models.id.TrainingId;

@Service
public class TrainingService {
    
    @Autowired
    private TrainingDAO trainingDAO;

    public TrainingDTO save(TrainingDTO trainingDTO) {
        Training training = TrainingMapper.toEntity(trainingDTO);
        return TrainingMapper.toDTO(this.trainingDAO.save(training));
    }

    public TrainingDTO findById(TrainingId id) {
        return this.trainingDAO.findById(id).map(TrainingMapper::toDTO).orElse(null);
    }

    public List<TrainingDTO> findAll() {
        return this.trainingDAO.findAll().stream().map(TrainingMapper::toDTO).toList();
    }

    public void delete(TrainingId id) {
        this.trainingDAO.delete(id);
    }

    public TrainingDTO update(TrainingDTO trainingDTO) {
        Training training = TrainingMapper.toEntity(trainingDTO);
        return TrainingMapper.toDTO(this.trainingDAO.update(training));
    }
}
