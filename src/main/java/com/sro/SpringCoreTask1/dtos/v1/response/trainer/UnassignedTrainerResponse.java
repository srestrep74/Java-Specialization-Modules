package com.sro.SpringCoreTask1.dtos.v1.response.trainer;

public record UnassignedTrainerResponse(
    String username,
    String firstName,
    String lastName,
    Long specialization
) {}
