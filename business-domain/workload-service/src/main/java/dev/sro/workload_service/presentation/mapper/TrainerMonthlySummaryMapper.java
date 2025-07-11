package dev.sro.workload_service.presentation.mapper;

import dev.sro.workload_service.domain.entity.MonthlySummary;
import dev.sro.workload_service.domain.entity.Trainer;
import dev.sro.workload_service.presentation.dto.response.TrainerMonthlySummaryResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface TrainerMonthlySummaryMapper {

    @Mapping(target = "trainingSummaryDuration", source = "totalDuration")
    TrainerMonthlySummaryResponse.MonthSummary toMonthSummary(MonthlySummary summary);

    default TrainerMonthlySummaryResponse toResponse(Trainer trainer, List<MonthlySummary> summaries) {
        Map<Integer, List<MonthlySummary>> summariesByYear = summaries.stream()
                .collect(Collectors.groupingBy(MonthlySummary::getYear));

        List<TrainerMonthlySummaryResponse.YearSummary> yearSummaries = summariesByYear.entrySet()
                .stream()
                .map(entry -> new TrainerMonthlySummaryResponse.YearSummary(
                        entry.getKey(),
                        entry.getValue().stream().map(this::toMonthSummary).collect(Collectors.toList())
                ))
                .collect(Collectors.toList());

        return new TrainerMonthlySummaryResponse(
                trainer.getUsername(),
                trainer.getFirstName(),
                trainer.getLastName(),
                trainer.getIsActive(),
                yearSummaries
        );
    }
} 