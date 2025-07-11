package dev.sro.workload_service.exception;

/**
 * Exception thrown when authentication fails.
 * This includes invalid credentials, expired tokens, or other authentication-related errors.
 */
public class AuthenticationFailedException extends RuntimeException {
    
    public AuthenticationFailedException(String message) {
        super(message);
    }
    
    public AuthenticationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
} 