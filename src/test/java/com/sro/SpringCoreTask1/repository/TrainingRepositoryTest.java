package com.sro.SpringCoreTask1.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import com.sro.SpringCoreTask1.entity.Trainee;
import com.sro.SpringCoreTask1.entity.Trainer;
import com.sro.SpringCoreTask1.entity.Training;
import com.sro.SpringCoreTask1.entity.TrainingType;

@DataJpaTest
@ActiveProfiles("test")
class TrainingRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TrainingRepository trainingRepository;

    private Trainee trainee;
    private Trainer trainer1;
    private Trainer trainer2;
    private TrainingType trainingType1;
    private TrainingType trainingType2;
    private Training training1;
    private Training training2;
    private Training training3;
    private LocalDate today;
    private LocalDate tomorrow;
    private LocalDate yesterday;

    @BeforeEach
    void setUp() {
        today = LocalDate.now();
        tomorrow = today.plusDays(1);
        yesterday = today.minusDays(1);

        trainingType1 = new TrainingType();
        trainingType1.setTrainingTypeName("Fitness");
        entityManager.persist(trainingType1);

        trainingType2 = new TrainingType();
        trainingType2.setTrainingTypeName("Yoga");
        entityManager.persist(trainingType2);

        trainee = new Trainee();
        trainee.setFirstName("Mark");
        trainee.setLastName("Johnson");
        trainee.setUsername("markj");
        trainee.setPassword("password123");
        trainee.setActive(true);
        trainee.setDateOfBirth(LocalDate.of(1990, 5, 15));
        trainee.setAddress("123 Main St");
        entityManager.persist(trainee);

        trainer1 = new Trainer();
        trainer1.setFirstName("John");
        trainer1.setLastName("Doe");
        trainer1.setUsername("johndoe");
        trainer1.setPassword("password456");
        trainer1.setActive(true);
        trainer1.setTrainingType(trainingType1);
        entityManager.persist(trainer1);

        trainer2 = new Trainer();
        trainer2.setFirstName("Jane");
        trainer2.setLastName("Smith");
        trainer2.setUsername("janesmith");
        trainer2.setPassword("password789");
        trainer2.setActive(true);
        trainer2.setTrainingType(trainingType2);
        entityManager.persist(trainer2);

        trainer1.getTrainees().add(trainee);
        trainee.getTrainers().add(trainer1);
        trainer2.getTrainees().add(trainee);
        trainee.getTrainers().add(trainer2);

        training1 = new Training();
        training1.setTrainingName("Morning Fitness");
        training1.setTrainingDate(today);
        training1.setDuration(60);
        training1.setTrainee(trainee);
        training1.setTrainer(trainer1);
        training1.setTrainingType(trainingType1);
        entityManager.persist(training1);

        training2 = new Training();
        training2.setTrainingName("Evening Yoga");
        training2.setTrainingDate(today);
        training2.setDuration(90);
        training2.setTrainee(trainee);
        training2.setTrainer(trainer2);
        training2.setTrainingType(trainingType2);
        entityManager.persist(training2);

        training3 = new Training();
        training3.setTrainingName("Advanced Fitness");
        training3.setTrainingDate(tomorrow);
        training3.setDuration(120);
        training3.setTrainee(trainee);
        training3.setTrainer(trainer1);
        training3.setTrainingType(trainingType1);
        entityManager.persist(training3);

        entityManager.flush();
    }

    @Test
    @DisplayName("Should find training by id")
    void findById_ShouldReturnTraining_WhenTrainingExists() {
        Optional<Training> found = trainingRepository.findById(training1.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getTrainingName()).isEqualTo("Morning Fitness");
        assertThat(found.get().getDuration()).isEqualTo(60);
    }

    @Test
    @DisplayName("Should return empty when training does not exist")
    void findById_ShouldReturnEmpty_WhenTrainingDoesNotExist() {
        Optional<Training> found = trainingRepository.findById(9999L);
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should save training successfully")
    void save_ShouldPersistTraining() {
        Training newTraining = new Training();
        newTraining.setTrainingName("New Training");
        newTraining.setTrainingDate(tomorrow);
        newTraining.setDuration(45);
        newTraining.setTrainee(trainee);
        newTraining.setTrainer(trainer2);
        newTraining.setTrainingType(trainingType2);

        Training saved = trainingRepository.save(newTraining);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTrainingName()).isEqualTo("New Training");

        Training retrieved = entityManager.find(Training.class, saved.getId());
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getDuration()).isEqualTo(45);
    }

    @Test
    @DisplayName("Should check existence by trainee, trainer and date")
    void existsByTraineeAndTrainerAndTrainingDate_ShouldReturnCorrectResult() {
        assertThat(trainingRepository.existsByTraineeAndTrainerAndTrainingDate(trainee, trainer1, today)).isTrue();
        assertThat(trainingRepository.existsByTraineeAndTrainerAndTrainingDate(trainee, trainer1, yesterday)).isFalse();
    }

    @Test
    @DisplayName("Should delete training successfully")
    void deleteById_ShouldRemoveTraining() {
        trainingRepository.deleteById(training1.getId());
        entityManager.flush();
        entityManager.clear();

        Training deleted = entityManager.find(Training.class, training1.getId());
        assertThat(deleted).isNull();
    }

    @Test
    @DisplayName("Should find all trainings")
    void findAll_ShouldReturnAllTrainings() {
        List<Training> trainings = trainingRepository.findAll();
        assertThat(trainings).hasSize(3)
                .extracting(Training::getTrainingName)
                .containsExactlyInAnyOrder("Morning Fitness", "Evening Yoga", "Advanced Fitness");
    }

    @Test
    @DisplayName("Should filter trainings by specification")
    void findAll_WithSpecification_ShouldReturnFilteredTrainings() {
        Specification<Training> fitnessSpec = (root, query, cb) -> cb
                .equal(root.get("trainingType").get("trainingTypeName"), "Fitness");
        List<Training> fitnessTrainings = trainingRepository.findAll(fitnessSpec);
        assertThat(fitnessTrainings).hasSize(2)
                .extracting(Training::getTrainingName)
                .containsExactlyInAnyOrder("Morning Fitness", "Advanced Fitness");

        Specification<Training> todaySpec = (root, query, cb) -> cb.equal(root.get("trainingDate"), today);
        List<Training> todayTrainings = trainingRepository.findAll(todaySpec);
        assertThat(todayTrainings).hasSize(2)
                .extracting(Training::getTrainingName)
                .containsExactlyInAnyOrder("Morning Fitness", "Evening Yoga");
    }
}