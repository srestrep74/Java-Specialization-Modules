package dev.sro.workload_service.mapper;

import dev.sro.workload_service.dtos.v1.request.TrainerWorkloadRequest;
import dev.sro.workload_service.dtos.v1.response.TrainerMonthlySummaryResponse;
import dev.sro.workload_service.entity.TrainerTrainingSummary;
import dev.sro.workload_service.entity.YearSummary;
import dev.sro.workload_service.entity.MonthSummary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface TrainerTrainingSummaryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "years", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "trainerUsername", source = "trainerUsername")
    @Mapping(target = "trainerFirstName", source = "trainerFirstName")
    @Mapping(target = "trainerLastName", source = "trainerLastName")
    @Mapping(target = "trainerStatus", source = "isActive")
    TrainerTrainingSummary toTrainerTrainingSummary(TrainerWorkloadRequest request);

    @Mapping(target = "trainerUsername", source = "trainerUsername")
    @Mapping(target = "trainerFirstName", source = "trainerFirstName")
    @Mapping(target = "trainerLastName", source = "trainerLastName")
    @Mapping(target = "trainerStatus", source = "trainerStatus")
    @Mapping(target = "years", expression = "java(mapYears(trainerSummary.getYears()))")
    TrainerMonthlySummaryResponse toResponse(TrainerTrainingSummary trainerSummary);

    @Mapping(target = "trainerUsername", source = "trainerSummary.trainerUsername")
    @Mapping(target = "trainerFirstName", source = "trainerSummary.trainerFirstName")
    @Mapping(target = "trainerLastName", source = "trainerSummary.trainerLastName")
    @Mapping(target = "trainerStatus", source = "trainerSummary.trainerStatus")
    @Mapping(target = "years", expression = "java(mapYearsForYear(trainerSummary.getYears(), year))")
    TrainerMonthlySummaryResponse toResponseForYear(TrainerTrainingSummary trainerSummary, Integer year);

    @Mapping(target = "trainerUsername", source = "trainerSummary.trainerUsername")
    @Mapping(target = "trainerFirstName", source = "trainerSummary.trainerFirstName")
    @Mapping(target = "trainerLastName", source = "trainerSummary.trainerLastName")
    @Mapping(target = "trainerStatus", source = "trainerSummary.trainerStatus")
    @Mapping(target = "years", expression = "java(mapYearsForYearAndMonth(trainerSummary.getYears(), year, month))")
    TrainerMonthlySummaryResponse toResponseForYearAndMonth(TrainerTrainingSummary trainerSummary, Integer year, Integer month);

    @Mapping(target = "year", source = "year")
    @Mapping(target = "months", expression = "java(mapMonths(yearSummary.getMonths()))")
    TrainerMonthlySummaryResponse.YearData mapYearSummary(YearSummary yearSummary);

    @Mapping(target = "month", source = "month")
    @Mapping(target = "trainingsSummaryDuration", source = "trainingsSummaryDuration")
    TrainerMonthlySummaryResponse.MonthData mapMonthSummary(MonthSummary monthSummary);

    default List<TrainerMonthlySummaryResponse.YearData> mapYears(List<YearSummary> years) {
        return years.stream()
                .map(this::mapYearSummary)
                .collect(Collectors.toList());
    }

    default List<TrainerMonthlySummaryResponse.YearData> mapYearsForYear(List<YearSummary> years, Integer year) {
        return years.stream()
                .filter(yearSummary -> yearSummary.getYear().equals(year))
                .map(this::mapYearSummary)
                .collect(Collectors.toList());
    }

    default List<TrainerMonthlySummaryResponse.YearData> mapYearsForYearAndMonth(List<YearSummary> years, Integer year, Integer month) {
        return years.stream()
                .filter(yearSummary -> yearSummary.getYear().equals(year))
                .map(yearSummary -> {
                    List<TrainerMonthlySummaryResponse.MonthData> monthDataList = yearSummary.getMonths().stream()
                            .filter(monthSummary -> monthSummary.getMonth().equals(month))
                            .map(this::mapMonthSummary)
                            .collect(Collectors.toList());

                    return TrainerMonthlySummaryResponse.YearData.builder()
                            .year(yearSummary.getYear())
                            .months(monthDataList)
                            .build();
                })
                .collect(Collectors.toList());
    }

    default List<TrainerMonthlySummaryResponse.MonthData> mapMonths(List<MonthSummary> months) {
        return months.stream()
                .map(this::mapMonthSummary)
                .collect(Collectors.toList());
    }
} 