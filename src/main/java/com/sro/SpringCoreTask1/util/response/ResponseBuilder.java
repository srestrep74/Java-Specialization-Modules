package com.sro.SpringCoreTask1.util.response;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


public class ResponseBuilder {
    
    public static <T> ResponseEntity<ApiStandardResponse<T>> success(T data) {
        return success(HttpStatus.OK, "Operation completed successfully", data);
    }

    public static <T> ResponseEntity<ApiStandardResponse<T>> success(HttpStatus status, String message, T data) {
        String path = ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString();
        ApiStandardResponse<T> response = new ApiStandardResponse<>(status.value(), message, path, data);
        return new ResponseEntity<>(response, status);
    }

    public static <T> ResponseEntity<ApiStandardResponse<T>> created(T data) {
        return success(HttpStatus.CREATED, "Resource created successfully", data);
    }

    public static <T> ResponseEntity<ApiStandardResponse<Void>> noContent() {
        String path = ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString();
        ApiStandardResponse<Void> response = new ApiStandardResponse<>(
            HttpStatus.NO_CONTENT.value(),
            "No content",
            path,
            null
        );
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

    public static <T> ResponseEntity<ApiStandardResponse<List<T>>> list(List<T> data) {
        return success(HttpStatus.OK, "List retrieved successfully", data);
    }
}
