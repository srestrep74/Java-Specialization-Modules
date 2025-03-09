package com.sro.SpringCoreTask1.dto.request;

public record TrainerRequestDTO(
    String firstName,
    String lastName,
    String username,
    String password,
    boolean active,
    Long trainingTypeId
){}