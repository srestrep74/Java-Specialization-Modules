package dev.sro.workload_service.service.impl;

import dev.sro.workload_service.repository.MonthlySummaryRepository;
import dev.sro.workload_service.repository.TrainerRepository;
import dev.sro.workload_service.repository.TrainingSessionRepository;
import dev.sro.workload_service.dtos.v1.request.TrainerWorkloadRequest;
import dev.sro.workload_service.dtos.v1.response.TrainerMonthlySummaryResponse;
import dev.sro.workload_service.entity.MonthlySummary;
import dev.sro.workload_service.entity.Trainer;
import dev.sro.workload_service.entity.TrainingSession;
import dev.sro.workload_service.entity.enums.ActionType;
import dev.sro.workload_service.exception.InvalidWorkloadDataException;
import dev.sro.workload_service.exception.TrainerNotFoundException;
import dev.sro.workload_service.exception.WorkloadProcessingException;
import dev.sro.workload_service.mapper.TrainerMapper;
import dev.sro.workload_service.mapper.TrainerMonthlySummaryMapper;
import dev.sro.workload_service.mapper.TrainingSessionMapper;
import dev.sro.workload_service.service.TrainerWorkloadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class TrainerWorkloadServiceImpl implements TrainerWorkloadService {

    private final TrainerRepository trainerRepository;
    private final TrainingSessionRepository trainingSessionRepository;
    private final MonthlySummaryRepository monthlySummaryRepository;
    private final TrainerMapper trainerMapper;
    private final TrainingSessionMapper trainingSessionMapper;
    private final TrainerMonthlySummaryMapper trainerMonthlySummaryMapper;

    @Override
    public void processTrainerWorkload(TrainerWorkloadRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("TrainerWorkloadRequest cannot be null");
        }

        validateWorkloadRequest(request);

        try {
            Trainer trainer = getOrCreateTrainer(request);

            TrainingSession trainingSession = trainingSessionMapper.toTrainingSession(request);
            trainingSession.setTrainer(trainer);
            trainingSessionRepository.save(trainingSession);

            updateMonthlySummary(trainer, request);
        } catch (InvalidWorkloadDataException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new WorkloadProcessingException(request.trainerUsername(), "processTrainerWorkload", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public TrainerMonthlySummaryResponse getTrainerMonthlySummary(String trainerUsername) {
        if (trainerUsername == null || trainerUsername.trim().isEmpty()) {
            throw new IllegalArgumentException("Trainer username cannot be null or empty");
        }

        try {
            Trainer trainer = trainerRepository.findByUsername(trainerUsername)
                    .orElseThrow(() -> new TrainerNotFoundException(trainerUsername));

            List<MonthlySummary> summaries = monthlySummaryRepository.findByTrainer_Username(trainerUsername);

            return trainerMonthlySummaryMapper.toResponse(trainer, summaries);
        } catch (TrainerNotFoundException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new WorkloadProcessingException(trainerUsername, "getTrainerMonthlySummary", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public TrainerMonthlySummaryResponse getTrainerMonthlySummary(String trainerUsername, Integer year) {
        if (trainerUsername == null || trainerUsername.trim().isEmpty()) {
            throw new IllegalArgumentException("Trainer username cannot be null or empty");
        }
        if (year == null || year < 1900 || year > 2100) {
            throw new IllegalArgumentException("Year must be between 1900 and 2100");
        }

        try {
            Trainer trainer = trainerRepository.findByUsername(trainerUsername)
                    .orElseThrow(() -> new TrainerNotFoundException(trainerUsername));

            List<MonthlySummary> summaries = monthlySummaryRepository.findByTrainer_UsernameAndYear(trainerUsername,
                    year);

            return trainerMonthlySummaryMapper.toResponse(trainer, summaries);
        } catch (TrainerNotFoundException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new WorkloadProcessingException(trainerUsername, "getTrainerMonthlySummary(year)", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public TrainerMonthlySummaryResponse getTrainerMonthlySummary(String trainerUsername, Integer year, Integer month) {
        if (trainerUsername == null || trainerUsername.trim().isEmpty()) {
            throw new IllegalArgumentException("Trainer username cannot be null or empty");
        }
        if (year == null || year < 1900 || year > 2100) {
            throw new IllegalArgumentException("Year must be between 1900 and 2100");
        }
        if (month == null || month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }

        try {
            Trainer trainer = trainerRepository.findByUsername(trainerUsername)
                    .orElseThrow(() -> new TrainerNotFoundException(trainerUsername));

            Optional<MonthlySummary> summaryOpt = monthlySummaryRepository
                    .findByTrainer_UsernameAndYearAndMonth(trainerUsername, year, month);

            List<MonthlySummary> summaries = summaryOpt.map(List::of).orElse(List.of());
            return trainerMonthlySummaryMapper.toResponse(trainer, summaries);
        } catch (TrainerNotFoundException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new WorkloadProcessingException(trainerUsername, "getTrainerMonthlySummary(year,month)", e);
        }
    }

    private Trainer getOrCreateTrainer(TrainerWorkloadRequest request) {
        try {
            return trainerRepository.findByUsername(request.trainerUsername())
                    .map(trainer -> {
                        trainer.updateProfile(
                                request.trainerFirstName(),
                                request.trainerLastName(),
                                request.isActive());
                        return trainerRepository.save(trainer);
                    })
                    .orElseGet(() -> {
                        Trainer newTrainer = trainerMapper.toTrainer(request);
                        return trainerRepository.save(newTrainer);
                    });
        } catch (Exception e) {
            throw new WorkloadProcessingException(request.trainerUsername(), "getOrCreateTrainer", e);
        }
    }

    private void updateMonthlySummary(Trainer trainer, TrainerWorkloadRequest request) {
        try {
            Integer year = request.trainingDate().getYear();
            Integer month = request.trainingDate().getMonthValue();

            MonthlySummary summary = monthlySummaryRepository
                    .findByTrainer_UsernameAndYearAndMonth(trainer.getUsername(), year, month)
                    .orElseGet(() -> {
                        MonthlySummary newSummary = MonthlySummary.builder()
                                .trainer(trainer)
                                .year(year)
                                .month(month)
                                .totalDuration(0)
                                .build();
                        return monthlySummaryRepository.save(newSummary);
                    });

            if (request.actionType() == ActionType.ADD) {
                summary.addDuration(request.trainingDuration());
            } else if (request.actionType() == ActionType.DELETE) {
                summary.subtractDuration(request.trainingDuration());
            }

            monthlySummaryRepository.save(summary);
        } catch (Exception e) {
            throw new WorkloadProcessingException(trainer.getUsername(), "updateMonthlySummary", e);
        }
    }

    private void validateWorkloadRequest(TrainerWorkloadRequest request) {
        if (request.trainerUsername() == null || request.trainerUsername().trim().isEmpty()) {
            throw new InvalidWorkloadDataException("trainerUsername", "null/empty", "Trainer username is required");
        }

        if (request.trainingDuration() != null && request.trainingDuration() <= 0) {
            throw new InvalidWorkloadDataException("trainingDuration", request.trainingDuration().toString(),
                    "Training duration must be positive");
        }

        if (request.trainingDate() == null) {
            throw new InvalidWorkloadDataException("trainingDate", "null", "Training date is required");
        }

        if (request.actionType() == null) {
            throw new InvalidWorkloadDataException("actionType", "null", "Action type is required");
        }
    }

}