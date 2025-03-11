package com.sro.SpringCoreTask1.entity;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Past;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "trainees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Trainee extends User {

    @Column(name = "address")
    private String address;

    @Past
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @OneToMany(mappedBy = "trainee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Training> trainings;

    @ManyToMany(mappedBy = "trainees")
    private List<Trainer> trainers;

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
