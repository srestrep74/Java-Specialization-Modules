package com.sro.SpringCoreTask1.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sro.SpringCoreTask1.dao.TrainerDAO;
import com.sro.SpringCoreTask1.models.Trainer;
import com.sro.SpringCoreTask1.util.ProfileUtil;

@Service
public class TrainerService {
    
    @Autowired
    private TrainerDAO trainerDAO;


    public Trainer save(Trainer trainer) {
        List<String> existingUsernames = trainerDAO.findAll().stream().map(Trainer::getUserName).toList();
        
        String username = ProfileUtil.generateUsername(trainer.getFirstName(), trainer.getLastName(), existingUsernames);
        String password = ProfileUtil.generatePassword();

        trainer.setUserName(username);
        trainer.setPassword(password);
        return trainerDAO.save(trainer);
    }

    public Trainer findById(Long id) {
        return this.trainerDAO.findById(id).get();
    }

    public List<Trainer> findAll() {
        return this.trainerDAO.findAll();
    }

    public void delete(Long id) {
        this.trainerDAO.delete(id);
    }

    public Trainer update(Trainer trainer) {
        return this.trainerDAO.update(trainer);
    }
}
