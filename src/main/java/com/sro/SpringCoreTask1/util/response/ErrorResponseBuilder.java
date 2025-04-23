package com.sro.SpringCoreTask1.util.response;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ErrorResponseBuilder {

    public static ResponseEntity<ApiStandardError> buildErrorResponse(
            HttpStatus status, String errorType, String message, HttpServletRequest request) {
        return buildErrorResponse(status, errorType, message, request.getRequestURI());
    }

    public static ResponseEntity<ApiStandardError> buildErrorResponse(
            HttpStatus status, String errorType, String message, String path) {
        ApiStandardError apiError = new ApiStandardError(
                status.value(),
                errorType,
                message,
                path);
        return new ResponseEntity<>(apiError, status);
    }

    public static ResponseEntity<ApiStandardError> notFound(String message, HttpServletRequest request) {
        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                "Resource Not Found",
                message,
                request.getRequestURI());
    }

    public static ResponseEntity<ApiStandardError> conflict(String message, HttpServletRequest request) {
        return buildErrorResponse(
                HttpStatus.CONFLICT,
                "Resource Conflict",
                message,
                request.getRequestURI());
    }

    public static ResponseEntity<ApiStandardError> validationError(String message, HttpServletRequest request) {
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Validation Error",
                message,
                request.getRequestURI());
    }

    public static ResponseEntity<ApiStandardError> unauthorized(String message, HttpServletRequest request) {
        return buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "Unauthorized",
                message,
                request.getRequestURI());
    }

    public static ResponseEntity<ApiStandardError> internalServerError(String message, HttpServletRequest request) {
        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                message,
                request.getRequestURI());
    }
}