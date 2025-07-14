package dev.sro.workload_service.mapper;

import dev.sro.workload_service.dtos.v1.response.TrainerMonthlySummaryResponse;
import dev.sro.workload_service.entity.MonthlySummary;
import dev.sro.workload_service.entity.Trainer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TrainerMonthlySummaryMapperTest {

        private final TrainerMonthlySummaryMapper mapper = TrainerMonthlySummaryMapper.INSTANCE;

        @Test
        void shouldMapMonthlySummaryToMonthSummary() {
                MonthlySummary monthlySummary = MonthlySummary.builder()
                                .id(1L)
                                .year(2024)
                                .month(3)
                                .totalDuration(120)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();

                TrainerMonthlySummaryResponse.MonthSummary result = mapper.toMonthSummary(monthlySummary);

                assertNotNull(result);
                assertEquals(3, result.month());
                assertEquals(120, result.trainingSummaryDuration());
        }

        @Test
        void shouldMapMonthlySummaryWithZeroDuration() {
                MonthlySummary monthlySummary = MonthlySummary.builder()
                                .id(2L)
                                .year(2024)
                                .month(1)
                                .totalDuration(0)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();

                TrainerMonthlySummaryResponse.MonthSummary result = mapper.toMonthSummary(monthlySummary);

                assertNotNull(result);
                assertEquals(1, result.month());
                assertEquals(0, result.trainingSummaryDuration());
        }

        @Test
        void shouldReturnNullWhenMonthlySummaryIsNull() {
                MonthlySummary monthlySummary = null;

                TrainerMonthlySummaryResponse.MonthSummary result = mapper.toMonthSummary(monthlySummary);

                assertNull(result);
        }

        @Test
        void shouldMapTrainerAndSummariesToResponse() {
                Trainer trainer = Trainer.builder()
                                .username("trainer.username")
                                .firstName("John")
                                .lastName("Doe")
                                .isActive(true)
                                .build();

                MonthlySummary summary1 = MonthlySummary.builder()
                                .id(1L)
                                .year(2024)
                                .month(1)
                                .totalDuration(60)
                                .trainer(trainer)
                                .build();

                MonthlySummary summary2 = MonthlySummary.builder()
                                .id(2L)
                                .year(2024)
                                .month(2)
                                .totalDuration(90)
                                .trainer(trainer)
                                .build();

                MonthlySummary summary3 = MonthlySummary.builder()
                                .id(3L)
                                .year(2023)
                                .month(12)
                                .totalDuration(120)
                                .trainer(trainer)
                                .build();

                List<MonthlySummary> summaries = Arrays.asList(summary1, summary2, summary3);

                TrainerMonthlySummaryResponse result = mapper.toResponse(trainer, summaries);

                assertNotNull(result);
                assertEquals("trainer.username", result.trainerUsername());
                assertEquals("John", result.trainerFirstName());
                assertEquals("Doe", result.trainerLastName());
                assertEquals(true, result.trainerStatus());
                assertEquals(2, result.years().size());

                TrainerMonthlySummaryResponse.YearSummary year2024 = result.years().stream()
                                .filter(year -> year.year() == 2024)
                                .findFirst()
                                .orElse(null);
                assertNotNull(year2024);
                assertEquals(2024, year2024.year());
                assertEquals(2, year2024.months().size());

                TrainerMonthlySummaryResponse.YearSummary year2023 = result.years().stream()
                                .filter(year -> year.year() == 2023)
                                .findFirst()
                                .orElse(null);
                assertNotNull(year2023);
                assertEquals(2023, year2023.year());
                assertEquals(1, year2023.months().size());
        }

        @Test
        void shouldMapTrainerAndSummariesToResponseWithSingleYear() {
                Trainer trainer = Trainer.builder()
                                .username("single.year.trainer")
                                .firstName("Jane")
                                .lastName("Smith")
                                .isActive(false)
                                .build();

                MonthlySummary summary1 = MonthlySummary.builder()
                                .id(1L)
                                .year(2024)
                                .month(3)
                                .totalDuration(45)
                                .trainer(trainer)
                                .build();

                MonthlySummary summary2 = MonthlySummary.builder()
                                .id(2L)
                                .year(2024)
                                .month(4)
                                .totalDuration(75)
                                .trainer(trainer)
                                .build();

                List<MonthlySummary> summaries = Arrays.asList(summary1, summary2);

                TrainerMonthlySummaryResponse result = mapper.toResponse(trainer, summaries);

                assertNotNull(result);
                assertEquals("single.year.trainer", result.trainerUsername());
                assertEquals("Jane", result.trainerFirstName());
                assertEquals("Smith", result.trainerLastName());
                assertEquals(false, result.trainerStatus());
                assertEquals(1, result.years().size());

                TrainerMonthlySummaryResponse.YearSummary year = result.years().get(0);
                assertEquals(2024, year.year());
                assertEquals(2, year.months().size());
        }

        @Test
        void shouldMapTrainerAndEmptySummariesToResponse() {
                Trainer trainer = Trainer.builder()
                                .username("empty.trainer")
                                .firstName("Empty")
                                .lastName("Trainer")
                                .isActive(true)
                                .build();

                List<MonthlySummary> summaries = Collections.emptyList();

                TrainerMonthlySummaryResponse result = mapper.toResponse(trainer, summaries);

                assertNotNull(result);
                assertEquals("empty.trainer", result.trainerUsername());
                assertEquals("Empty", result.trainerFirstName());
                assertEquals("Trainer", result.trainerLastName());
                assertEquals(true, result.trainerStatus());
                assertEquals(0, result.years().size());
        }

        @Test
        void shouldMapTrainerAndSummariesToResponseWithMultipleMonthsInSameYear() {
                Trainer trainer = Trainer.builder()
                                .username("multi.month.trainer")
                                .firstName("Multi")
                                .lastName("Month")
                                .isActive(true)
                                .build();

                MonthlySummary summary1 = MonthlySummary.builder()
                                .id(1L)
                                .year(2024)
                                .month(1)
                                .totalDuration(30)
                                .trainer(trainer)
                                .build();

                MonthlySummary summary2 = MonthlySummary.builder()
                                .id(2L)
                                .year(2024)
                                .month(3)
                                .totalDuration(45)
                                .trainer(trainer)
                                .build();

                MonthlySummary summary3 = MonthlySummary.builder()
                                .id(3L)
                                .year(2024)
                                .month(6)
                                .totalDuration(60)
                                .trainer(trainer)
                                .build();

                MonthlySummary summary4 = MonthlySummary.builder()
                                .id(4L)
                                .year(2024)
                                .month(12)
                                .totalDuration(90)
                                .trainer(trainer)
                                .build();

                List<MonthlySummary> summaries = Arrays.asList(summary1, summary2, summary3, summary4);

                TrainerMonthlySummaryResponse result = mapper.toResponse(trainer, summaries);

                assertNotNull(result);
                assertEquals(1, result.years().size());

                TrainerMonthlySummaryResponse.YearSummary year = result.years().get(0);
                assertEquals(2024, year.year());
                assertEquals(4, year.months().size());

                List<TrainerMonthlySummaryResponse.MonthSummary> months = year.months();
                assertTrue(months.stream().anyMatch(m -> m.month() == 1 && m.trainingSummaryDuration() == 30));
                assertTrue(months.stream().anyMatch(m -> m.month() == 3 && m.trainingSummaryDuration() == 45));
                assertTrue(months.stream().anyMatch(m -> m.month() == 6 && m.trainingSummaryDuration() == 60));
                assertTrue(months.stream().anyMatch(m -> m.month() == 12 && m.trainingSummaryDuration() == 90));
        }
}