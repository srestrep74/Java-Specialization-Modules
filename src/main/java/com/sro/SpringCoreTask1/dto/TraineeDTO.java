package com.sro.SpringCoreTask1.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

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
public class TraineeDTO {
    private Long userId;
    private String firstName;
    private String lastName;
    private String userName;
    private String password;
    @JsonProperty("isActive")
    private boolean isActive;
    private String address;
    private LocalDate dateOfBirth;
}
