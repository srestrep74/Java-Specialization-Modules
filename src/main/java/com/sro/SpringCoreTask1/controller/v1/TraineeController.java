package com.sro.SpringCoreTask1.controller.v1;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sro.SpringCoreTask1.annotations.Authenticated;
import com.sro.SpringCoreTask1.dtos.v1.request.trainee.RegisterTraineeRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.trainee.UpdateTraineeActivation;
import com.sro.SpringCoreTask1.dtos.v1.request.trainee.UpdateTraineeProfileRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.trainee.UpdateTraineeTrainerListRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.training.TraineeTrainingFilter;
import com.sro.SpringCoreTask1.dtos.v1.request.training.TraineeTrainingResponse;
import com.sro.SpringCoreTask1.dtos.v1.response.trainee.RegisterTraineeResponse;
import com.sro.SpringCoreTask1.dtos.v1.response.trainee.TraineeProfileResponse;
import com.sro.SpringCoreTask1.dtos.v1.response.trainee.TrainerSummaryResponse;
import com.sro.SpringCoreTask1.service.TraineeService;
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
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/api/v1/trainees", produces = "application/json")
@Tag(name = "Trainee Management", description = "Operations pertaining to trainees in the system")
public class TraineeController {

    private final TraineeService traineeService;
    private final TrainingService trainingService;

    public TraineeController(TraineeService traineeService, TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainingService = trainingService;
    }

    @Operation(
        summary = "Register a new trainee",
        description = "Endpoint to create a new trainee profile with basic information. "
            + "The system will automatically generate credentials.",
        operationId = "registerTrainee"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Trainee registered successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiStandardResponse.class, oneOf = RegisterTraineeResponse.class)
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
            + "personal details and assigned trainers.",
        operationId = "getTraineeProfile"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Trainee profile retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiStandardResponse.class, oneOf  = TraineeProfileResponse.class)
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
    @Authenticated(requireTrainee = true)
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
            + "All required fields must be provided.",
        operationId = "updateTraineeProfile"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Profile updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiStandardResponse.class, oneOf = TraineeProfileResponse.class)
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
            + "This action cannot be undone.",
        operationId = "deleteTraineeProfile"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Profile deleted successfully",
            content = @Content
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
    @DeleteMapping("/{username}")
    public ResponseEntity<ApiStandardResponse<Void>> deleteProfile(
            @Parameter(description = "Unique username identifier of the trainee", required = true) 
            @PathVariable String username) {
        traineeService.deleteByUsername(username);
        return ResponseBuilder.noContent();
    }

    @Operation(
        summary = "Get trainee's training sessions",
        description = "Retrieves a list of training sessions for the specified trainee with optional filtering and sorting capabilities. "
        + "Results can be filtered by date range, trainer name, and training type. "
        + "The response can be sorted by any training duration or date in ascending or descending order.",
        operationId = "getTraineeTrainings"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Training sessions retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiStandardResponse.class, oneOf = TraineeTrainingResponse.class)
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
            + "Deactivated accounts cannot access the system.",
        operationId = "updateTraineeActivation"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Activation status updated successfully",
            content = @Content
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
    @PatchMapping("/{username}/activation")
    public ResponseEntity<ApiStandardResponse<Void>> updateActivationStatus(
            @Parameter(description = "Unique username identifier of the trainee", required = true, example = "john.doe") 
            @PathVariable String username,
            @Parameter(description = "Activation status payload") 
            @RequestBody UpdateTraineeActivation updateTraineeActivation) {
        traineeService.updateActivationStatus(username, updateTraineeActivation.active());
        return ResponseBuilder.noContent();
    }

    @Operation(
        summary = "Update trainee's trainers list",
        description = "Updates the list of trainers assigned to a trainee. "
            + "This replaces the entire list of assigned trainers.",
        operationId = "updateTraineeTrainers"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Trainers list updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiStandardResponse.class, oneOf = TrainerSummaryResponse.class)
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
    @PutMapping("/{username}/trainers")
    public ResponseEntity<ApiStandardResponse<List<TrainerSummaryResponse>>> updateTrainersList(
            @Parameter(description = "Unique username identifier of the trainee", required = true, example = "john.doe") 
            @PathVariable String username,
            @Valid @RequestBody UpdateTraineeTrainerListRequest updateTrainersRequest) {
        List<TrainerSummaryResponse> updatedTrainers = traineeService.updateTraineeTrainers(username, updateTrainersRequest);
        return ResponseBuilder.list(updatedTrainers);
    }
}