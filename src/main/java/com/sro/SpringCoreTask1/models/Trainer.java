package com.sro.SpringCoreTask1.models;


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

    public Trainer(
        String firstName,
        String lastName,
        String userName,
        String password,
        boolean isActive,
        Long userId,
        String specialization,
        TrainingType trainingType
    ) {
        super(firstName, lastName, userName, password, isActive);
        this.userId = userId;
        this.specialization = specialization;
        this.trainingType = trainingType;
    }
    
}
