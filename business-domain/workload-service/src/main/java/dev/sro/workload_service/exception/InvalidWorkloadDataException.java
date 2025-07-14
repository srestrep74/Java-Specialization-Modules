package dev.sro.workload_service.exception;

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