package com.sro.SpringCoreTask1.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class User {

    private String firstName;
    private String lastName;
    private String userName;
    private String password;
    private boolean isActive;

    @JsonCreator
    public User(
        @JsonProperty("firstName") String firstName,
        @JsonProperty("lastName") String lastName,
        @JsonProperty("userName") String userName,
        @JsonProperty("password") String password,
        @JsonProperty("isActive") boolean isActive
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.password = password;
        this.isActive = isActive;
    }
    
}
