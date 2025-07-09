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
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Trainer Workload", description = "Endpoints for managing trainer workloads and summaries")
public interface TrainerWorkloadApi {

    @Operation(summary = "Process trainer workload", description = "Receives training data to update a trainer's monthly workload summary. This is typically called by the gym-service.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Workload processed successfully",
                    content = @Content(schema = @Schema(implementation = TrainerWorkloadResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<TrainerWorkloadResponse> processTrainerWorkload(@RequestBody TrainerWorkloadRequest request);


    @Operation(summary = "Get all monthly summaries for a trainer", description = "Retrieves a complete summary of all training hours for a specific trainer, grouped by year and month.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved summary",
                    content = @Content(schema = @Schema(implementation = TrainerMonthlySummaryResponse.class))),
            @ApiResponse(responseCode = "404", description = "Trainer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<TrainerMonthlySummaryResponse> getTrainerMonthlySummary(
            @Parameter(description = "Username of the trainer", required = true) @PathVariable String username);


    @Operation(summary = "Get monthly summaries for a specific year", description = "Retrieves training summaries for a specific trainer for a given year.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved summary",
                    content = @Content(schema = @Schema(implementation = TrainerMonthlySummaryResponse.class))),
            @ApiResponse(responseCode = "404", description = "Trainer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<TrainerMonthlySummaryResponse> getTrainerMonthlySummaryByYear(
            @Parameter(description = "Username of the trainer", required = true) @PathVariable String username,
            @Parameter(description = "Year to retrieve the summary for", required = true) @PathVariable Integer year);


    @Operation(summary = "Get summary for a specific month and year", description = "Retrieves the training summary for a specific trainer for a given month and year.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved summary",
                    content = @Content(schema = @Schema(implementation = TrainerMonthlySummaryResponse.class))),
            @ApiResponse(responseCode = "404", description = "Trainer not found or no data for the specified period"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<TrainerMonthlySummaryResponse> getTrainerMonthlySummaryByMonth(
            @Parameter(description = "Username of the trainer", required = true) @PathVariable String username,
            @Parameter(description = "Year of the summary", required = true) @PathVariable Integer year,
            @Parameter(description = "Month of the summary (1-12)", required = true) @PathVariable Integer month);
} 