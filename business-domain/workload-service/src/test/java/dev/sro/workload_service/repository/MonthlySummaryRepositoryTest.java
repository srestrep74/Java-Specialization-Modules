package dev.sro.workload_service.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import dev.sro.workload_service.entity.MonthlySummary;
import dev.sro.workload_service.entity.Trainer;

@DataJpaTest
@ActiveProfiles("test")
@EntityScan(basePackages = "dev.sro.workload_service.entity")
class MonthlySummaryRepositoryTest {

        @Autowired
        private TestEntityManager entityManager;

        @Autowired
        private MonthlySummaryRepository monthlySummaryRepository;

        private Trainer trainer1;
        private Trainer trainer2;
        private MonthlySummary summary1;
        private MonthlySummary summary2;
        private MonthlySummary summary3;

        @BeforeEach
        void setUp() {
                trainer1 = Trainer.builder()
                                .username("john.doe")
                                .firstName("John")
                                .lastName("Doe")
                                .isActive(true)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();
                entityManager.persist(trainer1);

                trainer2 = Trainer.builder()
                                .username("jane.smith")
                                .firstName("Jane")
                                .lastName("Smith")
                                .isActive(true)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();
                entityManager.persist(trainer2);

                summary1 = MonthlySummary.builder()
                                .trainer(trainer1)
                                .year(2024)
                                .month(1)
                                .totalDuration(120)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();
                entityManager.persist(summary1);

                summary2 = MonthlySummary.builder()
                                .trainer(trainer1)
                                .year(2024)
                                .month(2)
                                .totalDuration(180)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();
                entityManager.persist(summary2);

                summary3 = MonthlySummary.builder()
                                .trainer(trainer2)
                                .year(2024)
                                .month(1)
                                .totalDuration(90)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();
                entityManager.persist(summary3);

                entityManager.flush();
        }

        @Test
        @DisplayName("Should find monthly summary by trainer username, year and month when summary exists")
        void findByTrainerUsernameAndYearAndMonth_ShouldReturnMonthlySummary_WhenSummaryExists() {
                Optional<MonthlySummary> found = monthlySummaryRepository
                                .findByTrainer_UsernameAndYearAndMonth("john.doe", 2024, 1);

                assertThat(found).isPresent();
                assertThat(found.get().getTrainer().getUsername()).isEqualTo("john.doe");
                assertThat(found.get().getYear()).isEqualTo(2024);
                assertThat(found.get().getMonth()).isEqualTo(1);
                assertThat(found.get().getTotalDuration()).isEqualTo(120);
        }

        @Test
        @DisplayName("Should return empty when finding by non-existent trainer, year and month combination")
        void findByTrainerUsernameAndYearAndMonth_ShouldReturnEmpty_WhenSummaryDoesNotExist() {
                Optional<MonthlySummary> found = monthlySummaryRepository
                                .findByTrainer_UsernameAndYearAndMonth("john.doe", 2024, 12);

                assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("Should find all monthly summaries by trainer username")
        void findByTrainerUsername_ShouldReturnAllSummaries_WhenSummariesExist() {
                List<MonthlySummary> summaries = monthlySummaryRepository.findByTrainer_Username("john.doe");

                assertThat(summaries).hasSize(2);
                assertThat(summaries).extracting(MonthlySummary::getMonth).containsExactlyInAnyOrder(1, 2);
                assertThat(summaries).extracting(MonthlySummary::getTotalDuration).containsExactlyInAnyOrder(120, 180);
        }

        @Test
        @DisplayName("Should return empty list when finding by non-existent trainer username")
        void findByTrainerUsername_ShouldReturnEmptyList_WhenTrainerDoesNotExist() {
                List<MonthlySummary> summaries = monthlySummaryRepository.findByTrainer_Username("nonexistent");

                assertThat(summaries).isEmpty();
        }

        @Test
        @DisplayName("Should find monthly summaries by trainer username and year")
        void findByTrainerUsernameAndYear_ShouldReturnSummariesForYear_WhenSummariesExist() {
                List<MonthlySummary> summaries = monthlySummaryRepository
                                .findByTrainer_UsernameAndYear("john.doe", 2024);

                assertThat(summaries).hasSize(2);
                assertThat(summaries).extracting(MonthlySummary::getYear).containsOnly(2024);
                assertThat(summaries).extracting(MonthlySummary::getMonth).containsExactlyInAnyOrder(1, 2);
        }

        @Test
        @DisplayName("Should return empty list when finding by trainer username and non-existent year")
        void findByTrainerUsernameAndYear_ShouldReturnEmptyList_WhenYearDoesNotExist() {
                List<MonthlySummary> summaries = monthlySummaryRepository
                                .findByTrainer_UsernameAndYear("john.doe", 2025);

                assertThat(summaries).isEmpty();
        }

        @Test
        @DisplayName("Should test monthly summary methods - addDuration and subtractDuration")
        void monthlySummaryMethods_ShouldWorkCorrectly() {
                MonthlySummary summary = monthlySummaryRepository
                                .findByTrainer_UsernameAndYearAndMonth("john.doe", 2024, 1)
                                .orElseThrow();

                summary.addDuration(30);
                assertThat(summary.getTotalDuration()).isEqualTo(150);

                summary.subtractDuration(50);
                assertThat(summary.getTotalDuration()).isEqualTo(100);

                summary.subtractDuration(200);
                assertThat(summary.getTotalDuration()).isEqualTo(0);
        }

        @Test
        @DisplayName("Should handle multiple trainers with same year and month")
        void findByTrainerUsernameAndYearAndMonth_ShouldReturnCorrectTrainerSummary() {
                Optional<MonthlySummary> trainer1Summary = monthlySummaryRepository
                                .findByTrainer_UsernameAndYearAndMonth("john.doe", 2024, 1);
                Optional<MonthlySummary> trainer2Summary = monthlySummaryRepository
                                .findByTrainer_UsernameAndYearAndMonth("jane.smith", 2024, 1);

                assertThat(trainer1Summary).isPresent();
                assertThat(trainer2Summary).isPresent();

                assertThat(trainer1Summary.get().getTrainer().getUsername()).isEqualTo("john.doe");
                assertThat(trainer1Summary.get().getTotalDuration()).isEqualTo(120);

                assertThat(trainer2Summary.get().getTrainer().getUsername()).isEqualTo("jane.smith");
                assertThat(trainer2Summary.get().getTotalDuration()).isEqualTo(90);
        }
}