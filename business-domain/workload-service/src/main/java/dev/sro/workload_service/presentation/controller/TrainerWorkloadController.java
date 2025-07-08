package dev.sro.workload_service.presentation.controller;

import dev.sro.workload_service.application.dto.request.TrainerWorkloadRequest;
import dev.sro.workload_service.application.dto.response.TrainerMonthlySummaryResponse;
import dev.sro.workload_service.application.dto.response.TrainerWorkloadResponse;
import dev.sro.workload_service.application.service.TrainerWorkloadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/trainers")
@RequiredArgsConstructor
@Slf4j
public class TrainerWorkloadController {
    
    private final TrainerWorkloadService trainerWorkloadService;
    
    @PostMapping("/workload")
    public ResponseEntity<TrainerWorkloadResponse> processTrainerWorkload(
        @Valid @RequestBody TrainerWorkloadRequest request
    ) {
        log.info("Received workload request for trainer: {}", request.getTrainerUsername());
        
        try {
            trainerWorkloadService.processTrainerWorkload(request);
            return ResponseEntity.ok(TrainerWorkloadResponse.success());
        } catch (Exception e) {
            log.error("Error processing trainer workload: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(TrainerWorkloadResponse.builder()
                    .message("Failed to process trainer workload: " + e.getMessage())
                    .success(false)
                    .build());
        }
    }
    
    @GetMapping("/{username}/monthly-summary")
    public ResponseEntity<TrainerMonthlySummaryResponse> getTrainerMonthlySummary(
        @PathVariable String username
    ) {
        log.info("Received request for trainer monthly summary: {}", username);
        
        try {
            TrainerMonthlySummaryResponse response = trainerWorkloadService.getTrainerMonthlySummary(username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving trainer monthly summary: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{username}/monthly-summary/{year}")
    public ResponseEntity<TrainerMonthlySummaryResponse> getTrainerMonthlySummaryByYear(
        @PathVariable String username,
        @PathVariable Integer year
    ) {
        log.info("Received request for trainer monthly summary: {} for year: {}", username, year);
        
        try {
            TrainerMonthlySummaryResponse response = trainerWorkloadService.getTrainerMonthlySummary(username, year);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving trainer monthly summary by year: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{username}/monthly-summary/{year}/{month}")
    public ResponseEntity<TrainerMonthlySummaryResponse> getTrainerMonthlySummaryByMonth(
        @PathVariable String username,
        @PathVariable Integer year,
        @PathVariable Integer month
    ) {
        log.info("Received request for trainer monthly summary: {} for year: {} month: {}", 
            username, year, month);
        
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