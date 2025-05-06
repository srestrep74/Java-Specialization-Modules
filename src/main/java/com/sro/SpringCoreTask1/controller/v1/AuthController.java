package com.sro.SpringCoreTask1.controller.v1;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sro.SpringCoreTask1.dtos.v1.request.auth.ChangePasswordRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.auth.LoginRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.auth.RefreshTokenRequest;
import com.sro.SpringCoreTask1.dtos.v1.response.auth.LoginResponse;
import com.sro.SpringCoreTask1.service.AuthService;
import com.sro.SpringCoreTask1.util.response.ApiStandardError;
import com.sro.SpringCoreTask1.util.response.ApiStandardResponse;
import com.sro.SpringCoreTask1.util.response.ResponseBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
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
        description = "Authenticates a user based on provided credentials and returns a JWT token.",
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
    public ResponseEntity<ApiStandardResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.authenticate(loginRequest.username(), loginRequest.password());
        return ResponseBuilder.success(loginResponse);
    }

    @Operation(
        summary = "Change password",
        description = "Allows authenticated users to change their password by providing the current and new passwords.",
        operationId = "changePassword"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
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
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/change-password")
    public ResponseEntity<ApiStandardResponse<Void>> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        authService.changePassword(changePasswordRequest.username(), changePasswordRequest.oldPassword(), changePasswordRequest.newPassword());
        return ResponseBuilder.success(null);
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
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        // Extraer el token del encabezado Authorization
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            // Invalida explícitamente el token
            authService.invalidateToken(jwt);
            // Log para depuración
            System.out.println("Token invalidated: " + jwt);
        } else {
            System.out.println("No valid token found in request for logout");
        }
        // Eliminar la sesión actual del usuario
        authService.logout();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Refresh access token using refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully refreshed token"),
            @ApiResponse(responseCode = "401", description = "Invalid refresh token")
    })
    public ResponseEntity<LoginResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshRequest) {
        LoginResponse response = authService.refreshToken(refreshRequest.refreshToken());
        return ResponseEntity.ok(response);
    }

}
