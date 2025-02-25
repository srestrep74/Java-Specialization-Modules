package com.sro.SpringCoreTask1.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sro.SpringCoreTask1.dao.TraineeDAO;
import com.sro.SpringCoreTask1.dto.TraineeDTO;
import com.sro.SpringCoreTask1.mappers.TraineeMapper;
import com.sro.SpringCoreTask1.models.Trainee;
import com.sro.SpringCoreTask1.util.ProfileUtil;

@Service
public class TraineeService {
    
    @Autowired
    private TraineeDAO traineeDAO;

    public TraineeDTO save(TraineeDTO traineeDTO) {
        List<String> existingUsernames = traineeDAO.findAll().stream().map(Trainee::getUserName).toList();

        Trainee trainee = TraineeMapper.toEntity(traineeDTO);

        String username = ProfileUtil.generateUsername(trainee.getFirstName(), trainee.getLastName(), existingUsernames);
        String password = ProfileUtil.generatePassword();

        trainee.setUserName(username);
        trainee.setPassword(password);
        return TraineeMapper.toDTO(traineeDAO.save(trainee));
    }

    public TraineeDTO findById(Long id) {
        return this.traineeDAO.findById(id).map(TraineeMapper::toDTO).orElse(null);
    }

    public List<TraineeDTO> findAll() {
        return this.traineeDAO.findAll().stream().map(TraineeMapper::toDTO).toList();
    }

    public void delete(Long id) {
        this.traineeDAO.delete(id);
    }

    public TraineeDTO update(TraineeDTO traineeDTO) {
        Trainee trainee = TraineeMapper.toEntity(traineeDTO);
        return TraineeMapper.toDTO(this.traineeDAO.update(trainee));
    }
}
