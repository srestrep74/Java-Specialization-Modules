package dev.sro.workload_service.exception;

/**
 * Exception thrown when there's an error processing trainer workload data.
 * This covers issues during workload calculations, data persistence, or business rule violations.
 */
public class WorkloadProcessingException extends RuntimeException {
    
    public WorkloadProcessingException(String message) {
        super(message);
    }
    
    public WorkloadProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public WorkloadProcessingException(String trainerUsername, String operation, Throwable cause) {
        super("Failed to process workload for trainer: " + trainerUsername + " during operation: " + operation, cause);
    }
} 