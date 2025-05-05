package com.sro.SpringCoreTask1.dtos.v1.response.trainee;

public record RegisterTraineeResponse(
    String username,
    String password,
    String plainPassword
) {}
