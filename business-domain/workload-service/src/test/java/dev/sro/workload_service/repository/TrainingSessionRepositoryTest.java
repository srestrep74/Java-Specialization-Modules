package dev.sro.workload_service.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import dev.sro.workload_service.config.TestMongoConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;

import dev.sro.workload_service.entity.TrainingSession;
import dev.sro.workload_service.entity.enums.ActionType;

@DataMongoTest
@ActiveProfiles("test")
@Import(TestMongoConfig.class)
class TrainingSessionRepositoryTest {

        @Autowired
        private MongoTemplate mongoTemplate;

        @Autowired
        private TrainingSessionRepository trainingSessionRepository;

        @BeforeEach
        void setUp() {
                mongoTemplate.remove(new Query(), TrainingSession.class);

                TrainingSession session1 = TrainingSession.builder()
                                .id("session-1")
                                .trainerUsername("test.trainer")
                                .trainingDate(LocalDate.of(2024, 1, 15))
                                .trainingDuration(60)
                                .actionType(ActionType.ADD)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();
                mongoTemplate.save(session1);

                TrainingSession session2 = TrainingSession.builder()
                                .id("session-2")
                                .trainerUsername("test.trainer")
                                .trainingDate(LocalDate.of(2024, 1, 20))
                                .trainingDuration(90)
                                .actionType(ActionType.ADD)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();
                mongoTemplate.save(session2);

                TrainingSession session3 = TrainingSession.builder()
                                .id("session-3")
                                .trainerUsername("test.trainer")
                                .trainingDate(LocalDate.of(2024, 2, 10))
                                .trainingDuration(45)
                                .actionType(ActionType.ADD)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();
                mongoTemplate.save(session3);

                TrainingSession session4 = TrainingSession.builder()
                                .id("session-4")
                                .trainerUsername("another.trainer")
                                .trainingDate(LocalDate.of(2024, 1, 25))
                                .trainingDuration(75)
                                .actionType(ActionType.DELETE)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();
                mongoTemplate.save(session4);
        }

        @Test
        @DisplayName("Should find training session by trainer username and training date")
        void findByTrainerUsernameAndTrainingDate_ShouldReturnSession() {
                LocalDate trainingDate = LocalDate.of(2024, 1, 15);

                Optional<TrainingSession> session = trainingSessionRepository
                                .findByTrainerUsernameAndTrainingDate("test.trainer", trainingDate);

                assertThat(session).isPresent();
                assertThat(session.get().getTrainerUsername()).isEqualTo("test.trainer");
                assertThat(session.get().getTrainingDate()).isEqualTo(trainingDate);
                assertThat(session.get().getTrainingDuration()).isEqualTo(60);
        }

        @Test
        @DisplayName("Should return empty when no session exists for trainer and date")
        void findByTrainerUsernameAndTrainingDate_ShouldReturnEmpty_WhenNoSessionExists() {
                LocalDate trainingDate = LocalDate.of(2024, 1, 30);

                Optional<TrainingSession> session = trainingSessionRepository
                                .findByTrainerUsernameAndTrainingDate("test.trainer", trainingDate);

                assertThat(session).isEmpty();
        }

        @Test
        @DisplayName("Should find training sessions by trainer username and within a date range")
        void findByTrainerUsernameAndTrainingDateBetween_ShouldReturnSessionsInDateRange() {
                LocalDate startDate = LocalDate.of(2024, 1, 1);
                LocalDate endDate = LocalDate.of(2024, 1, 31);

                List<TrainingSession> sessions = trainingSessionRepository
                                .findByTrainerUsernameAndTrainingDateBetween("test.trainer", startDate, endDate);

                assertThat(sessions).hasSize(2);
                assertThat(sessions).extracting(TrainingSession::getTrainingDate)
                                .allMatch(date -> date.getMonthValue() == 1);
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
        @DisplayName("Should return empty list for year/month with no sessions")
        void findByTrainerUsernameAndYearAndMonth_ShouldReturnEmpty_WhenNoSessionsForYearAndMonth() {
                Integer year = 2024;
                Integer month = 3;

                List<TrainingSession> sessions = trainingSessionRepository
                                .findByTrainerUsernameAndYearAndMonth("test.trainer", year, month);

                assertThat(sessions).isEmpty();
        }

        @Test
        @DisplayName("Should check if training session exists for trainer and date")
        void existsByTrainerUsernameAndTrainingDate_ShouldReturnTrue_WhenSessionExists() {
                LocalDate trainingDate = LocalDate.of(2024, 1, 15);

                boolean exists = trainingSessionRepository
                                .existsByTrainerUsernameAndTrainingDate("test.trainer", trainingDate);

                assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("Should return false when training session does not exist for trainer and date")
        void existsByTrainerUsernameAndTrainingDate_ShouldReturnFalse_WhenSessionDoesNotExist() {
                LocalDate trainingDate = LocalDate.of(2024, 1, 30);

                boolean exists = trainingSessionRepository
                                .existsByTrainerUsernameAndTrainingDate("test.trainer", trainingDate);

                assertThat(exists).isFalse();
        }

        @Test
        @DisplayName("Should save new training session")
        void save_ShouldSaveNewTrainingSession() {
                TrainingSession newSession = TrainingSession.builder()
                                .id("session-5")
                                .trainerUsername("new.trainer")
                                .trainingDate(LocalDate.of(2024, 3, 15))
                                .trainingDuration(120)
                                .actionType(ActionType.UPDATE)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();

                TrainingSession savedSession = trainingSessionRepository.save(newSession);

                assertThat(savedSession).isNotNull();
                assertThat(savedSession.getId()).isEqualTo("session-5");
                assertThat(savedSession.getTrainerUsername()).isEqualTo("new.trainer");
                assertThat(savedSession.getActionType()).isEqualTo(ActionType.UPDATE);
        }

        @Test
        @DisplayName("Should update existing training session")
        void save_ShouldUpdateExistingTrainingSession() {
                TrainingSession existingSession = trainingSessionRepository
                                .findByTrainerUsernameAndTrainingDate("test.trainer", LocalDate.of(2024, 1, 15))
                                .orElseThrow();

                existingSession.setTrainingDuration(150);
                existingSession.setActionType(ActionType.UPDATE);
                existingSession.setUpdatedAt(LocalDateTime.now());

                TrainingSession updatedSession = trainingSessionRepository.save(existingSession);

                assertThat(updatedSession.getTrainingDuration()).isEqualTo(150);
                assertThat(updatedSession.getActionType()).isEqualTo(ActionType.UPDATE);
        }

        @Test
        @DisplayName("Should find all training sessions")
        void findAll_ShouldReturnAllTrainingSessions() {
                List<TrainingSession> allSessions = trainingSessionRepository.findAll();

                assertThat(allSessions).hasSize(4);
                assertThat(allSessions).extracting(TrainingSession::getTrainerUsername)
                                .containsExactlyInAnyOrder("test.trainer", "test.trainer", "test.trainer",
                                                "another.trainer");
        }

        @Test
        @DisplayName("Should delete training session")
        void delete_ShouldDeleteTrainingSession() {
                TrainingSession sessionToDelete = trainingSessionRepository
                                .findByTrainerUsernameAndTrainingDate("test.trainer", LocalDate.of(2024, 1, 15))
                                .orElseThrow();

                trainingSessionRepository.delete(sessionToDelete);

                Optional<TrainingSession> deletedSession = trainingSessionRepository
                                .findByTrainerUsernameAndTrainingDate("test.trainer", LocalDate.of(2024, 1, 15));

                assertThat(deletedSession).isEmpty();
        }

        @Test
        @DisplayName("Should find training session by ID")
        void findById_ShouldReturnTrainingSession() {
                Optional<TrainingSession> session = trainingSessionRepository.findById("session-1");

                assertThat(session).isPresent();
                assertThat(session.get().getTrainerUsername()).isEqualTo("test.trainer");
                assertThat(session.get().getTrainingDate()).isEqualTo(LocalDate.of(2024, 1, 15));
        }

        @Test
        @DisplayName("Should return empty when training session ID does not exist")
        void findById_ShouldReturnEmpty_WhenIdDoesNotExist() {
                Optional<TrainingSession> session = trainingSessionRepository.findById("non-existent-id");

                assertThat(session).isEmpty();
        }
}