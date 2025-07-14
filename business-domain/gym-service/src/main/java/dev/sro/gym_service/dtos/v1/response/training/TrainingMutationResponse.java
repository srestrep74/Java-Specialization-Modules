package dev.sro.gym_service.dtos.v1.response.training;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TrainingMutationResponse(String message, String details) {
} 