package com.sro.SpringCoreTask1.dtos.v1.request.trainer;

public record RegisterTrainerRequest(
    String firstName,
    String lastName,
    Long specialization
) {}
