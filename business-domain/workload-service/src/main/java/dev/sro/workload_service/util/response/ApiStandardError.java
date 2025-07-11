package dev.sro.workload_service.util.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Standard error response structure for workload service")
public record ApiStandardError(
        @Schema(description = "Timestamp when the error occurred", example = "2023-01-01T12:00:00") 
        LocalDateTime timestamp,

        @Schema(description = "HTTP status code", example = "404") 
        int status,

        @Schema(description = "Error type", example = "Resource Not Found") 
        String error,

        @Schema(description = "Detailed error message", example = "Trainer not found with username: john.doe") 
        String message,

        @Schema(description = "Request path that generated the error", example = "/api/v1/trainers/workload") 
        String path) {
    
    public ApiStandardError(int status, String error, String message, String path) {
        this(LocalDateTime.now(), status, error, message, path);
    }
} 