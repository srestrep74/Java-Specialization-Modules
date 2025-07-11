package dev.sro.workload_service.presentation.dto.response;

public record TrainerWorkloadResponse(
        String message,
        boolean success
) {
    public static TrainerWorkloadResponse createSuccessResponse() {
        return new TrainerWorkloadResponse("Trainer workload processed successfully", true);
    }
} 