package com.sro.SpringCoreTask1.models;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class Trainer extends User{

    private Long userId;
    private String specialization;
    private TrainingType trainingType;

    @JsonCreator
    public Trainer(
        @JsonProperty("firstName") String firstName,
        @JsonProperty("lastName") String lastName,
        @JsonProperty("userName") String userName,
        @JsonProperty("password") String password,
        @JsonProperty("isActive") boolean isActive,
        @JsonProperty("userId") Long userId,
        @JsonProperty("specialization") String specialization,
        @JsonProperty("trainingType") TrainingType trainingType
    ) {
        super(firstName, lastName, userName, password, isActive);
        this.userId = userId;
        this.specialization = specialization;
        this.trainingType = trainingType;
    }
    
}
