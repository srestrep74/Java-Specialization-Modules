package com.sro.SpringCoreTask1.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Past;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "trainees")
@EqualsAndHashCode(callSuper = true)
public class Trainee extends User {

    @Column(name = "address")
    private String address;

    @Past
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @OneToMany(mappedBy = "trainee", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Training> trainings = new ArrayList<>();

    @ManyToMany(mappedBy = "trainees")
    private List<Trainer> trainers = new ArrayList<>();

    @Override
    public String toString() {
        return "Trainer{" +
                "id=" + this.getId() +
                ", firstName='" + this.getFirstName() + '\'' +
                ", lastName='" + this.getLastName() + '\'' +
                ", username='" + this.getUsername() + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", address='" + address + '\'' +
                '}';
    }

}
