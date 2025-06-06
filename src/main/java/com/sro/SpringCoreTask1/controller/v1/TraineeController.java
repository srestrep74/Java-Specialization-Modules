package com.sro.SpringCoreTask1.controller.v1;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.sro.SpringCoreTask1.dtos.v1.request.trainee.RegisterTraineeRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.trainee.UpdateTraineeActivation;
import com.sro.SpringCoreTask1.dtos.v1.request.trainee.UpdateTraineeProfileRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.trainee.UpdateTraineeTrainerListRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.training.TraineeTrainingFilter;
import com.sro.SpringCoreTask1.dtos.v1.request.training.TraineeTrainingResponse;
import com.sro.SpringCoreTask1.dtos.v1.response.trainee.RegisterTraineeResponse;
import com.sro.SpringCoreTask1.dtos.v1.response.trainee.TraineeProfileResponse;
import com.sro.SpringCoreTask1.dtos.v1.response.trainee.TrainerSummaryResponse;
import com.sro.SpringCoreTask1.dtos.v1.response.trainer.UnassignedTrainerResponse;
import com.sro.SpringCoreTask1.service.TraineeService;
import com.sro.SpringCoreTask1.service.TrainerService;
import com.sro.SpringCoreTask1.service.TrainingService;
import com.sro.SpringCoreTask1.util.response.ApiStandardError;
import com.sro.SpringCoreTask1.util.response.ApiStandardResponse;
import com.sro.SpringCoreTask1.util.response.ResponseBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping(value = "/api/v1/trainees", produces = "application/json")
@Tag(name = "Trainee Management", description = "Operations pertaining to trainees in the system")
public class TraineeController {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    public TraineeController(TraineeService traineeService, TrainerService trainerService, TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    @Operation(
        summary = "Register a new trainee",
        description = "Endpoint to create a new trainee profile with basic information. "
            + "The system will automatically generate credentials. "
            + "This endpoint does not require authentication.",
        operationId = "registerTrainee",
        security = { } 
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Trainee registered successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiStandardResponse.class)
            )
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
            responseCode = "409",
            description = "Trainee username already exists",
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
    @PostMapping
    public ResponseEntity<ApiStandardResponse<RegisterTraineeResponse>> registerTrainee(
            @Valid @RequestBody RegisterTraineeRequest traineeRequest) {
        RegisterTraineeResponse traineeResponse = traineeService.save(traineeRequest);
        return ResponseBuilder.created(traineeResponse);
    }

    @Operation(
        summary = "Get trainee profile",
        description = "Retrieves complete profile information for a trainee including "
            + "personal details and assigned trainers. "
            + "Requires authentication with TRAINEE or ADMIN role. A trainee can only access their own profile.",
        operationId = "getTraineeProfile",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Trainee profile retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiStandardResponse.class)
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
            description = "Forbidden - User does not have required TRAINEE or ADMIN role",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiStandardError.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Trainee not found",
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
    @PreAuthorize("hasRole('TRAINEE') or hasRole('ADMIN')")
    @GetMapping("/{username}")
    public ResponseEntity<ApiStandardResponse<TraineeProfileResponse>> getProfile(
            @Parameter(description = "Unique username identifier of the trainee", required = true, example = "john.doe") 
            @PathVariable String username) {
        TraineeProfileResponse profile = traineeService.findByUsername(username);
        return ResponseBuilder.success(profile);
    }

    @Operation(
        summary = "Update trainee profile",
        description = "Updates the profile information for an existing trainee. "
            + "All required fields must be provided. "
            + "Requires authentication with TRAINEE or ADMIN role. A trainee can only update their own profile.",
        operationId = "updateTraineeProfile",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Profile updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiStandardResponse.class)
            )
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
            description = "Forbidden - User does not have required TRAINEE or ADMIN role",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiStandardError.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Trainee not found",
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
    @PreAuthorize("hasRole('TRAINEE') or hasRole('ADMIN')")
    @PutMapping("/{username}")
    public ResponseEntity<ApiStandardResponse<TraineeProfileResponse>> updateProfile(
            @Parameter(description = "Unique username identifier of the trainee", required = true, example = "john.doe") 
            @PathVariable String username,
            @Valid @RequestBody UpdateTraineeProfileRequest updatedRequest) {
        TraineeProfileResponse profile = traineeService.update(username, updatedRequest);
        return ResponseBuilder.success(profile);
    }

    @Operation(
        summary = "Delete trainee profile",
        description = "Permanently removes a trainee profile from the system. "
            + "This action cannot be undone. "
            + "Requires authentication with ADMIN role. Only administrators can delete trainee profiles.",
        operationId = "deleteTraineeProfile",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Profile deleted successfully",
            content = @Content
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
            description = "Forbidden - User does not have required ADMIN role",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiStandardError.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Trainee not found",
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
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{username}")
    public ResponseEntity<ApiStandardResponse<Void>> deleteProfile(
            @Parameter(description = "Unique username identifier of the trainee", required = true) 
            @PathVariable String username) {
        traineeService.deleteByUsername(username);
        return ResponseBuilder.success(HttpStatus.OK, "Profile deleted successfully", null);
    }

    @Operation(
        summary = "Get trainee's training sessions",
        description = "Retrieves a list of training sessions for the specified trainee with optional filtering and sorting capabilities. "
        + "Results can be filtered by date range, trainer name, and training type. "
        + "The response can be sorted by any training duration or date in ascending or descending order. "
        + "Requires authentication with TRAINEE or ADMIN role. A trainee can only view their own training sessions.",
        operationId = "getTraineeTrainings",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Training sessions retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiStandardResponse.class)
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
            description = "Forbidden - User does not have required TRAINEE or ADMIN role",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiStandardError.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Trainee not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiStandardError.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid parameters provided. Possible issues: "
                + "invalid date format, invalid sort field, or invalid sort direction",
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
    @PreAuthorize("hasRole('TRAINEE') or hasRole('ADMIN')")
    @GetMapping("/{username}/trainings")
    public ResponseEntity<ApiStandardResponse<List<TraineeTrainingResponse>>> getTraineeTrainings(
            @Parameter(description = "Unique username identifier of the trainee", required = true, example = "john.doe") 
            @PathVariable String username,
            @Parameter(description = "Start date for filtering (yyyy-MM-dd)", example = "2023-01-01") 
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @Parameter(description = "End date for filtering (yyyy-MM-dd)", example = "2023-12-31") 
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @Parameter(description = "Filter by trainer username", example = "john.doe") 
            @RequestParam(required = false) String trainerName,
            @Parameter(description = "Filter by training type", example = "Yoga") 
            @RequestParam(required = false) String trainingType,
            @Parameter( 
                description = "Field to sort by. Available fields: "
                    + "trainingDate, duration. Default: trainingDate",
                example = "trainingDate",
                schema = @Schema(
                    allowableValues = {"trainingDate", "duration"},
                    defaultValue = "trainingDate"
                )
            ) 
            @RequestParam(required = false, defaultValue = "trainingDate") String sortField,
            @Parameter(
                description = "Sort direction. Default: DESC (newest first)",
                example = "DESC",
                schema = @Schema(
                    allowableValues = {"ASC", "DESC"},
                    defaultValue = "DESC"
                )
            ) 
            @RequestParam(required = false, defaultValue = "DESC") String sortDirection) {
        
        TraineeTrainingFilter filterDTO = new TraineeTrainingFilter(
            username, fromDate, toDate, trainerName, trainingType);
        
        List<TraineeTrainingResponse> trainings = trainingService.findTrainingsByTraineeWithFilters(filterDTO, sortField, sortDirection);
        return ResponseBuilder.list(trainings);
    }

    @Operation(
        summary = "Update trainee activation status",
        description = "Activates or deactivates a trainee account. "
            + "Deactivated accounts cannot access the system. "
            + "Requires authentication with TRAINEE or ADMIN role. A trainee can only update their own activation status.",
        operationId = "updateTraineeActivation",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Activation status updated successfully",
            content = @Content
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
            description = "Forbidden - User does not have required TRAINEE or ADMIN role",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiStandardError.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Trainee not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiStandardError.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request payload",
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
    @PreAuthorize("hasRole('TRAINEE') or hasRole('ADMIN')")
    @PatchMapping("/{username}/activation")
    public ResponseEntity<ApiStandardResponse<Void>> updateActivationStatus(
            @Parameter(description = "Unique username identifier of the trainee", required = true, example = "john.doe") 
            @PathVariable String username,
            @Parameter(description = "Activation status payload") 
            @RequestBody UpdateTraineeActivation updateTraineeActivation) {
        traineeService.updateActivationStatus(username, updateTraineeActivation.active());
        return ResponseBuilder.success(HttpStatus.OK, "Activation status updated successfully", null);
    }

    @Operation(
        summary = "Update trainee's trainers list",
        description = "Updates the list of trainers assigned to a trainee. "
            + "This replaces the entire list of assigned trainers. "
            + "Requires authentication with TRAINEE or ADMIN role. A trainee can only update their own trainers list.",
        operationId = "updateTraineeTrainers",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Trainers list updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiStandardResponse.class)
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
            description = "Forbidden - User does not have required TRAINEE or ADMIN role",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiStandardError.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Trainee or Trainer not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiStandardError.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request payload or trainer is not active",
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
    @PreAuthorize("hasRole('TRAINEE') or hasRole('ADMIN')")
    @PutMapping("/{username}/trainers")
    public ResponseEntity<ApiStandardResponse<List<TrainerSummaryResponse>>> updateTrainersList(
            @Parameter(description = "Unique username identifier of the trainee", required = true, example = "john.doe") 
            @PathVariable String username,
            @Valid @RequestBody UpdateTraineeTrainerListRequest updateTrainersRequest) {
        List<TrainerSummaryResponse> updatedTrainers = traineeService.updateTraineeTrainers(username, updateTrainersRequest);
        return ResponseBuilder.list(updatedTrainers);
    }

    @Operation(
        summary = "Get unassigned trainers",
        description = "Retrieves a list of active trainers not currently assigned "
            + "to the specified trainee. Returns HAL+JSON response with _links containing:"
            + "\n- self: Link to this resource"
            + "\n- profile: Link to each trainer's profile"
            + "\n- trainings: Link to each trainer's training sessions. "
            + "Requires authentication with TRAINEE or ADMIN role. A trainee can only view unassigned trainers for their own account.",
        operationId = "getUnassignedTrainers",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Unassigned trainers retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiStandardResponse.class)
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
            description = "Forbidden - User does not have required TRAINEE or ADMIN role",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiStandardError.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid parameter",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiStandardError.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Trainee not found",
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
    @PreAuthorize("hasRole('TRAINEE') or hasRole('ADMIN')")
    @GetMapping("/{username}/unassigned-trainers")
    public ResponseEntity<ApiStandardResponse<List<UnassignedTrainerResponse>>> getUnassignedTrainers(
            @Parameter(description = "Unique username identifier of the trainee", required = true, example = "john.doe") 
            @PathVariable String username) {
        List<UnassignedTrainerResponse> trainers = trainerService.findUnassignedTrainersByTraineeUsername(username);
        return ResponseBuilder.list(trainers);
    }
}