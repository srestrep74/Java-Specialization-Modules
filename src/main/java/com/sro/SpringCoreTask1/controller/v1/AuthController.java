package com.sro.SpringCoreTask1.controller.v1;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sro.SpringCoreTask1.annotations.Authenticated;
import com.sro.SpringCoreTask1.dtos.v1.request.auth.ChangePasswordRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.auth.LoginRequest;
import com.sro.SpringCoreTask1.dtos.v1.response.auth.LoginResponse;
import com.sro.SpringCoreTask1.service.AuthService;

@RestController
@RequestMapping(value = "/api/v1/auth", produces = "application/json")
public class AuthController {
    
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.authenticate(loginRequest.username(), loginRequest.password());
        return ResponseEntity.ok(loginResponse);
    }

    @Authenticated
    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        authService.changePassword(changePasswordRequest.username(), changePasswordRequest.oldPassword(), changePasswordRequest.newPassword());
        return ResponseEntity.noContent().build();
    }

}
