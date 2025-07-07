package dev.sro.gym_service.dtos.v1.request.seed;

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
    List<Long> trainerIds,
    String role
) {}
