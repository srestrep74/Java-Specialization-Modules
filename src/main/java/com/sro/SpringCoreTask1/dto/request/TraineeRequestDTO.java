package com.sro.SpringCoreTask1.dto.request;

import java.time.LocalDate;
import java.util.List;

public record TraineeRequestDTO(
    String firstName,
    String lastName,
    String username,
    String password,
    boolean active,
    String address,
    LocalDate dateOfBirth,
    List<Long> trainerIds
){}
