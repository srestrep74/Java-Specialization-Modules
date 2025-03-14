package com.sro.SpringCoreTask1.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "trainers")
@EqualsAndHashCode(callSuper = true)
public class Trainer extends User{

    @ManyToOne
    @JoinColumn(name = "training_type_id")
    private TrainingType trainingType;

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Training> trainings = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "trainers_trainees",
        joinColumns = @JoinColumn(name = "trainer_id"),
        inverseJoinColumns = @JoinColumn(name = "trainee_id")
    )
    private List<Trainee> trainees = new ArrayList<>();

    @Override
    public String toString() {
        return "Trainer{" +
                "id=" + this.getId() +
                ", firstName='" + this.getFirstName() + '\'' +
                ", lastName='" + this.getLastName() + '\'' +
                ", username='" + this.getUsername() + '\'' +
                ", trainingType=" + trainingType +
                '}';
    }

}
