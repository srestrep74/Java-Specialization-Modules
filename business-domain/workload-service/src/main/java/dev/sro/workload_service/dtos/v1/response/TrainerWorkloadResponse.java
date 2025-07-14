package dev.sro.workload_service.dtos.v1.response;

public record TrainerWorkloadResponse(
        String message,
        boolean success
) {
    public static TrainerWorkloadResponse createSuccessResponse() {
        return new TrainerWorkloadResponse("Trainer workload processed successfully", true);
    }
} 