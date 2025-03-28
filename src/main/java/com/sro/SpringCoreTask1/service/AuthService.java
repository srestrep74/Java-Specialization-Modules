package com.sro.SpringCoreTask1.service;

public interface AuthService {
    boolean authenticateTrainee(String username, String password);
    boolean authenticateTrainer(String username, String password);
    Long getCurrentTraineeId();
    Long getCurrentTrainerId();
    boolean isTraineeAuthenticated();
    boolean isTrainerAuthenticated();
    void logout();
    void changeTraineePassword(String username, String newPassword);
    void changeTrainerPassword(String username, String newPassword);
}