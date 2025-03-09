package com.sro.SpringCoreTask1.dto.response;


public record TrainerResponseDTO(
    Long id,
    String firstName,
    String lastName,
    String username,
    boolean active,
    TrainingTypeResponseDTO trainingType
){}
