package com.sro.SpringCoreTask1.dtos.v1.response.trainee;

public record TrainerSummaryResponseDTO(
    String username,
    String firstName,
    String lastName,
    Long specialization
) {}
