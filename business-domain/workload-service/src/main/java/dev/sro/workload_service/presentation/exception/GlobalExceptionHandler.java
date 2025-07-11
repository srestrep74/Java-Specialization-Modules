package dev.sro.workload_service.presentation.exception;

import dev.sro.workload_service.application.dto.response.TrainerWorkloadResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the workload-service.
 * Implements comprehensive error handling and logging following best practices.
 * Integrates with the LoggingAspect for consistent request tracing.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
        MethodArgumentNotValidException ex
    ) {
        String requestId = MDC.get("requestId");
        String requestInfo = getRequestInfo();
        
        Map<String, Object> errors = new HashMap<>();
        Map<String, String> validationErrors = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });
        
        errors.put("success", false);
        errors.put("message", "Validation failed");
        errors.put("errors", validationErrors);
        errors.put("timestamp", LocalDateTime.now());
        errors.put("requestId", requestId);
        
        log.warn("Validation Exception | RequestID: {} | Request: {} | ValidationErrors: {}", 
                requestId, requestInfo, validationErrors);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<TrainerWorkloadResponse> handleIllegalArgumentException(
        IllegalArgumentException ex
    ) {
        String requestId = MDC.get("requestId");
        String requestInfo = getRequestInfo();
        
        log.error("IllegalArgument Exception | RequestID: {} | Request: {} | Error: {}", 
                requestId, requestInfo, ex.getMessage(), ex);
        
        TrainerWorkloadResponse response = new TrainerWorkloadResponse("Invalid request: " + ex.getMessage(), false);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolationException(
        ConstraintViolationException ex
    ) {
        String requestId = MDC.get("requestId");
        String requestInfo = getRequestInfo();
        
        Map<String, Object> errors = new HashMap<>();
        Map<String, String> constraintErrors = new HashMap<>();
        
        ex.getConstraintViolations().forEach(violation -> {
            constraintErrors.put(violation.getPropertyPath().toString(), violation.getMessage());
        });
        
        errors.put("success", false);
        errors.put("message", "Constraint violation");
        errors.put("errors", constraintErrors);
        errors.put("timestamp", LocalDateTime.now());
        errors.put("requestId", requestId);
        
        log.warn("Constraint Violation Exception | RequestID: {} | Request: {} | Violations: {}", 
                requestId, requestInfo, constraintErrors);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
    
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentTypeMismatchException(
        MethodArgumentTypeMismatchException ex
    ) {
        String requestId = MDC.get("requestId");
        String requestInfo = getRequestInfo();
        
        Map<String, Object> errors = new HashMap<>();
        errors.put("success", false);
        errors.put("message", "Invalid parameter type");
        errors.put("error", String.format("Parameter '%s' should be of type %s", 
                ex.getName(), ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown"));
        errors.put("timestamp", LocalDateTime.now());
        errors.put("requestId", requestId);
        
        log.warn("Method Argument Type Mismatch | RequestID: {} | Request: {} | Parameter: {} | ExpectedType: {}", 
                requestId, requestInfo, ex.getName(), 
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
    
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(
        AuthenticationException ex
    ) {
        String requestId = MDC.get("requestId");
        String requestInfo = getRequestInfo();
        
        Map<String, Object> errors = new HashMap<>();
        errors.put("success", false);
        errors.put("message", "Authentication failed");
        errors.put("error", "Invalid credentials or authentication token");
        errors.put("timestamp", LocalDateTime.now());
        errors.put("requestId", requestId);
        
        log.warn("Authentication Exception | RequestID: {} | Request: {} | Error: {}", 
                requestId, requestInfo, ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errors);
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(
        AccessDeniedException ex
    ) {
        String requestId = MDC.get("requestId");
        String requestInfo = getRequestInfo();
        
        Map<String, Object> errors = new HashMap<>();
        errors.put("success", false);
        errors.put("message", "Access denied");
        errors.put("error", "Insufficient permissions to access this resource");
        errors.put("timestamp", LocalDateTime.now());
        errors.put("requestId", requestId);
        
        log.warn("Access Denied Exception | RequestID: {} | Request: {} | Error: {}", 
                requestId, requestInfo, ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errors);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<TrainerWorkloadResponse> handleGenericException(Exception ex) {
        String requestId = MDC.get("requestId");
        String requestInfo = getRequestInfo();
        
        log.error("Unexpected Exception | RequestID: {} | Request: {} | Error: {}", 
                requestId, requestInfo, ex.getMessage(), ex);
        
        TrainerWorkloadResponse response = new TrainerWorkloadResponse("An unexpected error occurred", false);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    // =================== UTILITY METHODS ===================
    
    /**
     * Extracts request information for logging purposes
     */
    private String getRequestInfo() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            return String.format("%s %s", request.getMethod(), request.getRequestURI());
        } catch (Exception e) {
            return "Unknown request";
        }
    }
} 