package com.sro.SpringCoreTask1.dtos.v1.request.trainee;

import java.time.LocalDate;

public record UpdateTraineeProfileRequest(
    String username,
    String firstName,
    String lastName,
    LocalDate dateOfBirth,
    String address,
    boolean active
) {}
