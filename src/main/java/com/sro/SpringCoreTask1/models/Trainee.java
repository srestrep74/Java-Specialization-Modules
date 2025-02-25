package com.sro.SpringCoreTask1.models;

import java.time.LocalDate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class Trainee extends User{
    
    private Long userId;
    private String address;
    private LocalDate dateOfBirth;

    public Trainee(
        String firstName,
        String lastName,
        String userName,
        String password,
        boolean isActive,
        Long userId,
        String address,
        LocalDate dateOfBirth
    ) {
        super(firstName, lastName, userName, password, isActive);
        this.userId = userId;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
    }

}
