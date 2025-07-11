package dev.sro.workload_service.domain.exception;

/**
 * Exception thrown when workload data is invalid or doesn't meet business requirements.
 * This includes invalid dates, negative durations, missing required fields, etc.
 */
public class InvalidWorkloadDataException extends RuntimeException {
    
    public InvalidWorkloadDataException(String message) {
        super(message);
    }
    
    public InvalidWorkloadDataException(String field, String value, String reason) {
        super("Invalid workload data for field '" + field + "' with value '" + value + "': " + reason);
    }
    
    public InvalidWorkloadDataException(String message, Throwable cause) {
        super(message, cause);
    }
} 