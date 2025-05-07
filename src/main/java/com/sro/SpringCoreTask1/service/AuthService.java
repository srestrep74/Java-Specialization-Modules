package com.sro.SpringCoreTask1.service;

import com.sro.SpringCoreTask1.dtos.v1.response.auth.LoginResponse;
import com.sro.SpringCoreTask1.entity.User;

public interface AuthService {
    LoginResponse authenticate(String username, String password);
    
    LoginResponse refreshToken(String refreshToken);
    
    void invalidateToken(String token);

    boolean isAuthenticated();

    boolean isCurrentUserTrainee();

    boolean isCurrentUserTrainer();

    User getCurrentUser();

    void changePassword(String username, String oldPassword, String newPassword);
    
    void logoutWithToken(String authorizationHeader);

    void logout();

    void setCurrentUser(User user);
}