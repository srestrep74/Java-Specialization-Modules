package com.sro.SpringCoreTask1.util.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Standard API response structure for successful operations")
public record ApiStandardResponse<T>(
    @Schema(description = "Timestamp when the response was generated", example = "2021-01-01T00:00:00Z")
    LocalDateTime timestamp,

    @Schema(description = "HTTP status code", example = "200")
    int status,

    @Schema(description = "Reponse message", example = "Operation completed successfully")
    String message,

    @Schema(description = "Request path", example = "/api/v1/trainees")
    String path,

    @Schema(description = "Response data payload")
    T data
) {
    public ApiStandardResponse(int status, String message, String path, T data) {
        this(LocalDateTime.now(), status, message, path, data);
    }
}
