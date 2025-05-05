package com.sro.SpringCoreTask1.controller.v1;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.sro.SpringCoreTask1.dtos.v1.request.trainer.RegisterTrainerRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.trainer.UpdateTrainerActivation;
import com.sro.SpringCoreTask1.dtos.v1.request.trainer.UpdateTrainerProfileRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.training.TrainerTrainingFilter;
import com.sro.SpringCoreTask1.dtos.v1.request.training.TrainerTrainingResponse;
import com.sro.SpringCoreTask1.dtos.v1.response.trainer.RegisterTrainerResponse;
import com.sro.SpringCoreTask1.dtos.v1.response.trainer.TrainerProfileResponse;
import com.sro.SpringCoreTask1.service.TrainerService;
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

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping(value = "/api/v1/trainers", produces = {"application/json", "application/hal+json"})
@Tag(name = "Trainer Management", description = "Operations pertaining to trainers in the system")
public class TrainerController {

    private final TrainerService trainerService;
    private final TrainingService trainingService;

    public TrainerController(TrainerService trainerService, TrainingService trainingService) {
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    @Operation(
        summary = "Register a new trainer",
        description = "Creates a new trainer profile with provided information. "
            + "Specialization is required and must be a valid training type. Returns HAL+JSON response with _links containing:"
            + "\n- self: Link to the registration"
            + "\n- profile: Link to the created trainer's profile",
        operationId = "registerTrainer"
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
                schema = @Schema(implementation = ApiStandardError.class)
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Trainer already exists",
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
    public ResponseEntity<EntityModel<RegisterTrainerResponse>> registerTrainer(
            @Valid @RequestBody RegisterTrainerRequest trainerRequest) {
        RegisterTrainerResponse trainerResponse = trainerService.save(trainerRequest);

        EntityModel<RegisterTrainerResponse> entityModel = EntityModel.of(trainerResponse);
        entityModel.add(linkTo(methodOn(TrainerController.class).registerTrainer(trainerRequest)).withSelfRel());
        entityModel.add(linkTo(methodOn(TrainerController.class).getProfile(trainerResponse.username())).withRel("profile"));
        return ResponseEntity.status(HttpStatus.CREATED).body(entityModel);
    }

    @Operation(
        summary = "Get trainer profile",
        description = "Retrieves complete profile information for a trainer including "
            + "personal details, specialization, and assigned trainees. Returns HAL+JSON response with _links containing:"
            + "\n- self: Link to this profile"
            + "\n- update: Link to update the profile"
            + "\n- activation: Link to update activation status"
            + "\n- trainings: Link to trainer's training sessions",
        operationId = "getTrainerProfile"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Trainer profile retrieved successfully",
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
    @PreAuthorize("hasRole('TRAINER')")
    @GetMapping("/{username}")
    public ResponseEntity<EntityModel<TrainerProfileResponse>> getProfile(
            @Parameter(description = "Unique username identifier of the trainer", required = true, example = "john.doe") 
            @PathVariable String username) {
        TrainerProfileResponse profile = trainerService.findByUsername(username);

        EntityModel<TrainerProfileResponse> entityModel = EntityModel.of(profile);
        entityModel.add(linkTo(methodOn(TrainerController.class).getProfile(username)).withSelfRel());
        entityModel.add(linkTo(methodOn(TrainerController.class).updateProfile(username, null)).withRel("update"));
        entityModel.add(linkTo(methodOn(TrainerController.class).updateActivationStatus(username, null)).withRel("activation"));
        entityModel.add(linkTo(methodOn(TrainerController.class).getTrainerTrainings(username, null, null, null)).withRel("trainings"));
        return ResponseEntity.ok(entityModel);
    }

    @Operation(
        summary = "Update trainer profile",
        description = "Updates the profile information for an existing trainer. "
            + "Specialization cannot be changed through this endpoint. Returns HAL+JSON response with _links containing:"
            + "\n- self: Link to this update operation"
            + "\n- profile: Link to view the profile"
            + "\n- activation: Link to update activation status"
            + "\n- trainings: Link to trainer's training sessions",
        operationId = "updateTrainerProfile"
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
                schema = @Schema(implementation = ApiStandardError.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Trainer not found",
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
    @PreAuthorize("hasRole('TRAINER')")
    @PutMapping("/{username}")
    public ResponseEntity<EntityModel<TrainerProfileResponse>> updateProfile(
            @Parameter(description = "Unique username identifier of the trainer", required = true, example = "john.doe") 
            @PathVariable String username,
            @Valid @RequestBody UpdateTrainerProfileRequest updatedRequest) {
        TrainerProfileResponse profile = trainerService.update(username, updatedRequest);

        EntityModel<TrainerProfileResponse> entityModel = EntityModel.of(profile);
        entityModel.add(linkTo(methodOn(TrainerController.class).updateProfile(username, updatedRequest)).withSelfRel());
        entityModel.add(linkTo(methodOn(TrainerController.class).getProfile(username)).withRel("profile"));
        entityModel.add(linkTo(methodOn(TrainerController.class).updateActivationStatus(username, null)).withRel("activation"));
        entityModel.add(linkTo(methodOn(TrainerController.class).getTrainerTrainings(username, null, null, null)).withRel("trainings"));
        return ResponseEntity.ok(entityModel);
    }

    @Operation(
        summary = "Get trainer's training sessions",
        description = "Retrieves a list of training sessions for the specified trainer "
            + "with optional filtering by date range and trainee name. Returns HAL+JSON response with _links containing:"
            + "\n- self: Link to this resource"
            + "\n- trainer-profile: Link to the trainer's profile",
        operationId = "getTrainerTrainings"
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
                schema = @Schema(implementation = ApiStandardError.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid filters parameters",
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
    @PreAuthorize("hasRole('TRAINER')")
    @GetMapping("/{username}/trainings")
    public ResponseEntity<CollectionModel<EntityModel<TrainerTrainingResponse>>> getTrainerTrainings(
            @Parameter(description = "Unique username identifier of the trainer", required = true, example = "john.doe") 
            @PathVariable String username,
            @Parameter(description = "Start date for filtering (yyyy-MM-dd)", example = "2023-01-01") 
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @Parameter(description = "End date for filtering (yyyy-MM-dd)", example = "2023-12-31") 
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @Parameter(description = "Filter by trainee username", example = "trainee.doe") 
            @RequestParam(required = false) String traineeName) {
        
        TrainerTrainingFilter filterDTO = new TrainerTrainingFilter(
            username, fromDate, toDate, traineeName);
        
        List<TrainerTrainingResponse> trainings = trainingService.findTrainingsByTrainerWithFilters(filterDTO);

        List<EntityModel<TrainerTrainingResponse>> trainingModels = trainings.stream()
            .map(training -> EntityModel.of(training, 
                linkTo(methodOn(TrainerController.class).getTrainerTrainings(
                    username, fromDate, toDate, traineeName)).withSelfRel()))
            .collect(Collectors.toList());
        
        CollectionModel<EntityModel<TrainerTrainingResponse>> collectionModel = CollectionModel.of(
            trainingModels,
            linkTo(methodOn(TrainerController.class).getTrainerTrainings(
                username, fromDate, toDate, traineeName)).withSelfRel(),
            linkTo(methodOn(TrainerController.class).getProfile(username)).withRel("trainer-profile")
        );
        
        return ResponseEntity.ok(collectionModel);
    }

    @Operation(
        summary = "Update trainer activation status",
        description = "Activates or deactivates a trainer account. "
            + "Deactivated accounts cannot access the system. Returns no content with Location header containing:"
            + "\n- Location: URI to view the trainer's profile",
        operationId = "updateTrainerActivation"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Activation status updated successfully",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Trainer not found",
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
    @PreAuthorize("hasRole('TRAINER')")
    @PatchMapping("/{username}/activation")
    public ResponseEntity<Void> updateActivationStatus(
            @Parameter(description = "Unique username identifier of the trainer", required = true, example = "john.doe") 
            @PathVariable String username,
            @Parameter(description = "Activation status payload") 
            @RequestBody UpdateTrainerActivation updateTrainerActivation) {
        trainerService.updateActivationStatus(username, updateTrainerActivation.active());
        Link profileLink = linkTo(methodOn(TrainerController.class).getProfile(username)).withRel("profile");
        return ResponseEntity.ok().header(HttpHeaders.LOCATION, profileLink.toUri().toString()).build();
    }
}