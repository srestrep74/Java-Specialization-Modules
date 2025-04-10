package com.sro.SpringCoreTask1.controller.v1;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sro.SpringCoreTask1.dtos.v1.response.trainingType.TrainingTypeResponse;
import com.sro.SpringCoreTask1.exception.ApiError;
import com.sro.SpringCoreTask1.service.TrainingTypeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "/api/v1/training-types", produces = "application/json")
@Tag(name = "Training Type Management", description = "Operations pertaining to training types in the system")
public class TrainingTypeController {
    
    private final TrainingTypeService trainingTypeService;

    public TrainingTypeController(TrainingTypeService trainingTypeService) {
        this.trainingTypeService = trainingTypeService;
    }
    
    @Operation(
        summary = "Get all training types",
        description = "Retrieves a list of all available training types in the system",
        operationId = "getAllTrainingTypes"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Training types retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = TrainingTypeResponse.class))
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
    @GetMapping
    public ResponseEntity<List<TrainingTypeResponse>> findAll() {
        return ResponseEntity.ok(trainingTypeService.findAll());
    }
}