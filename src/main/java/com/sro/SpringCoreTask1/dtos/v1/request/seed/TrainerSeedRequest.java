package com.sro.SpringCoreTask1.dtos.v1.request.seed;

public record TrainerSeedRequest(
    String firstName,
    String lastName,
    String username,
    String password,
    boolean active,
    Long trainingTypeId
) {}
