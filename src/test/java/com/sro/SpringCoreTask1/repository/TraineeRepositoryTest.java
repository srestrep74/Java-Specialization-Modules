package com.sro.SpringCoreTask1.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.sro.SpringCoreTask1.entity.Trainee;
import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.entity.TrainingType;

@DataJpaTest
@ActiveProfiles("test")
class TraineeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TraineeRepository traineeRepository;

    private Trainee trainee1;
    private Trainee trainee2;
    private Trainer trainer1;
    private Trainer trainer2;
    private TrainingType trainingType;

    @BeforeEach
    void setUp() {
        // Crear y persistir un tipo de entrenamiento
        trainingType = new TrainingType();
        trainingType.setTrainingTypeName("Fitness");
        entityManager.persist(trainingType);

        // Crear entrenadores
        trainer1 = new Trainer();
        trainer1.setFirstName("John");
        trainer1.setLastName("Doe");
        trainer1.setUsername("johndoe");
        trainer1.setPassword("password123");
        trainer1.setActive(true);
        trainer1.setTrainingType(trainingType);
        entityManager.persist(trainer1);

        trainer2 = new Trainer();
        trainer2.setFirstName("Jane");
        trainer2.setLastName("Smith");
        trainer2.setUsername("janesmith");
        trainer2.setPassword("password456");
        trainer2.setActive(true);
        trainer2.setTrainingType(trainingType);
        entityManager.persist(trainer2);

        // Crear aprendices
        trainee1 = new Trainee();
        trainee1.setFirstName("Mark");
        trainee1.setLastName("Johnson");
        trainee1.setUsername("markj");
        trainee1.setPassword("password789");
        trainee1.setActive(true);
        trainee1.setDateOfBirth(LocalDate.of(1990, 5, 15));
        trainee1.setAddress("123 Main St");
        entityManager.persist(trainee1);

        trainee2 = new Trainee();
        trainee2.setFirstName("Sarah");
        trainee2.setLastName("Williams");
        trainee2.setUsername("sarahw");
        trainee2.setPassword("password987");
        trainee2.setActive(false);
        trainee2.setDateOfBirth(LocalDate.of(1992, 8, 21));
        trainee2.setAddress("456 Elm St");
        entityManager.persist(trainee2);

        // Asociar trainer1 con trainee1
        trainer1.getTrainees().add(trainee1);
        trainee1.getTrainers().add(trainer1);
        
        // Asociar trainer2 con trainee1
        trainer2.getTrainees().add(trainee1);
        trainee1.getTrainers().add(trainer2);

        entityManager.flush();
    }

    @Test
    @DisplayName("Should find trainee by username when trainee exists")
    void findByUsername_ShouldReturnTrainee_WhenTraineeExists() {
        // when
        Optional<Trainee> found = traineeRepository.findByUsername("markj");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("Mark");
        assertThat(found.get().getLastName()).isEqualTo("Johnson");
    }

    @Test
    @DisplayName("Should return empty when finding by non-existent username")
    void findByUsername_ShouldReturnEmpty_WhenTraineeDoesNotExist() {
        // when
        Optional<Trainee> found = traineeRepository.findByUsername("nonexistent");

        // then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should check if trainee exists by username")
    void existsByUsername_ShouldReturnTrue_WhenTraineeExists() {
        // when
        boolean exists = traineeRepository.existsByUsername("markj");
        boolean notExists = traineeRepository.existsByUsername("nonexistent");

        // then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Should update trainee password")
    @Transactional
    void updatePassword_ShouldUpdatePasswordSuccessfully() {
        // given
        String newPassword = "newPassword123";
        
        // when
        int updatedRows = traineeRepository.updatePassword(trainee1.getId(), newPassword);
        entityManager.clear(); // Clear persistence context to force reload
        
        // then
        assertThat(updatedRows).isEqualTo(1);
        Trainee updatedTrainee = entityManager.find(Trainee.class, trainee1.getId());
        assertThat(updatedTrainee.getPassword()).isEqualTo(newPassword);
    }

    @Test
    @DisplayName("Should delete trainee by username")
    @Transactional
    void deleteByUsername_ShouldDeleteTrainee() {
        // when
        int deletedRows = traineeRepository.deleteByUsername("markj");
        
        // then
        assertThat(deletedRows).isEqualTo(1);
        Optional<Trainee> deleted = traineeRepository.findByUsername("markj");
        assertThat(deleted).isEmpty();
    }

    @Test
    @DisplayName("Should find trainers by trainee id")
    void findTrainersByTraineeId_ShouldReturnTrainers() {
        // when
        Set<Trainer> trainers = traineeRepository.findTrainersByTraineeId(trainee1.getId());
        
        // then
        assertThat(trainers).hasSize(2);
        assertThat(trainers).contains(trainer1, trainer2);
    }

    @Test
    @DisplayName("Should count active trainees")
    void countByActive_ShouldReturnCorrectCount() {
        // when
        long activeCount = traineeRepository.countByActive(true);
        
        // then
        assertThat(activeCount).isEqualTo(1);
    }
} 