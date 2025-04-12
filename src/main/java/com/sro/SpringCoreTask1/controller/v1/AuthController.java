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
import com.sro.SpringCoreTask1.util.response.ApiStandardError;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping(value = "/api/v1/auth", produces = "application/json")
public class AuthController {
    
    private final AuthService authService;

    public AuthController(AuthService authService) {    
        this.authService = authService;
    }
    
    @Operation(
        summary = "User login",
        description = "Authenticates a user based on provided credentials.",
        operationId = "login"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Login successful",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Invalid credentials",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiStandardError.class)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiStandardError.class)
            )
        )
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.authenticate(loginRequest.username(), loginRequest.password());
        return ResponseEntity.ok(loginResponse);
    }

    @Operation(
        summary = "Change password",
        description = "Allows authenticated users to change their password by providing the current and new passwords.",
        operationId = "changePassword"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Password changed successfully"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid password format or missing data",
            content = @Content(schema = @Schema(implementation = ApiStandardError.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized access or invalid old password",
            content = @Content(schema = @Schema(implementation = ApiStandardError.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ApiStandardError.class))
        )
    })
    @Authenticated
    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        authService.changePassword(changePasswordRequest.username(), changePasswordRequest.oldPassword(), changePasswordRequest.newPassword());
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Logout user",
        description = "Logs out the currently authenticated user by invalidating the session.",
        operationId = "logout"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Logout successful"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "User not authenticated",
            content = @Content(schema = @Schema(implementation = ApiStandardError.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ApiStandardError.class))
        )
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        authService.logout();
        return ResponseEntity.noContent().build();
    }

}
