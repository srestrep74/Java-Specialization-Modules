package dev.sro.workload_service.exception;


public class TrainerNotFoundException extends RuntimeException {
    
    public TrainerNotFoundException(String username) {
        super("Trainer not found with username: " + username);
    }
    
    public TrainerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
} 