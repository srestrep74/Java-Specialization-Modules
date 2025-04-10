package com.sro.SpringCoreTask1.controller.v1;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sro.SpringCoreTask1.dtos.v1.request.trainer.RegisterTrainerRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.trainer.UpdateTrainerActivation;
import com.sro.SpringCoreTask1.dtos.v1.request.trainer.UpdateTrainerProfileRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.training.TrainerTrainingFilter;
import com.sro.SpringCoreTask1.dtos.v1.request.training.TrainerTrainingResponse;
import com.sro.SpringCoreTask1.dtos.v1.response.trainer.RegisterTrainerResponse;
import com.sro.SpringCoreTask1.dtos.v1.response.trainer.TrainerProfileResponse;
import com.sro.SpringCoreTask1.dtos.v1.response.trainer.UnassignedTrainerResponse;
import com.sro.SpringCoreTask1.exception.ApiError;
import com.sro.SpringCoreTask1.service.TrainerService;
import com.sro.SpringCoreTask1.service.TrainingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/api/v1/trainers", produces = "application/json")
@Tag(name = "Trainer Management", description = "APIs for managing trainers")
public class TrainerController {

    private final TrainerService trainerService;
    private final TrainingService trainingService;

    public TrainerController(TrainerService trainerService, TrainingService trainingService) {
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    @Operation(
        summary = "Register a new trainer",
        description = "Creates a new trainer profile with the provided information"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Trainer registered successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RegisterTrainerResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiError.class)
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Trainer already exists",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiError.class)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiError.class)
            )
        )
    })
    @PostMapping
    public ResponseEntity<RegisterTrainerResponse> registerTrainer(
            @Valid @RequestBody RegisterTrainerRequest trainerRequest) {
        RegisterTrainerResponse trainerResponse = trainerService.save(trainerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(trainerResponse);
    }

    @Operation(
        summary = "Get trainer profile",
        description = "Retrieves the profile information for a trainer by username"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Profile found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TrainerProfileResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Trainer not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiError.class)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiError.class)
            )
        )
    })
    @GetMapping("/{username}")
    public ResponseEntity<TrainerProfileResponse> getProfile(
            @Parameter(description = "Username of the trainer") 
            @PathVariable String username) {
        TrainerProfileResponse profile = trainerService.findByUsername(username);
        return ResponseEntity.ok(profile);
    }

    @Operation(
        summary = "Update trainer profile",
        description = "Updates an existing trainer's profile information"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Profile updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TrainerProfileResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiError.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Trainer not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiError.class)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiError.class)
            )
        )
    })
    @PutMapping("/{username}")
    public ResponseEntity<TrainerProfileResponse> updateProfile(
            @Parameter(description = "Username of the trainer") 
            @PathVariable String username,
            @Valid @RequestBody UpdateTrainerProfileRequest trainerUpdateRequestDTO) {
        TrainerProfileResponse profile = trainerService.update(username, trainerUpdateRequestDTO);
        return ResponseEntity.ok(profile);
    }

    @Operation(
        summary = "Get unassigned trainers",
        description = "Returns a list of trainers not assigned to a specific trainee"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Unassigned trainers retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UnassignedTrainerResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid parameter",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiError.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Trainee not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiError.class)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiError.class)
            )
        )
    })
    @GetMapping("/unassigned")
    public ResponseEntity<List<UnassignedTrainerResponse>> getUnassignedTrainers(
            @Parameter(description = "Username of the trainee to find unassigned trainers for") 
            @RequestParam String traineeUsername) {
        List<UnassignedTrainerResponse> trainers = trainerService.findUnassignedTrainersByTraineeUsername(traineeUsername);
        return ResponseEntity.ok(trainers);
    }

    @Operation(
        summary = "Get trainer's training sessions",
        description = "Retrieves all training sessions for a trainer with optional filtering"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Training sessions retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TrainerTrainingResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Trainer not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiError.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid parameters",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiError.class)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiError.class)
            )
        )
    })
    @GetMapping("/{username}/trainings")
    public ResponseEntity<List<TrainerTrainingResponse>> getTrainerTrainings(
            @Parameter(description = "Username of the trainer") 
            @PathVariable String username,
            @Parameter(description = "Filter trainings from this date (ISO format)") 
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @Parameter(description = "Filter trainings until this date (ISO format)") 
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @Parameter(description = "Filter by trainee name") 
            @RequestParam(required = false) String traineeName) {
        
        TrainerTrainingFilter filterDTO = new TrainerTrainingFilter(
            username, fromDate, toDate, traineeName);
        
        List<TrainerTrainingResponse> trainings = trainingService.findTrainingsByTrainerWithFilters(filterDTO);
        return ResponseEntity.ok(trainings);
    }

    @Operation(
        summary = "Update trainer activation status",
        description = "Activates or deactivates a trainer's account"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Activation status updated successfully",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Trainer not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiError.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request payload",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiError.class)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiError.class)
            )
        )
    })
    @PatchMapping("/{username}/activation")
    public ResponseEntity<Void> updateActivationStatus(
            @Parameter(description = "Username of the trainer") 
            @PathVariable String username,
            @Parameter(description = "Activation status payload") 
            @RequestBody UpdateTrainerActivation updateTrainerActivation) {
        trainerService.updateActivationStatus(username, updateTrainerActivation.active());
        return ResponseEntity.noContent().build();
    }
}