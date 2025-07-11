package dev.sro.workload_service.presentation.exception;

import dev.sro.workload_service.domain.exception.InvalidWorkloadDataException;
import dev.sro.workload_service.domain.exception.MonthlySummaryNotFoundException;
import dev.sro.workload_service.domain.exception.TrainerNotFoundException;
import dev.sro.workload_service.domain.exception.WorkloadProcessingException;
import dev.sro.workload_service.presentation.dto.response.ApiStandardError;
import dev.sro.workload_service.presentation.util.ErrorResponseBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

/**
 * Global exception handler for the workload-service.
 * Implements comprehensive error handling using standardized error responses.
 * Follows the same pattern as gym-service for consistency across microservices.
 * All logging is handled by the LoggingAspect to maintain consistency.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // =================== DOMAIN EXCEPTIONS ===================

    @ExceptionHandler(TrainerNotFoundException.class)
    public ResponseEntity<ApiStandardError> handleTrainerNotFoundException(
            TrainerNotFoundException ex, HttpServletRequest request) {
        return ErrorResponseBuilder.notFound(ex.getMessage(), request);
    }

    @ExceptionHandler(MonthlySummaryNotFoundException.class)
    public ResponseEntity<ApiStandardError> handleMonthlySummaryNotFoundException(
            MonthlySummaryNotFoundException ex, HttpServletRequest request) {
        return ErrorResponseBuilder.notFound(ex.getMessage(), request);
    }

    @ExceptionHandler(WorkloadProcessingException.class)
    public ResponseEntity<ApiStandardError> handleWorkloadProcessingException(
            WorkloadProcessingException ex, HttpServletRequest request) {
        return ErrorResponseBuilder.internalServerError(ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidWorkloadDataException.class)
    public ResponseEntity<ApiStandardError> handleInvalidWorkloadDataException(
            InvalidWorkloadDataException ex, HttpServletRequest request) {
        return ErrorResponseBuilder.validationError(ex.getMessage(), request);
    }

    // =================== VALIDATION EXCEPTIONS ===================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiStandardError> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce("", (a, b) -> a + (a.isEmpty() ? "" : ", ") + b);

        return ErrorResponseBuilder.validationError(errorMessage, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiStandardError> handleConstraintViolationException(
            ConstraintViolationException ex, HttpServletRequest request) {
        String errorMessage = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .reduce("", (a, b) -> a + (a.isEmpty() ? "" : ", ") + b);

        return ErrorResponseBuilder.validationError(errorMessage, request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiStandardError> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String message = String.format("Parameter '%s' should be of type %s", 
                ex.getName(), ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");
        
        return ErrorResponseBuilder.validationError(message, request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiStandardError> handleIllegalArgumentException(
            IllegalArgumentException ex, HttpServletRequest request) {
        return ErrorResponseBuilder.validationError(ex.getMessage(), request);
    }

    // =================== SECURITY EXCEPTIONS ===================

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiStandardError> handleAuthenticationException(
            AuthenticationException ex, HttpServletRequest request) {
        return ErrorResponseBuilder.unauthorized("Authentication failed: " + ex.getMessage(), request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiStandardError> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {
        return ErrorResponseBuilder.forbidden("Access denied: " + ex.getMessage(), request);
    }

    // =================== GENERIC EXCEPTION ===================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiStandardError> handleGenericException(
            Exception ex, HttpServletRequest request) {
        // Re-throw security exceptions to be handled by Spring Security
        if (ex instanceof AccessDeniedException) {
            throw (AccessDeniedException) ex;
        }
        
        return ErrorResponseBuilder.internalServerError("An unexpected error occurred: " + ex.getMessage(), request);
    }
} 