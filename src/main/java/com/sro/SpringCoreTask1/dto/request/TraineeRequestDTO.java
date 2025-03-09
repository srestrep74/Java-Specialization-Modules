package com.sro.SpringCoreTask1.dto.request;

import java.time.LocalDate;

public record TraineeRequestDTO(
    String firstName,
    String lastName,
    String username,
    String password,
    boolean active,
    String address,
    LocalDate dateOfBirth
){}
