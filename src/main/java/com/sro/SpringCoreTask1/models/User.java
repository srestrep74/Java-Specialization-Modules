package com.sro.SpringCoreTask1.models;


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

    public User(
        String firstName,
        String lastName,
        String userName,
        String password,
        boolean isActive
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.password = password;
        this.isActive = isActive;
    }
    
}
