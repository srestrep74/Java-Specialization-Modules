package com.sro.SpringCoreTask1.controller.v1;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sro.SpringCoreTask1.dtos.v1.request.training.CreateTrainingRequest;
import com.sro.SpringCoreTask1.service.TrainingService;
import com.sro.SpringCoreTask1.util.response.ApiStandardError;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/api/v1/trainings", produces = "application/json")
@Tag(name = "Training Management", description = "Operations pertaining to training sessions in the system")
@SecurityRequirement(name = "bearerAuth")
public class TrainingController {
    
    private final TrainingService trainingService;

    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @Operation(
        summary = "Create a new training session",
        description = "Registers a new training session between a trainee and trainer. "
            + "Requires valid trainee and trainer usernames, training name, date and duration. "
            + "Requires authentication with either TRAINEE or TRAINER role. "
            + "The trainer specified must be assigned to the trainee for the training session to be created successfully.",
        operationId = "createTraining",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Training session created successfully",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiStandardError.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Authentication token missing or invalid",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiStandardError.class)
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - User does not have required role (TRAINEE or TRAINER)",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiStandardError.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Trainee, trainer, or training type not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiStandardError.class)
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Conflict with existing training session",
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
    @PreAuthorize("hasRole('TRAINEE') or hasRole('TRAINER')")
    @PostMapping
    public ResponseEntity<Void> createTraining(
            @Parameter(description = "Details of the training session to create") 
            @Valid @RequestBody CreateTrainingRequest createTrainingRequest) {
        trainingService.save(createTrainingRequest);
        return ResponseEntity.ok().build();
    }

}