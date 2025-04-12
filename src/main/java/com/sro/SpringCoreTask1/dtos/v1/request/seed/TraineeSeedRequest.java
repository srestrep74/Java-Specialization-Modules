package com.sro.SpringCoreTask1.dtos.v1.request.seed;

import java.time.LocalDate;
import java.util.List;

public record TraineeSeedRequest(
    String firstName,
    String lastName,
    String username,
    String password,
    boolean active,
    String address,
    LocalDate dateOfBirth,
    List<Long> trainerIds
) {}
