package dev.sro.workload_service.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;

import dev.sro.workload_service.config.TestMongoConfig;
import dev.sro.workload_service.entity.TrainerTrainingSummary;
import dev.sro.workload_service.entity.YearSummary;
import dev.sro.workload_service.entity.MonthSummary;

@DataMongoTest
@ActiveProfiles("test")
@Import(TestMongoConfig.class)
class TrainerTrainingSummaryRepositoryTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private TrainerTrainingSummaryRepository trainerTrainingSummaryRepository;

    @BeforeEach
    void setUp() {
        mongoTemplate.remove(new Query(), TrainerTrainingSummary.class);

        MonthSummary month1 = MonthSummary.builder()
                .month(1)
                .trainingsSummaryDuration(120)
                .build();

        MonthSummary month2 = MonthSummary.builder()
                .month(2)
                .trainingsSummaryDuration(180)
                .build();

        MonthSummary month3 = MonthSummary.builder()
                .month(3)
                .trainingsSummaryDuration(90)
                .build();

        YearSummary year2024 = YearSummary.builder()
                .year(2024)
                .months(List.of(month1, month2))
                .build();

        YearSummary year2023 = YearSummary.builder()
                .year(2023)
                .months(List.of(month3))
                .build();

        TrainerTrainingSummary trainer1 = TrainerTrainingSummary.builder()
                .id("trainer-1")
                .trainerUsername("john.doe")
                .trainerFirstName("John")
                .trainerLastName("Doe")
                .trainerStatus(true)
                .years(List.of(year2024, year2023))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        mongoTemplate.save(trainer1);

        TrainerTrainingSummary trainer2 = TrainerTrainingSummary.builder()
                .id("trainer-2")
                .trainerUsername("jane.smith")
                .trainerFirstName("Jane")
                .trainerLastName("Smith")
                .trainerStatus(true)
                .years(List.of(year2024))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        mongoTemplate.save(trainer2);

        TrainerTrainingSummary trainer3 = TrainerTrainingSummary.builder()
                .id("trainer-3")
                .trainerUsername("bob.wilson")
                .trainerFirstName("Bob")
                .trainerLastName("Wilson")
                .trainerStatus(false)
                .years(List.of())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        mongoTemplate.save(trainer3);
    }

    @Test
    @DisplayName("Should find trainer training summary by username")
    void findByTrainerUsername_ShouldReturnTrainerSummary() {
        Optional<TrainerTrainingSummary> trainer = trainerTrainingSummaryRepository.findByTrainerUsername("john.doe");

        assertThat(trainer).isPresent();
        assertThat(trainer.get().getTrainerUsername()).isEqualTo("john.doe");
        assertThat(trainer.get().getTrainerFirstName()).isEqualTo("John");
        assertThat(trainer.get().getTrainerLastName()).isEqualTo("Doe");
        assertThat(trainer.get().getTrainerStatus()).isTrue();
        assertThat(trainer.get().getYears()).hasSize(2);
    }

    @Test
    @DisplayName("Should return empty when trainer username does not exist")
    void findByTrainerUsername_ShouldReturnEmpty_WhenUsernameDoesNotExist() {
        Optional<TrainerTrainingSummary> trainer = trainerTrainingSummaryRepository
                .findByTrainerUsername("nonexistent");

        assertThat(trainer).isEmpty();
    }

    @Test
    @DisplayName("Should find trainers by first name and last name")
    void findByTrainerFirstNameAndTrainerLastName_ShouldReturnTrainers() {
        List<TrainerTrainingSummary> trainers = trainerTrainingSummaryRepository
                .findByTrainerFirstNameAndTrainerLastName("John", "Doe");

        assertThat(trainers).hasSize(1);
        assertThat(trainers.get(0).getTrainerUsername()).isEqualTo("john.doe");
    }

    @Test
    @DisplayName("Should return empty list when first name and last name combination does not exist")
    void findByTrainerFirstNameAndTrainerLastName_ShouldReturnEmpty_WhenCombinationDoesNotExist() {
        List<TrainerTrainingSummary> trainers = trainerTrainingSummaryRepository
                .findByTrainerFirstNameAndTrainerLastName("John", "Smith");

        assertThat(trainers).isEmpty();
    }

    @Test
    @DisplayName("Should find trainers by first name")
    void findByTrainerFirstName_ShouldReturnTrainers() {
        List<TrainerTrainingSummary> trainers = trainerTrainingSummaryRepository.findByTrainerFirstName("John");

        assertThat(trainers).hasSize(1);
        assertThat(trainers.get(0).getTrainerUsername()).isEqualTo("john.doe");
    }

    @Test
    @DisplayName("Should return empty list when first name does not exist")
    void findByTrainerFirstName_ShouldReturnEmpty_WhenFirstNameDoesNotExist() {
        List<TrainerTrainingSummary> trainers = trainerTrainingSummaryRepository.findByTrainerFirstName("Alice");

        assertThat(trainers).isEmpty();
    }

    @Test
    @DisplayName("Should find trainers by last name")
    void findByTrainerLastName_ShouldReturnTrainers() {
        List<TrainerTrainingSummary> trainers = trainerTrainingSummaryRepository.findByTrainerLastName("Smith");

        assertThat(trainers).hasSize(1);
        assertThat(trainers.get(0).getTrainerUsername()).isEqualTo("jane.smith");
    }

    @Test
    @DisplayName("Should return empty list when last name does not exist")
    void findByTrainerLastName_ShouldReturnEmpty_WhenLastNameDoesNotExist() {
        List<TrainerTrainingSummary> trainers = trainerTrainingSummaryRepository.findByTrainerLastName("Johnson");

        assertThat(trainers).isEmpty();
    }

    @Test
    @DisplayName("Should check if trainer exists by username")
    void existsByTrainerUsername_ShouldReturnTrue_WhenTrainerExists() {
        boolean exists = trainerTrainingSummaryRepository.existsByTrainerUsername("john.doe");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when trainer username does not exist")
    void existsByTrainerUsername_ShouldReturnFalse_WhenUsernameDoesNotExist() {
        boolean exists = trainerTrainingSummaryRepository.existsByTrainerUsername("nonexistent");

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should find trainer by username and year/month")
    void findByUsernameAndYearAndMonth_ShouldReturnTrainer() {
        Optional<TrainerTrainingSummary> trainer = trainerTrainingSummaryRepository
                .findByUsernameAndYearAndMonth("john.doe", 2024, 1);

        assertThat(trainer).isPresent();
        assertThat(trainer.get().getTrainerUsername()).isEqualTo("john.doe");
        assertThat(trainer.get().getYears()).anyMatch(year -> year.getYear().equals(2024) &&
                year.getMonths().stream().anyMatch(month -> month.getMonth().equals(1)));
    }

    @Test
    @DisplayName("Should return empty when trainer does not have data for year/month")
    void findByUsernameAndYearAndMonth_ShouldReturnEmpty_WhenNoDataForYearMonth() {
        Optional<TrainerTrainingSummary> trainer = trainerTrainingSummaryRepository
                .findByUsernameAndYearAndMonth("john.doe", 2024, 12);

        assertThat(trainer).isEmpty();
    }

    @Test
    @DisplayName("Should return empty when trainer does not exist")
    void findByUsernameAndYearAndMonth_ShouldReturnEmpty_WhenTrainerDoesNotExist() {
        Optional<TrainerTrainingSummary> trainer = trainerTrainingSummaryRepository
                .findByUsernameAndYearAndMonth("nonexistent", 2024, 1);

        assertThat(trainer).isEmpty();
    }

    @Test
    @DisplayName("Should save new trainer training summary")
    void save_ShouldSaveNewTrainerSummary() {
        TrainerTrainingSummary newTrainer = TrainerTrainingSummary.builder()
                .id("trainer-4")
                .trainerUsername("new.trainer")
                .trainerFirstName("New")
                .trainerLastName("Trainer")
                .trainerStatus(true)
                .years(List.of())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        TrainerTrainingSummary savedTrainer = trainerTrainingSummaryRepository.save(newTrainer);

        assertThat(savedTrainer).isNotNull();
        assertThat(savedTrainer.getId()).isEqualTo("trainer-4");
        assertThat(savedTrainer.getTrainerUsername()).isEqualTo("new.trainer");
        assertThat(savedTrainer.getTrainerStatus()).isTrue();
    }

    @Test
    @DisplayName("Should update existing trainer training summary")
    void save_ShouldUpdateExistingTrainerSummary() {
        TrainerTrainingSummary existingTrainer = trainerTrainingSummaryRepository
                .findByTrainerUsername("john.doe")
                .orElseThrow();

        existingTrainer.setTrainerStatus(false);
        existingTrainer.setUpdatedAt(LocalDateTime.now());

        TrainerTrainingSummary updatedTrainer = trainerTrainingSummaryRepository.save(existingTrainer);

        assertThat(updatedTrainer.getTrainerStatus()).isFalse();
    }

    @Test
    @DisplayName("Should find all trainer training summaries")
    void findAll_ShouldReturnAllTrainerSummaries() {
        List<TrainerTrainingSummary> allTrainers = trainerTrainingSummaryRepository.findAll();

        assertThat(allTrainers).hasSize(3);
        assertThat(allTrainers).extracting(TrainerTrainingSummary::getTrainerUsername)
                .containsExactlyInAnyOrder("john.doe", "jane.smith", "bob.wilson");
    }

    @Test
    @DisplayName("Should delete trainer training summary")
    void delete_ShouldDeleteTrainerSummary() {
        TrainerTrainingSummary trainerToDelete = trainerTrainingSummaryRepository
                .findByTrainerUsername("john.doe")
                .orElseThrow();

        trainerTrainingSummaryRepository.delete(trainerToDelete);

        Optional<TrainerTrainingSummary> deletedTrainer = trainerTrainingSummaryRepository
                .findByTrainerUsername("john.doe");

        assertThat(deletedTrainer).isEmpty();
    }

    @Test
    @DisplayName("Should find trainer training summary by ID")
    void findById_ShouldReturnTrainerSummary() {
        Optional<TrainerTrainingSummary> trainer = trainerTrainingSummaryRepository.findById("trainer-1");

        assertThat(trainer).isPresent();
        assertThat(trainer.get().getTrainerUsername()).isEqualTo("john.doe");
        assertThat(trainer.get().getTrainerFirstName()).isEqualTo("John");
    }

    @Test
    @DisplayName("Should return empty when trainer ID does not exist")
    void findById_ShouldReturnEmpty_WhenIdDoesNotExist() {
        Optional<TrainerTrainingSummary> trainer = trainerTrainingSummaryRepository.findById("non-existent-id");

        assertThat(trainer).isEmpty();
    }

    @Test
    @DisplayName("Should find active trainers")
    void findByTrainerStatus_ShouldReturnActiveTrainers() {
        List<TrainerTrainingSummary> activeTrainers = trainerTrainingSummaryRepository
                .findAll().stream()
                .filter(TrainerTrainingSummary::getTrainerStatus)
                .toList();

        assertThat(activeTrainers).hasSize(2);
        assertThat(activeTrainers).extracting(TrainerTrainingSummary::getTrainerUsername)
                .containsExactlyInAnyOrder("john.doe", "jane.smith");
    }

    @Test
    @DisplayName("Should find inactive trainers")
    void findByTrainerStatus_ShouldReturnInactiveTrainers() {
        List<TrainerTrainingSummary> inactiveTrainers = trainerTrainingSummaryRepository
                .findAll().stream()
                .filter(trainer -> !trainer.getTrainerStatus())
                .toList();

        assertThat(inactiveTrainers).hasSize(1);
        assertThat(inactiveTrainers.get(0).getTrainerUsername()).isEqualTo("bob.wilson");
    }
}