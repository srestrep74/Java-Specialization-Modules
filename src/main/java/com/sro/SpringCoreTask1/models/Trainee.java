package com.sro.SpringCoreTask1.models;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

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
    private Training training;

    @JsonCreator
    public Trainee(
        @JsonProperty("firstName") String firstName,
        @JsonProperty("lastName") String lastName,
        @JsonProperty("userName") String userName,
        @JsonProperty("password") String password,
        @JsonProperty("isActive") boolean isActive,
        @JsonProperty("userId") Long userId,
        @JsonProperty("address") String address,
        @JsonProperty("dateOfBirth") LocalDate dateOfBirth,
        @JsonProperty("training") Training training
    ) {
        super(firstName, lastName, userName, password, isActive);
        this.userId = userId;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.training = training;
    }

}
