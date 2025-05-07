package com.sro.SpringCoreTask1.service.impl.auth;

import com.sro.SpringCoreTask1.entity.Trainee;
import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.repository.TraineeRepository;
import com.sro.SpringCoreTask1.repository.TrainerRepository;
import com.sro.SpringCoreTask1.security.CustomUserDetails;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomUserDetailsService(TraineeRepository traineeRepository, TrainerRepository trainerRepository,
            PasswordEncoder passwordEncoder) {
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Trainee> trainee = traineeRepository.findByUsername(username);
        if (trainee.isPresent()) {
            return new CustomUserDetails(trainee.get());
        }

        Optional<Trainer> trainer = trainerRepository.findByUsername(username);
        if (trainer.isPresent()) {
            return new CustomUserDetails(trainer.get());
        }

        throw new UsernameNotFoundException("User not found with username: " + username);
    }

    public UserDetails loadUserByUsernameAndPassword(String username, String password)
            throws UsernameNotFoundException {
        Optional<Trainee> trainee = traineeRepository.findByUsername(username);
        if (trainee.isPresent() && passwordEncoder.matches(password, trainee.get().getPassword())) {
            return new CustomUserDetails(trainee.get());
        }

        Optional<Trainer> trainer = trainerRepository.findByUsername(username);
        if (trainer.isPresent() && passwordEncoder.matches(password, trainer.get().getPassword())) {
            return new CustomUserDetails(trainer.get());
        }

        throw new UsernameNotFoundException("Invalid username or password");
    }
}