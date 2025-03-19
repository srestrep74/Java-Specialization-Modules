package com.sro.SpringCoreTask1.facade;

public interface AuthServiceFacade {
    boolean authenticateTrainee(String username, String password);
    boolean authenticateTrainer(String username, String password);
    Long getAuthenticatedTraineeId();
    Long getAuthenticatedTrainerId();
    boolean isTraineeAuthenticated();
    boolean isTrainerAuthenticated();
    void perfomLogout();
    boolean verifyTraineeCredentials(String username, String password);
    boolean verifyTrainerCredentials(String username, String password);
}
