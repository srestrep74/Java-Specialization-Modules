package com.sro.SpringCoreTask1.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sro.SpringCoreTask1.dao.TrainerDAO;
import com.sro.SpringCoreTask1.dto.TrainerDTO;
import com.sro.SpringCoreTask1.mappers.TrainerMapper;
import com.sro.SpringCoreTask1.models.Trainer;
import com.sro.SpringCoreTask1.util.ProfileUtil;

@Service
public class TrainerService {
    
    @Autowired
    private TrainerDAO trainerDAO;


    public TrainerDTO save(TrainerDTO trainerDTO) {
        List<String> existingUsernames = trainerDAO.findAll().stream().map(Trainer::getUserName).toList();

        Trainer trainer = TrainerMapper.toEntity(trainerDTO);
        
        String username = ProfileUtil.generateUsername(trainer.getFirstName(), trainer.getLastName(), existingUsernames);
        String password = ProfileUtil.generatePassword();

        trainer.setUserName(username);
        trainer.setPassword(password);
        return TrainerMapper.toDTO(trainerDAO.save(trainer));
    }

    public TrainerDTO findById(Long id) {
        return this.trainerDAO.findById(id).map(TrainerMapper::toDTO).orElse(null);
    }

    public List<TrainerDTO> findAll() {
        return this.trainerDAO.findAll().stream().map(TrainerMapper::toDTO).toList();
    }

    public void delete(Long id) {
        this.trainerDAO.delete(id);
    }

    public TrainerDTO update(TrainerDTO trainerDTO) {
        Trainer trainer = TrainerMapper.toEntity(trainerDTO);
        return TrainerMapper.toDTO(this.trainerDAO.update(trainer));
    }
}
