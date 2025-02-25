package com.sro.SpringCoreTask1.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sro.SpringCoreTask1.models.TrainingType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TrainerDTO {
    private Long userId;
    private String firstName;
    private String lastName;
    private String userName;
    private String password;
    @JsonProperty("isActive")
    private boolean isActive;
    private String specialization;
    private TrainingType trainingType;
}
