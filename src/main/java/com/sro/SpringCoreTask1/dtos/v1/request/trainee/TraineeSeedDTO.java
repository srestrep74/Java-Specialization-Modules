package com.sro.SpringCoreTask1.dtos.v1.request.trainee;

import java.time.LocalDate;
import java.util.List;

public record TraineeSeedDTO(
    String firstName,
    String lastName,
    String username,
    String password,
    boolean active,
    String address,
    LocalDate dateOfBirth,
    List<Long> trainerIds
) {}
