package com.sro.SpringCoreTask1.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sro.SpringCoreTask1.dao.TraineeDAO;
import com.sro.SpringCoreTask1.models.Trainee;
import com.sro.SpringCoreTask1.util.ProfileUtil;

@Service
public class TraineeService {
    
    @Autowired
    private TraineeDAO traineeDAO;

    public Trainee save(Trainee trainee) {
        List<String> existingUsernames = traineeDAO.findAll().stream().map(Trainee::getUserName).toList();

        String username = ProfileUtil.generateUsername(trainee.getFirstName(), trainee.getLastName(), existingUsernames);
        String password = ProfileUtil.generatePassword();

        trainee.setUserName(username);
        trainee.setPassword(password);
        return traineeDAO.save(trainee);
    }

    public Trainee findById(Long id) {
        return this.traineeDAO.findById(id).get();
    }

    public List<Trainee> findAll() {
        return this.traineeDAO.findAll();
    }

    public void delete(Long id) {
        this.traineeDAO.delete(id);
    }

    public Trainee update(Trainee trainee) {
        return this.traineeDAO.update(trainee);
    }
}
