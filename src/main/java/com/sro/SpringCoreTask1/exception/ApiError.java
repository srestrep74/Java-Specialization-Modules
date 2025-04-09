package com.sro.SpringCoreTask1.exception;

import java.time.LocalDateTime;

public record ApiError(
    LocalDateTime timestamp,
    int status,
    String error,
    String message,
    String path
) {
    public ApiError(int status, String error, String message, String path) {
        this(LocalDateTime.now(), status, error, message, path);
    }
}
