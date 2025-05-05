package com.sro.SpringCoreTask1.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.sro.SpringCoreTask1.util.response.ApiStandardError;
import com.sro.SpringCoreTask1.util.response.ErrorResponseBuilder;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiStandardError> handleResourceNotFoundException(
            ResourceNotFoundException ex, HttpServletRequest request) {
        return ErrorResponseBuilder.notFound(ex.getMessage(), request);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ApiStandardError> handleResourceAlreadyExistsException(
            ResourceAlreadyExistsException ex, HttpServletRequest request) {
        return ErrorResponseBuilder.conflict(ex.getMessage(), request);
    }

    @ExceptionHandler(DatabaseOperationException.class)
    public ResponseEntity<ApiStandardError> handleDatabaseOperationException(
            DatabaseOperationException ex, HttpServletRequest request) {
        return ErrorResponseBuilder.internalServerError(ex.getMessage(), request);
    }

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

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiStandardError> handleIllegalArgumentException(
            IllegalArgumentException ex, HttpServletRequest request) {
        return ErrorResponseBuilder.validationError(ex.getMessage(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiStandardError> handleGenericException(
            Exception ex, HttpServletRequest request) {
        if (ex instanceof AccessDeniedException) {
            throw (AccessDeniedException) ex;
        }
        return ErrorResponseBuilder.internalServerError(ex.getMessage(), request);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiStandardError> handleUnauthorizedException(
            UnauthorizedException ex, HttpServletRequest request) {
        return ErrorResponseBuilder.unauthorized(ex.getMessage(), request);
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ApiStandardError> handleAuthenticationFailedException(
            AuthenticationFailedException ex, HttpServletRequest request) {
        HttpStatus status = ex.getMessage().contains("Account locked") ? 
            HttpStatus.TOO_MANY_REQUESTS : HttpStatus.UNAUTHORIZED;
        
        return ErrorResponseBuilder.buildErrorResponse(status, "Authentication Failed", ex.getMessage(), request.getRequestURI());
    }
}