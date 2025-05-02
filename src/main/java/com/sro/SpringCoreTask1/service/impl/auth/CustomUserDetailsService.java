package com.sro.SpringCoreTask1.service.impl.auth;

import com.sro.SpringCoreTask1.entity.Trainee;
import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.repository.TraineeRepository;
import com.sro.SpringCoreTask1.repository.TrainerRepository;
import com.sro.SpringCoreTask1.security.CustomUserDetails;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;

    public CustomUserDetailsService(TraineeRepository traineeRepository, TrainerRepository trainerRepository) {
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Ver si se puede mejorar con un solo repository global user
        Optional<Trainee> trainee = traineeRepository.findByUsername(username);
        if (trainee.isPresent()) {
            return new CustomUserDetails(trainee.get(), "TRAINEE");
        }

        Optional<Trainer> trainer = trainerRepository.findByUsername(username);
        if (trainer.isPresent()) {
            return new CustomUserDetails(trainer.get(), "TRAINER");
        }

        throw new UsernameNotFoundException("User not found with username: " + username);
    }

    public UserDetails loadUserByUsernameAndPassword(String username, String password)
            throws UsernameNotFoundException {
        Optional<Trainee> trainee = traineeRepository.findByUsername(username);
        if (trainee.isPresent() && trainee.get().getPassword().equals(password)) {
            return new CustomUserDetails(trainee.get(), "TRAINEE");
        }

        Optional<Trainer> trainer = trainerRepository.findByUsername(username);
        if (trainer.isPresent() && trainer.get().getPassword().equals(password)) {
            return new CustomUserDetails(trainer.get(), "TRAINER");
        }

        throw new UsernameNotFoundException("Invalid username or password");
    }
}