package dev.sro.workload_service.controller.v1;

import dev.sro.workload_service.dtos.v1.request.TrainerWorkloadRequest;
import dev.sro.workload_service.dtos.v1.response.TrainerMonthlySummaryResponse;
import dev.sro.workload_service.dtos.v1.response.TrainerWorkloadResponse;
import dev.sro.workload_service.service.TrainerWorkloadService;
import dev.sro.workload_service.util.response.ApiStandardResponse;
import dev.sro.workload_service.util.response.ResponseBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/workloads")
@RequiredArgsConstructor
public class TrainerWorkloadController implements TrainerWorkloadApi {
    
    private final TrainerWorkloadService trainerWorkloadService;
    
    @Override
    @PostMapping
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public ResponseEntity<ApiStandardResponse<TrainerWorkloadResponse>> processTrainerWorkload(
        @Valid @RequestBody TrainerWorkloadRequest request
    ) {
        trainerWorkloadService.processTrainerWorkload(request);
        TrainerWorkloadResponse response = TrainerWorkloadResponse.createSuccessResponse();
        return ResponseBuilder.workloadProcessed(response);
    }
    
    @Override
    @GetMapping("/trainers/{username}/monthly-summary")
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public ResponseEntity<ApiStandardResponse<TrainerMonthlySummaryResponse>> getTrainerMonthlySummary(
        @PathVariable String username
    ) {
        TrainerMonthlySummaryResponse response = trainerWorkloadService.getTrainerMonthlySummary(username);
        return ResponseBuilder.summaryRetrieved(response);
    }
    
    @Override
    @GetMapping("/trainers/{username}/monthly-summary/{year}")
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public ResponseEntity<ApiStandardResponse<TrainerMonthlySummaryResponse>> getTrainerMonthlySummaryByYear(
        @PathVariable String username,
        @PathVariable Integer year
    ) {
        TrainerMonthlySummaryResponse response = trainerWorkloadService.getTrainerMonthlySummary(username, year);
        return ResponseBuilder.summaryRetrieved(response);
    }
    
    @Override
    @GetMapping("/trainers/{username}/monthly-summary/{year}/{month}")
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