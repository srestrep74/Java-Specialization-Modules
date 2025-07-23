package dev.sro.workload_service.mapper;

import dev.sro.workload_service.dtos.v1.request.TrainerWorkloadRequest;
import dev.sro.workload_service.dtos.v1.response.TrainerMonthlySummaryResponse;
import dev.sro.workload_service.entity.TrainerTrainingSummary;
import dev.sro.workload_service.entity.YearSummary;
import dev.sro.workload_service.entity.MonthSummary;
import dev.sro.workload_service.entity.enums.ActionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TrainerTrainingSummaryMapperTest {

    @Autowired
    private TrainerTrainingSummaryMapper mapper;

    @Test
    void shouldMapTrainerWorkloadRequestToTrainerTrainingSummary() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "trainer.username",
                "John",
                "Doe",
                true,
                LocalDate.of(2024, 3, 15),
                120,
                ActionType.ADD
        );

        TrainerTrainingSummary result = mapper.toTrainerTrainingSummary(request);

        assertNotNull(result);
        assertEquals("trainer.username", result.getTrainerUsername());
        assertEquals("John", result.getTrainerFirstName());
        assertEquals("Doe", result.getTrainerLastName());
        assertEquals(true, result.getTrainerStatus());
        assertNull(result.getId());
        assertNotNull(result.getYears());
        assertEquals(0, result.getYears().size());
        assertNull(result.getCreatedAt());
        assertNull(result.getUpdatedAt());
    }

    @Test
    void shouldMapTrainerWorkloadRequestWithInactiveTrainer() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "inactive.trainer",
                "Jane",
                "Smith",
                false,
                LocalDate.of(2024, 1, 10),
                60,
                ActionType.DELETE
        );

        TrainerTrainingSummary result = mapper.toTrainerTrainingSummary(request);

        assertNotNull(result);
        assertEquals("inactive.trainer", result.getTrainerUsername());
        assertEquals("Jane", result.getTrainerFirstName());
        assertEquals("Smith", result.getTrainerLastName());
        assertEquals(false, result.getTrainerStatus());
    }

    @Test
    void shouldMapMonthSummaryToMonthData() {
        MonthSummary monthSummary = MonthSummary.builder()
                .month(3)
                .trainingsSummaryDuration(120)
                .build();

        TrainerMonthlySummaryResponse.MonthData result = mapper.mapMonthSummary(monthSummary);

        assertNotNull(result);
        assertEquals(3, result.month());
        assertEquals(120, result.trainingsSummaryDuration());
    }

    @Test
    void shouldMapMonthSummaryWithZeroDuration() {
        MonthSummary monthSummary = MonthSummary.builder()
                .month(1)
                .trainingsSummaryDuration(0)
                .build();

        TrainerMonthlySummaryResponse.MonthData result = mapper.mapMonthSummary(monthSummary);

        assertNotNull(result);
        assertEquals(1, result.month());
        assertEquals(0, result.trainingsSummaryDuration());
    }

    @Test
    void shouldMapYearSummaryToYearData() {
        MonthSummary month1 = MonthSummary.builder()
                .month(1)
                .trainingsSummaryDuration(60)
                .build();
        MonthSummary month2 = MonthSummary.builder()
                .month(2)
                .trainingsSummaryDuration(90)
                .build();

        YearSummary yearSummary = YearSummary.builder()
                .year(2024)
                .months(Arrays.asList(month1, month2))
                .build();

        TrainerMonthlySummaryResponse.YearData result = mapper.mapYearSummary(yearSummary);

        assertNotNull(result);
        assertEquals(2024, result.year());
        assertEquals(2, result.months().size());
        assertTrue(result.months().stream().anyMatch(m -> m.month() == 1 && m.trainingsSummaryDuration() == 60));
        assertTrue(result.months().stream().anyMatch(m -> m.month() == 2 && m.trainingsSummaryDuration() == 90));
    }

    @Test
    void shouldMapYearSummaryWithEmptyMonths() {
        YearSummary yearSummary = YearSummary.builder()
                .year(2023)
                .months(Collections.emptyList())
                .build();

        TrainerMonthlySummaryResponse.YearData result = mapper.mapYearSummary(yearSummary);

        assertNotNull(result);
        assertEquals(2023, result.year());
        assertEquals(0, result.months().size());
    }

    @Test
    void shouldMapTrainerTrainingSummaryToResponse() {
        MonthSummary month1 = MonthSummary.builder()
                .month(1)
                .trainingsSummaryDuration(60)
                .build();
        MonthSummary month2 = MonthSummary.builder()
                .month(2)
                .trainingsSummaryDuration(90)
                .build();

        YearSummary year2024 = YearSummary.builder()
                .year(2024)
                .months(Arrays.asList(month1, month2))
                .build();

        YearSummary year2023 = YearSummary.builder()
                .year(2023)
                .months(Collections.singletonList(MonthSummary.builder()
                        .month(12)
                        .trainingsSummaryDuration(120)
                        .build()))
                .build();

        TrainerTrainingSummary trainerSummary = TrainerTrainingSummary.builder()
                .id("trainer-id")
                .trainerUsername("trainer.username")
                .trainerFirstName("John")
                .trainerLastName("Doe")
                .trainerStatus(true)
                .years(Arrays.asList(year2024, year2023))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        TrainerMonthlySummaryResponse result = mapper.toResponse(trainerSummary);

        assertNotNull(result);
        assertEquals("trainer.username", result.trainerUsername());
        assertEquals("John", result.trainerFirstName());
        assertEquals("Doe", result.trainerLastName());
        assertEquals(true, result.trainerStatus());
        assertEquals(2, result.years().size());

        TrainerMonthlySummaryResponse.YearData year2024Data = result.years().stream()
                .filter(year -> year.year() == 2024)
                .findFirst()
                .orElse(null);
        assertNotNull(year2024Data);
        assertEquals(2024, year2024Data.year());
        assertEquals(2, year2024Data.months().size());

        TrainerMonthlySummaryResponse.YearData year2023Data = result.years().stream()
                .filter(year -> year.year() == 2023)
                .findFirst()
                .orElse(null);
        assertNotNull(year2023Data);
        assertEquals(2023, year2023Data.year());
        assertEquals(1, year2023Data.months().size());
    }

    @Test
    void shouldMapTrainerTrainingSummaryToResponseForSpecificYear() {
        MonthSummary month1 = MonthSummary.builder()
                .month(1)
                .trainingsSummaryDuration(60)
                .build();
        MonthSummary month2 = MonthSummary.builder()
                .month(2)
                .trainingsSummaryDuration(90)
                .build();

        YearSummary year2024 = YearSummary.builder()
                .year(2024)
                .months(Arrays.asList(month1, month2))
                .build();

        YearSummary year2023 = YearSummary.builder()
                .year(2023)
                .months(Collections.singletonList(MonthSummary.builder()
                        .month(12)
                        .trainingsSummaryDuration(120)
                        .build()))
                .build();

        TrainerTrainingSummary trainerSummary = TrainerTrainingSummary.builder()
                .trainerUsername("trainer.username")
                .trainerFirstName("John")
                .trainerLastName("Doe")
                .trainerStatus(true)
                .years(Arrays.asList(year2024, year2023))
                .build();

        TrainerMonthlySummaryResponse result = mapper.toResponseForYear(trainerSummary, 2024);

        assertNotNull(result);
        assertEquals("trainer.username", result.trainerUsername());
        assertEquals("John", result.trainerFirstName());
        assertEquals("Doe", result.trainerLastName());
        assertEquals(true, result.trainerStatus());
        assertEquals(1, result.years().size());

        TrainerMonthlySummaryResponse.YearData yearData = result.years().get(0);
        assertEquals(2024, yearData.year());
        assertEquals(2, yearData.months().size());
    }

    @Test
    void shouldMapTrainerTrainingSummaryToResponseForNonExistentYear() {
        YearSummary year2024 = YearSummary.builder()
                .year(2024)
                .months(Collections.singletonList(MonthSummary.builder()
                        .month(1)
                        .trainingsSummaryDuration(60)
                        .build()))
                .build();

        TrainerTrainingSummary trainerSummary = TrainerTrainingSummary.builder()
                .trainerUsername("trainer.username")
                .trainerFirstName("John")
                .trainerLastName("Doe")
                .trainerStatus(true)
                .years(Collections.singletonList(year2024))
                .build();

        TrainerMonthlySummaryResponse result = mapper.toResponseForYear(trainerSummary, 2023);

        assertNotNull(result);
        assertEquals("trainer.username", result.trainerUsername());
        assertEquals("John", result.trainerFirstName());
        assertEquals("Doe", result.trainerLastName());
        assertEquals(true, result.trainerStatus());
        assertEquals(0, result.years().size());
    }

    @Test
    void shouldMapTrainerTrainingSummaryToResponseForYearAndMonth() {
        MonthSummary month1 = MonthSummary.builder()
                .month(1)
                .trainingsSummaryDuration(60)
                .build();
        MonthSummary month2 = MonthSummary.builder()
                .month(2)
                .trainingsSummaryDuration(90)
                .build();

        YearSummary year2024 = YearSummary.builder()
                .year(2024)
                .months(Arrays.asList(month1, month2))
                .build();

        TrainerTrainingSummary trainerSummary = TrainerTrainingSummary.builder()
                .trainerUsername("trainer.username")
                .trainerFirstName("John")
                .trainerLastName("Doe")
                .trainerStatus(true)
                .years(Collections.singletonList(year2024))
                .build();

        TrainerMonthlySummaryResponse result = mapper.toResponseForYearAndMonth(trainerSummary, 2024, 1);

        assertNotNull(result);
        assertEquals("trainer.username", result.trainerUsername());
        assertEquals("John", result.trainerFirstName());
        assertEquals("Doe", result.trainerLastName());
        assertEquals(true, result.trainerStatus());
        assertEquals(1, result.years().size());

        TrainerMonthlySummaryResponse.YearData yearData = result.years().get(0);
        assertEquals(2024, yearData.year());
        assertEquals(1, yearData.months().size());

        TrainerMonthlySummaryResponse.MonthData monthData = yearData.months().get(0);
        assertEquals(1, monthData.month());
        assertEquals(60, monthData.trainingsSummaryDuration());
    }

    @Test
    void shouldMapTrainerTrainingSummaryToResponseForNonExistentMonth() {
        YearSummary year2024 = YearSummary.builder()
                .year(2024)
                .months(Collections.singletonList(MonthSummary.builder()
                        .month(1)
                        .trainingsSummaryDuration(60)
                        .build()))
                .build();

        TrainerTrainingSummary trainerSummary = TrainerTrainingSummary.builder()
                .trainerUsername("trainer.username")
                .trainerFirstName("John")
                .trainerLastName("Doe")
                .trainerStatus(true)
                .years(Collections.singletonList(year2024))
                .build();

        TrainerMonthlySummaryResponse result = mapper.toResponseForYearAndMonth(trainerSummary, 2024, 2);

        assertNotNull(result);
        assertEquals("trainer.username", result.trainerUsername());
        assertEquals("John", result.trainerFirstName());
        assertEquals("Doe", result.trainerLastName());
        assertEquals(true, result.trainerStatus());
        assertEquals(1, result.years().size());

        TrainerMonthlySummaryResponse.YearData yearData = result.years().get(0);
        assertEquals(2024, yearData.year());
        assertEquals(0, yearData.months().size());
    }

    @Test
    void shouldMapEmptyYearsList() {
        TrainerTrainingSummary trainerSummary = TrainerTrainingSummary.builder()
                .trainerUsername("empty.trainer")
                .trainerFirstName("Empty")
                .trainerLastName("Trainer")
                .trainerStatus(true)
                .years(Collections.emptyList())
                .build();

        TrainerMonthlySummaryResponse result = mapper.toResponse(trainerSummary);

        assertNotNull(result);
        assertEquals("empty.trainer", result.trainerUsername());
        assertEquals("Empty", result.trainerFirstName());
        assertEquals("Trainer", result.trainerLastName());
        assertEquals(true, result.trainerStatus());
        assertEquals(0, result.years().size());
    }

    @Test
    void shouldMapYearsWithMultipleMonthsInSameYear() {
        MonthSummary month1 = MonthSummary.builder()
                .month(1)
                .trainingsSummaryDuration(30)
                .build();
        MonthSummary month3 = MonthSummary.builder()
                .month(3)
                .trainingsSummaryDuration(45)
                .build();
        MonthSummary month6 = MonthSummary.builder()
                .month(6)
                .trainingsSummaryDuration(60)
                .build();
        MonthSummary month12 = MonthSummary.builder()
                .month(12)
                .trainingsSummaryDuration(90)
                .build();

        YearSummary year2024 = YearSummary.builder()
                .year(2024)
                .months(Arrays.asList(month1, month3, month6, month12))
                .build();

        TrainerTrainingSummary trainerSummary = TrainerTrainingSummary.builder()
                .trainerUsername("multi.month.trainer")
                .trainerFirstName("Multi")
                .trainerLastName("Month")
                .trainerStatus(true)
                .years(Collections.singletonList(year2024))
                .build();

        TrainerMonthlySummaryResponse result = mapper.toResponse(trainerSummary);

        assertNotNull(result);
        assertEquals(1, result.years().size());

        TrainerMonthlySummaryResponse.YearData year = result.years().get(0);
        assertEquals(2024, year.year());
        assertEquals(4, year.months().size());

        List<TrainerMonthlySummaryResponse.MonthData> months = year.months();
        assertTrue(months.stream().anyMatch(m -> m.month() == 1 && m.trainingsSummaryDuration() == 30));
        assertTrue(months.stream().anyMatch(m -> m.month() == 3 && m.trainingsSummaryDuration() == 45));
        assertTrue(months.stream().anyMatch(m -> m.month() == 6 && m.trainingsSummaryDuration() == 60));
        assertTrue(months.stream().anyMatch(m -> m.month() == 12 && m.trainingsSummaryDuration() == 90));
    }

    @Test
    void shouldMapInactiveTrainerStatus() {
        TrainerTrainingSummary trainerSummary = TrainerTrainingSummary.builder()
                .trainerUsername("inactive.trainer")
                .trainerFirstName("Inactive")
                .trainerLastName("Trainer")
                .trainerStatus(false)
                .years(Collections.emptyList())
                .build();

        TrainerMonthlySummaryResponse result = mapper.toResponse(trainerSummary);

        assertNotNull(result);
        assertEquals("inactive.trainer", result.trainerUsername());
        assertEquals("Inactive", result.trainerFirstName());
        assertEquals("Trainer", result.trainerLastName());
        assertEquals(false, result.trainerStatus());
    }

    @Test
    void shouldMapMonthsList() {
        MonthSummary month1 = MonthSummary.builder()
                .month(1)
                .trainingsSummaryDuration(60)
                .build();
        MonthSummary month2 = MonthSummary.builder()
                .month(2)
                .trainingsSummaryDuration(90)
                .build();

        List<MonthSummary> months = Arrays.asList(month1, month2);

        List<TrainerMonthlySummaryResponse.MonthData> result = mapper.mapMonths(months);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).month());
        assertEquals(60, result.get(0).trainingsSummaryDuration());
        assertEquals(2, result.get(1).month());
        assertEquals(90, result.get(1).trainingsSummaryDuration());
    }

    @Test
    void shouldMapEmptyMonthsList() {
        List<MonthSummary> months = Collections.emptyList();

        List<TrainerMonthlySummaryResponse.MonthData> result = mapper.mapMonths(months);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void shouldMapYearsList() {
        MonthSummary month1 = MonthSummary.builder()
                .month(1)
                .trainingsSummaryDuration(60)
                .build();
        MonthSummary month2 = MonthSummary.builder()
                .month(2)
                .trainingsSummaryDuration(90)
                .build();

        YearSummary year2024 = YearSummary.builder()
                .year(2024)
                .months(Arrays.asList(month1, month2))
                .build();

        YearSummary year2023 = YearSummary.builder()
                .year(2023)
                .months(Collections.singletonList(MonthSummary.builder()
                        .month(12)
                        .trainingsSummaryDuration(120)
                        .build()))
                .build();

        List<YearSummary> years = Arrays.asList(year2024, year2023);

        List<TrainerMonthlySummaryResponse.YearData> result = mapper.mapYears(years);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(2024, result.get(0).year());
        assertEquals(2, result.get(0).months().size());
        assertEquals(2023, result.get(1).year());
        assertEquals(1, result.get(1).months().size());
    }

    @Test
    void shouldMapEmptyYearsListToEmptyResult() {
        List<YearSummary> years = Collections.emptyList();

        List<TrainerMonthlySummaryResponse.YearData> result = mapper.mapYears(years);

        assertNotNull(result);
        assertEquals(0, result.size());
    }
} 