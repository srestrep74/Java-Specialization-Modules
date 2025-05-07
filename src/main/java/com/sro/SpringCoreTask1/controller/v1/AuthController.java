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
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "/api/v1/auth", produces = "application/json")
@Tag(name = "Authentication", description = "Authentication operations for users including login, logout, refresh token and password management")
public class AuthController {
    
    private final AuthService authService;

    public AuthController(AuthService authService) {    
        this.authService = authService;
    }
    
    @Operation(
        summary = "User login",
        description = "Authenticates a user based on provided credentials and returns JWT tokens. " +
                     "The response includes both access token (short-lived) and refresh token (long-lived) " +
                     "that can be used to obtain a new access token when it expires.",
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
            description = "Invalid credentials. Response includes remaining attempts before account lockout.",
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
        description = "Allows authenticated users to change their password by providing the current and new passwords. " +
                     "This endpoint requires a valid JWT token.",
        operationId = "changePassword",
        security = { @SecurityRequirement(name = "bearerAuth") }
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
        description = "Logs out the currently authenticated user by invalidating all their tokens. " +
                     "This endpoint invalidates both the current access token and all refresh tokens " +
                     "associated with the user, preventing token reuse after logout.",
        operationId = "logout",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Logout successful. All user tokens have been invalidated."
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
        // Extract token from Authorization header
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            try {
                // Explicitly invalidate the access token
                authService.invalidateToken(jwt);
            } catch (Exception e) {
                // Continue with logout even if token invalidation fails
            }
        }
        // Invalidate all refresh tokens and clear user session
        authService.logout();
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Refresh access token",
        description = "Generates new access and refresh tokens using a valid refresh token. " +
                     "The previous refresh token is invalidated as part of the rotation security mechanism, " +
                     "preventing refresh token reuse.",
        operationId = "refreshToken"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Successfully refreshed tokens. Returns new access and refresh tokens.",
            content = @Content(schema = @Schema(implementation = LoginResponse.class))
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Invalid, expired, or blacklisted refresh token",
            content = @Content(schema = @Schema(implementation = ApiStandardError.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ApiStandardError.class))
        )
    })
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshRequest) {
        LoginResponse response = authService.refreshToken(refreshRequest.refreshToken());
        return ResponseEntity.ok(response);
    }
}
