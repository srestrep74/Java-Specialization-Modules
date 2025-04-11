package com.sro.SpringCoreTask1.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFoundException(
            ResourceNotFoundException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Resource Not Found", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleResourceAlreadyExistsException(
            ResourceAlreadyExistsException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.CONFLICT, "Resource Already Exists", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(DatabaseOperationException.class)
    public ResponseEntity<ApiError> handleDatabaseOperationException(
            DatabaseOperationException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Database Operation Error", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce("", (a, b) -> a + (a.isEmpty() ? "" : ", ") + b);

        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation Error", errorMessage, request.getRequestURI());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolationException(
            ConstraintViolationException ex, HttpServletRequest request) {
        String errorMessage = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .reduce("", (a, b) -> a + (a.isEmpty() ? "" : ", ") + b);

        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation Error", errorMessage, request.getRequestURI());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(
            IllegalArgumentException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid Request", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(
            Exception ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage(), request.getRequestURI());
    }

    private ResponseEntity<ApiError> buildErrorResponse(
            HttpStatus status, String error, String message, String path) {
        ApiError apiError = new ApiError(
                status.value(),
                error,
                message,
                path
        );
        return new ResponseEntity<>(apiError, status);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiError> handleUnauthorizedException(
            UnauthorizedException ex, HttpServletRequest request) {
        return buildErrorResponse(
            HttpStatus.UNAUTHORIZED, 
            "Unauthorized", 
            ex.getMessage(), 
            request.getRequestURI()
        );
    }
}