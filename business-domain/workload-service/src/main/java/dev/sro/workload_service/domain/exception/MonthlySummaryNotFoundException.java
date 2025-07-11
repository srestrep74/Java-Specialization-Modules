package dev.sro.workload_service.domain.exception;

/**
 * Exception thrown when a monthly summary is not found for a specific trainer and period.
 * This indicates that no workload data exists for the requested time frame.
 */
public class MonthlySummaryNotFoundException extends RuntimeException {
    
    public MonthlySummaryNotFoundException(String trainerUsername) {
        super("No monthly summary found for trainer: " + trainerUsername);
    }
    
    public MonthlySummaryNotFoundException(String trainerUsername, Integer year) {
        super("No monthly summary found for trainer: " + trainerUsername + " in year: " + year);
    }
    
    public MonthlySummaryNotFoundException(String trainerUsername, Integer year, Integer month) {
        super("No monthly summary found for trainer: " + trainerUsername + " in " + year + "/" + month);
    }
    
    public MonthlySummaryNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
} 