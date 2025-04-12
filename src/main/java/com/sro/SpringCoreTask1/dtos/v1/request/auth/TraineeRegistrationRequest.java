package com.sro.SpringCoreTask1.dtos.v1.request.auth;

import java.time.LocalDate;

public record TraineeRegistrationRequest(
    String firstName,
    String lastName,
    String address,
    LocalDate dateOfBirth
) {} 
