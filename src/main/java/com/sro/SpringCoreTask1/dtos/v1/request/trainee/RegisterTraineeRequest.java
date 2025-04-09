package com.sro.SpringCoreTask1.dtos.v1.request.trainee;

import java.time.LocalDate;

public record RegisterTraineeRequest(
    String firstName,
    String lastName,
    LocalDate dateOfBirth,
    String address
) {}
