package com.sro.SpringCoreTask1.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import com.sro.SpringCoreTask1.entity.Trainee;
import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.entity.TrainingType;

@DataJpaTest
@ActiveProfiles("test")
class TrainerRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TrainerRepository trainerRepository;

    private Trainer activeTrainer;
    private Trainer inactiveTrainer;
    private Trainee trainee;
    private TrainingType trainingType;

    @BeforeEach
    void setUp() {
        trainingType = createTrainingType("Fitness");
        activeTrainer = createTrainer("John", "Doe", "johndoe", true);
        inactiveTrainer = createTrainer("Jane", "Smith", "janesmith", false);
        trainee = createTrainee("Mark", "Johnson", "markj");
        
        associateTrainerWithTrainee(activeTrainer, trainee);
    }

    private TrainingType createTrainingType(String name) {
        TrainingType type = new TrainingType();
        type.setTrainingTypeName(name);
        return entityManager.persist(type);
    }

    private Trainer createTrainer(String firstName, String lastName, String username, boolean active) {
        Trainer trainer = new Trainer();
        trainer.setFirstName(firstName);
        trainer.setLastName(lastName);
        trainer.setUsername(username);
        trainer.setPassword("password");
        trainer.setActive(active);
        trainer.setTrainingType(trainingType);
        return entityManager.persist(trainer);
    }

    private Trainee createTrainee(String firstName, String lastName, String username) {
        Trainee trainee = new Trainee();
        trainee.setFirstName(firstName);
        trainee.setLastName(lastName);
        trainee.setUsername(username);
        trainee.setPassword("password");
        trainee.setActive(true);
        trainee.setDateOfBirth(LocalDate.of(1990, 5, 15));
        trainee.setAddress("123 Main St");
        return entityManager.persist(trainee);
    }

    private void associateTrainerWithTrainee(Trainer trainer, Trainee trainee) {
        trainer.getTrainees().add(trainee);
        trainee.getTrainers().add(trainer);
        entityManager.flush();
    }

    @Test
    void findByUsername_ShouldReturnTrainer_WhenExists() {
        Optional<Trainer> found = trainerRepository.findByUsername("johndoe");
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("John");
    }

    @Test
    void findByUsername_ShouldReturnEmpty_WhenNotExists() {
        Optional<Trainer> found = trainerRepository.findByUsername("nonexistent");
        assertThat(found).isEmpty();
    }

    @Test
    void updatePassword_ShouldUpdateSuccessfully() {
        String newPassword = "newPassword123";
        trainerRepository.updatePassword(activeTrainer.getId(), newPassword);
        entityManager.clear();
        
        assertThat(entityManager.find(Trainer.class, activeTrainer.getId()).getPassword())
            .isEqualTo(newPassword);
    }

    @Test
    void findTrainersByTraineeId_ShouldReturnAssociatedTrainers() {
        Set<Trainer> trainers = trainerRepository.findTrainersByTraineeId(trainee.getId());
        assertThat(trainers).hasSize(1).contains(activeTrainer);
    }

    @Test
    void countByActive_ShouldReturnCorrectCount() {
        assertThat(trainerRepository.countByActive(true)).isEqualTo(1);
        assertThat(trainerRepository.countByActive(false)).isEqualTo(1);
    }

    @Test
    void findUnassignedTrainers_UsingSpecification() {
        Trainer unassignedTrainer = createTrainer("Unassigned", "Trainer", "unassigned", true);
        entityManager.flush();
        
        Specification<Trainer> spec = (root, query, cb) -> 
            cb.notEqual(root.get("username"), "johndoe");
        
        var trainers = trainerRepository.findAll(spec);
        
        assertThat(trainers)
            .hasSize(2)
            .contains(inactiveTrainer, unassignedTrainer)
            .doesNotContain(activeTrainer);
    }
}