package dev.sro.gym_service.dtos.v1.request.auth;

import java.time.LocalDate;

public record TraineeRegistrationRequest(
    String firstName,
    String lastName,
    String address,
    LocalDate dateOfBirth
) {} 
