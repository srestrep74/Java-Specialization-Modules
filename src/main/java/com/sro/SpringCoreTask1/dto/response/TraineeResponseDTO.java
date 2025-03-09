package com.sro.SpringCoreTask1.dto.response;

import java.time.LocalDate;

public record TraineeResponseDTO(
    Long id,
    String firstName,
    String lastName,
    String username,
    boolean active,
    String address,
    LocalDate dateOfBirth
) {}
