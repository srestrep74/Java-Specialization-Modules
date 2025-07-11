package dev.sro.workload_service.presentation.controller.v1;

import dev.sro.workload_service.application.dto.request.TrainerWorkloadRequest;
import dev.sro.workload_service.application.dto.response.TrainerMonthlySummaryResponse;
import dev.sro.workload_service.application.dto.response.TrainerWorkloadResponse;
import dev.sro.workload_service.application.service.TrainerWorkloadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/trainers")
@RequiredArgsConstructor
@Slf4j
public class TrainerWorkloadController implements TrainerWorkloadApi {
    
    private final TrainerWorkloadService trainerWorkloadService;
    
    @Override
    @PostMapping("/workload")
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public ResponseEntity<TrainerWorkloadResponse> processTrainerWorkload(
        @Valid @RequestBody TrainerWorkloadRequest request
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Processing workload request for trainer: {} from user: {} with roles: {}", 
            request.trainerUsername(), authentication.getName(), authentication.getAuthorities());
        
        try {
            trainerWorkloadService.processTrainerWorkload(request);
            return ResponseEntity.ok(TrainerWorkloadResponse.createSuccessResponse());
        } catch (Exception e) {
            log.error("Error processing trainer workload: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new TrainerWorkloadResponse("Failed to process trainer workload: " + e.getMessage(), false));
        }
    }
    
    @Override
    @GetMapping("/{username}/monthly-summary")
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public ResponseEntity<TrainerMonthlySummaryResponse> getTrainerMonthlySummary(
        @PathVariable String username
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Retrieving monthly summary for trainer: {} requested by user: {} with roles: {}", 
            username, authentication.getName(), authentication.getAuthorities());
        
        try {
            TrainerMonthlySummaryResponse response = trainerWorkloadService.getTrainerMonthlySummary(username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving trainer monthly summary: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @Override
    @GetMapping("/{username}/monthly-summary/{year}")
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public ResponseEntity<TrainerMonthlySummaryResponse> getTrainerMonthlySummaryByYear(
        @PathVariable String username,
        @PathVariable Integer year
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Retrieving monthly summary for trainer: {} for year: {} requested by user: {} with roles: {}", 
            username, year, authentication.getName(), authentication.getAuthorities());
        
        try {
            TrainerMonthlySummaryResponse response = trainerWorkloadService.getTrainerMonthlySummary(username, year);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving trainer monthly summary by year: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @Override
    @GetMapping("/{username}/monthly-summary/{year}/{month}")
    @PreAuthorize("hasRole('TRAINER') or hasRole('ADMIN')")
    public ResponseEntity<TrainerMonthlySummaryResponse> getTrainerMonthlySummaryByMonth(
        @PathVariable String username,
        @PathVariable Integer year,
        @PathVariable Integer month
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Retrieving monthly summary for trainer: {} for year: {} month: {} requested by user: {} with roles: {}", 
            username, year, month, authentication.getName(), authentication.getAuthorities());
        
        try {
            TrainerMonthlySummaryResponse response = trainerWorkloadService
                .getTrainerMonthlySummary(username, year, month);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving trainer monthly summary by month: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
} 