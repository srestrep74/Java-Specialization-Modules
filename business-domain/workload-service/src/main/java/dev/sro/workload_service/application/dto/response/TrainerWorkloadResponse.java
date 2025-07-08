package dev.sro.workload_service.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainerWorkloadResponse {
    
    private String message;
    private boolean success;
    
    public static TrainerWorkloadResponse success() {
        return TrainerWorkloadResponse.builder()
            .message("Trainer workload processed successfully")
            .success(true)
            .build();
    }
    
    public static TrainerWorkloadResponse success(String message) {
        return TrainerWorkloadResponse.builder()
            .message(message)
            .success(true)
            .build();
    }
} 