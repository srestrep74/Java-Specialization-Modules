package dev.sro.workload_service.domain.exception;

/**
 * Exception thrown when a trainer is not found in the system.
 * This exception indicates that the requested trainer does not exist.
 */
public class TrainerNotFoundException extends RuntimeException {
    
    public TrainerNotFoundException(String username) {
        super("Trainer not found with username: " + username);
    }
    
    public TrainerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
} 