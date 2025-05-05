package com.sro.SpringCoreTask1.service.impl.auth;

import com.sro.SpringCoreTask1.dtos.v1.response.auth.LoginResponse;
import com.sro.SpringCoreTask1.entity.Trainee;
import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.entity.User;
import com.sro.SpringCoreTask1.exception.AuthenticationFailedException;
import com.sro.SpringCoreTask1.exception.DatabaseOperationException;
import com.sro.SpringCoreTask1.repository.TraineeRepository;
import com.sro.SpringCoreTask1.repository.TrainerRepository;
import com.sro.SpringCoreTask1.security.CustomUserDetails;
import com.sro.SpringCoreTask1.service.AuthService;
import com.sro.SpringCoreTask1.service.LoginAttemptService;
import com.sro.SpringCoreTask1.util.jwt.JwtUtil;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final LoginAttemptService loginAttemptService;

    private User authenticatedUser;
    private boolean isTrainee;

    public AuthServiceImpl(
            TraineeRepository traineeRepository,
            TrainerRepository trainerRepository,
            JwtUtil jwtUtil,
            CustomUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder,
            LoginAttemptService loginAttemptService) {
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse authenticate(String username, String password) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Username and password cannot be null or empty");
        }

        if (loginAttemptService.isBlocked(username)) {
            long remainingMinutes = loginAttemptService.getBlockDuration();
            throw new AuthenticationFailedException("Account is locked. Try again in " + remainingMinutes + " minutes");
        }

        try {
            UserDetails userDetails = userDetailsService.loadUserByUsernameAndPassword(username, password);
            String token = jwtUtil.generateToken(userDetails);

            if (userDetails instanceof CustomUserDetails) {
                this.authenticatedUser = ((CustomUserDetails) userDetails).getUser();
                this.isTrainee = ((CustomUserDetails) userDetails).getRole().equals("TRAINEE");
            }

            loginAttemptService.resetAttempts(username);

            return new LoginResponse(username, true, token);
        } catch (UsernameNotFoundException e) {
            loginAttemptService.registerFailedAttempt(username);
            int remainingAttempts = loginAttemptService.getRemainingAttempts(username);
            String errorMsg = "Invalid username or password";
            if (remainingAttempts <= 0) {
                errorMsg += String.format(". Account locked for %d minutes",
                        loginAttemptService.getBlockDuration());
            } else {
                errorMsg += String.format(". %d attempts remaining", remainingAttempts);
            }
            throw new AuthenticationFailedException(errorMsg);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error during authentication", e);
        }
    }

    @Override
    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        if (username == null || username.isEmpty() ||
                oldPassword == null || oldPassword.isEmpty() ||
                newPassword == null || newPassword.isEmpty()) {
            throw new IllegalArgumentException("Username, old password and new password cannot be null or empty");
        }

        try {
            LoginResponse loginResponse = authenticate(username, oldPassword);

            if (!loginResponse.success()) {
                throw new AuthenticationFailedException("Current password is incorrect");
            }

            if (isCurrentUserTrainee()) {
                Trainee trainee = (Trainee) authenticatedUser;
                if (!passwordEncoder.matches(oldPassword, trainee.getPassword())) {
                    throw new AuthenticationFailedException("Current password is incorrect");
                }
                trainee.setPassword(passwordEncoder.encode(newPassword));
                traineeRepository.save(trainee);
            } else {
                Trainer trainer = (Trainer) authenticatedUser;
                if (!passwordEncoder.matches(oldPassword, trainer.getPassword())) {
                    throw new AuthenticationFailedException("Current password is incorrect");
                }
                trainer.setPassword(passwordEncoder.encode(newPassword));
                trainerRepository.save(trainer);
            }
        } catch (AuthenticationFailedException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error changing password", e);
        }
    }

    @Override
    public void logout() {
        this.authenticatedUser = null;
    }

    @Override
    public User getCurrentUser() {
        return this.authenticatedUser;
    }

    @Override
    public void setCurrentUser(User user) {
        this.authenticatedUser = user;
    }

    @Override
    public boolean isAuthenticated() {
        return this.authenticatedUser != null;
    }

    @Override
    public boolean isCurrentUserTrainee() {
        return isAuthenticated() && this.isTrainee;
    }

    @Override
    public boolean isCurrentUserTrainer() {
        return isAuthenticated() && !this.isTrainee;
    }

}