package dev.sro.workload_service.presentation.controller.v1;

import dev.sro.workload_service.application.service.TrainerWorkloadService;
import dev.sro.workload_service.presentation.dto.request.TrainerWorkloadRequest;
import dev.sro.workload_service.presentation.dto.response.TrainerMonthlySummaryResponse;
import dev.sro.workload_service.presentation.dto.response.TrainerWorkloadResponse;
import dev.sro.workload_service.presentation.response.ApiStandardResponse;
import dev.sro.workload_service.presentation.response.ResponseBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/trainers")
@RequiredArgsConstructor
public class TrainerWorkloadController implements TrainerWorkloadApi {
    
    private final TrainerWorkloadService trainerWorkloadService;
    
    @Override
    @PostMapping("/workload")
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public ResponseEntity<ApiStandardResponse<TrainerWorkloadResponse>> processTrainerWorkload(
        @Valid @RequestBody TrainerWorkloadRequest request
    ) {
        trainerWorkloadService.processTrainerWorkload(request);
        TrainerWorkloadResponse response = TrainerWorkloadResponse.createSuccessResponse();
        return ResponseBuilder.workloadProcessed(response);
    }
    
    @Override
    @GetMapping("/{username}/monthly-summary")
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public ResponseEntity<ApiStandardResponse<TrainerMonthlySummaryResponse>> getTrainerMonthlySummary(
        @PathVariable String username
    ) {
        TrainerMonthlySummaryResponse response = trainerWorkloadService.getTrainerMonthlySummary(username);
        return ResponseBuilder.summaryRetrieved(response);
    }
    
    @Override
    @GetMapping("/{username}/monthly-summary/{year}")
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public ResponseEntity<ApiStandardResponse<TrainerMonthlySummaryResponse>> getTrainerMonthlySummaryByYear(
        @PathVariable String username,
        @PathVariable Integer year
    ) {
        TrainerMonthlySummaryResponse response = trainerWorkloadService.getTrainerMonthlySummary(username, year);
        return ResponseBuilder.summaryRetrieved(response);
    }
    
    @Override
    @GetMapping("/{username}/monthly-summary/{year}/{month}")
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public ResponseEntity<ApiStandardResponse<TrainerMonthlySummaryResponse>> getTrainerMonthlySummaryByMonth(
        @PathVariable String username,
        @PathVariable Integer year,
        @PathVariable Integer month
    ) {
        TrainerMonthlySummaryResponse response = trainerWorkloadService
            .getTrainerMonthlySummary(username, year, month);
        return ResponseBuilder.summaryRetrieved(response);
    }
} 