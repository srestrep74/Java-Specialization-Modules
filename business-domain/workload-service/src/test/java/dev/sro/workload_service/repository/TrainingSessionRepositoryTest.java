package dev.sro.workload_service.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.test.context.ActiveProfiles;

import dev.sro.workload_service.entity.Trainer;
import dev.sro.workload_service.entity.TrainingSession;
import dev.sro.workload_service.entity.enums.ActionType;

@DataJpaTest
@ActiveProfiles("test")
@EntityScan(basePackages = "dev.sro.workload_service.entity")
class TrainingSessionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TrainingSessionRepository trainingSessionRepository;

    private Trainer trainer;

    @BeforeEach
    void setUp() {
        trainer = Trainer.builder()
                .username("test.trainer")
                .firstName("Test")
                .lastName("Trainer")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        entityManager.persist(trainer);

        TrainingSession session1 = TrainingSession.builder()
                .trainer(trainer)
                .trainingDate(LocalDate.of(2024, 1, 15))
                .trainingDuration(60)
                .actionType(ActionType.ADD)
                .build();
        entityManager.persist(session1);

        TrainingSession session2 = TrainingSession.builder()
                .trainer(trainer)
                .trainingDate(LocalDate.of(2024, 1, 20))
                .trainingDuration(90)
                .actionType(ActionType.ADD)
                .build();
        entityManager.persist(session2);

        TrainingSession session3 = TrainingSession.builder()
                .trainer(trainer)
                .trainingDate(LocalDate.of(2024, 2, 10))
                .trainingDuration(45)
                .actionType(ActionType.ADD)
                .build();
        entityManager.persist(session3);

        entityManager.flush();
    }

    @Test
    @DisplayName("Should find training sessions by trainer username and within a date range")
    void findByTrainerUsernameAndTrainingDateBetween_ShouldReturnSessionsInDateRange() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        List<TrainingSession> sessions = trainingSessionRepository
                .findByTrainerUsernameAndTrainingDateBetween("test.trainer", startDate, endDate);

        assertThat(sessions).hasSize(2);
        assertThat(sessions).extracting(s -> s.getTrainingDate().getMonthValue()).containsOnly(1);
    }

    @Test
    @DisplayName("Should return empty list for date range with no sessions")
    void findByTrainerUsernameAndTrainingDateBetween_ShouldReturnEmpty_WhenNoSessionsInDateRange() {
        LocalDate startDate = LocalDate.of(2024, 3, 1);
        LocalDate endDate = LocalDate.of(2024, 3, 31);

        List<TrainingSession> sessions = trainingSessionRepository
                .findByTrainerUsernameAndTrainingDateBetween("test.trainer", startDate, endDate);

        assertThat(sessions).isEmpty();
    }

    @Test
    @DisplayName("Should find all training sessions for a given trainer username")
    void findByTrainerUsername_ShouldReturnAllSessionsForTrainer() {
        List<TrainingSession> sessions = trainingSessionRepository.findByTrainerUsername("test.trainer");

        assertThat(sessions).hasSize(3);
    }

    @Test
    @DisplayName("Should return empty list when trainer has no sessions")
    void findByTrainerUsername_ShouldReturnEmpty_WhenTrainerHasNoSessions() {
        Trainer newTrainer = Trainer.builder()
                .username("new.trainer")
                .firstName("New")
                .lastName("Trainer")
                .isActive(true)
                .build();
        entityManager.persist(newTrainer);
        entityManager.flush();

        List<TrainingSession> sessions = trainingSessionRepository.findByTrainerUsername("new.trainer");

        assertThat(sessions).isEmpty();
    }

    @Test
    @DisplayName("Should return empty list for a non-existent trainer")
    void findByTrainerUsername_ShouldReturnEmpty_ForNonExistentTrainer() {
        List<TrainingSession> sessions = trainingSessionRepository.findByTrainerUsername("non.existent");

        assertThat(sessions).isEmpty();
    }
}