package dev.sro.workload_service.presentation.controller.v1;

import dev.sro.workload_service.application.dto.request.TrainerWorkloadRequest;
import dev.sro.workload_service.application.dto.response.TrainerMonthlySummaryResponse;
import dev.sro.workload_service.application.dto.response.TrainerWorkloadResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Trainer Workload Management", description = "Operations for managing trainer workloads and retrieving monthly summaries. All endpoints require authentication with TRAINER or ADMIN role.")
@SecurityRequirement(name = "bearerAuth")
public interface TrainerWorkloadApi {

    @Operation(
        summary = "Process trainer workload", 
        description = "Processes a trainer workload request to update monthly training summaries. " +
                     "This endpoint is typically called by the gym-service when training sessions are created, " +
                     "updated, or deleted. The request includes trainer information, training session details, " +
                     "and the action type (ADD or DELETE). Requires authentication with TRAINER or ADMIN role.",
        operationId = "processTrainerWorkload",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Workload processed successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TrainerWorkloadResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid request body or missing required fields",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TrainerWorkloadResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Authentication token missing or invalid",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TrainerWorkloadResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - User does not have required TRAINER or ADMIN role",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TrainerWorkloadResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error during workload processing",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TrainerWorkloadResponse.class)
            )
        )
    })
    ResponseEntity<TrainerWorkloadResponse> processTrainerWorkload(
        @Parameter(description = "Trainer workload request containing trainer information, training session details, and action type")
        @RequestBody TrainerWorkloadRequest request
    );

    @Operation(
        summary = "Get trainer monthly summary", 
        description = "Retrieves a complete summary of all training hours for a specific trainer, " +
                     "organized by year and month. This provides a comprehensive overview of the trainer's " +
                     "workload across all time periods. Requires authentication with TRAINER or ADMIN role.",
        operationId = "getTrainerMonthlySummary",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Monthly summary retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TrainerMonthlySummaryResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Authentication token missing or invalid",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TrainerMonthlySummaryResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - User does not have required TRAINER or ADMIN role",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TrainerMonthlySummaryResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Trainer not found or no training data available",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TrainerMonthlySummaryResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error during summary retrieval",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TrainerMonthlySummaryResponse.class)
            )
        )
    })
    ResponseEntity<TrainerMonthlySummaryResponse> getTrainerMonthlySummary(
        @Parameter(description = "Unique username identifier of the trainer", required = true, example = "john.doe") 
        @PathVariable String username
    );

    @Operation(
        summary = "Get trainer monthly summary by year", 
        description = "Retrieves training summaries for a specific trainer for a given year, " +
                     "organized by month. This allows filtering the trainer's workload data " +
                     "to focus on a specific year. Requires authentication with TRAINER or ADMIN role.",
        operationId = "getTrainerMonthlySummaryByYear",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Yearly summary retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TrainerMonthlySummaryResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid year parameter",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TrainerMonthlySummaryResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Authentication token missing or invalid",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TrainerMonthlySummaryResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - User does not have required TRAINER or ADMIN role",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TrainerMonthlySummaryResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Trainer not found or no training data available for the specified year",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TrainerMonthlySummaryResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error during summary retrieval",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TrainerMonthlySummaryResponse.class)
            )
        )
    })
    ResponseEntity<TrainerMonthlySummaryResponse> getTrainerMonthlySummaryByYear(
        @Parameter(description = "Unique username identifier of the trainer", required = true, example = "john.doe") 
        @PathVariable String username,
        @Parameter(description = "Year to retrieve the summary for (e.g., 2024)", required = true, example = "2024") 
        @PathVariable Integer year
    );

    @Operation(
        summary = "Get trainer monthly summary by specific month", 
        description = "Retrieves the training summary for a specific trainer for a given month and year. " +
                     "This provides the most granular view of a trainer's workload for a specific time period. " +
                     "Requires authentication with TRAINER or ADMIN role.",
        operationId = "getTrainerMonthlySummaryByMonth",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Monthly summary retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TrainerMonthlySummaryResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid year or month parameters (month must be 1-12)",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TrainerMonthlySummaryResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Authentication token missing or invalid",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TrainerMonthlySummaryResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - User does not have required TRAINER or ADMIN role",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TrainerMonthlySummaryResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Trainer not found or no training data available for the specified month and year",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TrainerMonthlySummaryResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error during summary retrieval",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TrainerMonthlySummaryResponse.class)
            )
        )
    })
    ResponseEntity<TrainerMonthlySummaryResponse> getTrainerMonthlySummaryByMonth(
        @Parameter(description = "Unique username identifier of the trainer", required = true, example = "john.doe") 
        @PathVariable String username,
        @Parameter(description = "Year of the summary (e.g., 2024)", required = true, example = "2024") 
        @PathVariable Integer year,
        @Parameter(description = "Month of the summary (1-12)", required = true, example = "3") 
        @PathVariable Integer month
    );
} 