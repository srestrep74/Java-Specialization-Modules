package dev.sro.workload_service.service.impl;

import dev.sro.workload_service.repository.TrainerTrainingSummaryRepository;
import dev.sro.workload_service.repository.TrainingSessionRepository;
import dev.sro.workload_service.dtos.v1.request.TrainerWorkloadRequest;
import dev.sro.workload_service.dtos.v1.response.TrainerMonthlySummaryResponse;
import dev.sro.workload_service.entity.TrainerTrainingSummary;
import dev.sro.workload_service.entity.TrainingSession;
import dev.sro.workload_service.entity.YearSummary;
import dev.sro.workload_service.entity.MonthSummary;
import dev.sro.workload_service.entity.enums.ActionType;
import dev.sro.workload_service.exception.InvalidWorkloadDataException;
import dev.sro.workload_service.exception.TrainerNotFoundException;
import dev.sro.workload_service.exception.WorkloadProcessingException;
import dev.sro.workload_service.mapper.TrainerTrainingSummaryMapper;
import dev.sro.workload_service.mapper.TrainingSessionMapper;
import dev.sro.workload_service.service.TrainerWorkloadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class TrainerWorkloadServiceImpl implements TrainerWorkloadService {

    private final TrainerTrainingSummaryRepository trainerTrainingSummaryRepository;
    private final TrainingSessionRepository trainingSessionRepository;
    private final TrainerTrainingSummaryMapper trainerTrainingSummaryMapper;
    private final TrainingSessionMapper trainingSessionMapper;

    @Override
    public void processTrainerWorkload(TrainerWorkloadRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("TrainerWorkloadRequest cannot be null");
        }

        validateWorkloadRequest(request);

        try {
            TrainerTrainingSummary trainerSummary = getOrCreateTrainerSummary(request);
            updateTrainingSummary(trainerSummary, request);
            
            saveTrainingSession(request);
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
            TrainerTrainingSummary trainerSummary = trainerTrainingSummaryRepository
                    .findByTrainerUsername(trainerUsername)
                    .orElseThrow(() -> new TrainerNotFoundException(trainerUsername));

            return trainerTrainingSummaryMapper.toResponse(trainerSummary);
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
            TrainerTrainingSummary trainerSummary = trainerTrainingSummaryRepository
                    .findByTrainerUsername(trainerUsername)
                    .orElseThrow(() -> new TrainerNotFoundException(trainerUsername));

            return trainerTrainingSummaryMapper.toResponseForYear(trainerSummary, year);
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
            TrainerTrainingSummary trainerSummary = trainerTrainingSummaryRepository
                    .findByTrainerUsername(trainerUsername)
                    .orElseThrow(() -> new TrainerNotFoundException(trainerUsername));

            return trainerTrainingSummaryMapper.toResponseForYearAndMonth(trainerSummary, year, month);
        } catch (TrainerNotFoundException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new WorkloadProcessingException(trainerUsername, "getTrainerMonthlySummary(year,month)", e);
        }
    }

    private TrainerTrainingSummary getOrCreateTrainerSummary(TrainerWorkloadRequest request) {
        try {
            return trainerTrainingSummaryRepository.findByTrainerUsername(request.trainerUsername())
                    .map(trainerSummary -> {
                        trainerSummary.updateProfile(
                                request.trainerFirstName(),
                                request.trainerLastName(),
                                request.isActive());
                        trainerSummary.setUpdatedAt(LocalDateTime.now());
                        return trainerTrainingSummaryRepository.save(trainerSummary);
                    })
                    .orElseGet(() -> {
                        TrainerTrainingSummary newTrainerSummary = trainerTrainingSummaryMapper
                                .toTrainerTrainingSummary(request);
                        newTrainerSummary.setCreatedAt(LocalDateTime.now());
                        newTrainerSummary.setUpdatedAt(LocalDateTime.now());
                        return trainerTrainingSummaryRepository.save(newTrainerSummary);
                    });
        } catch (Exception e) {
            throw new WorkloadProcessingException(request.trainerUsername(), "getOrCreateTrainerSummary", e);
        }
    }

    private void updateTrainingSummary(TrainerTrainingSummary trainerSummary, TrainerWorkloadRequest request) {
        try {
            Integer year = request.trainingDate().getYear();
            Integer month = request.trainingDate().getMonthValue();

            YearSummary yearSummary = trainerSummary.findOrCreateYear(year);
            MonthSummary monthSummary = yearSummary.findOrCreateMonth(month);

            if (request.actionType() == ActionType.ADD) {
                monthSummary.addDuration(request.trainingDuration());
            } else if (request.actionType() == ActionType.DELETE) {
                monthSummary.subtractDuration(request.trainingDuration());
            } else if (request.actionType() == ActionType.UPDATE) {
                updateExistingTrainingSession(trainerSummary, request, monthSummary);
            }

            trainerSummary.setUpdatedAt(LocalDateTime.now());
            trainerTrainingSummaryRepository.save(trainerSummary);
        } catch (Exception e) {
            throw new WorkloadProcessingException(trainerSummary.getTrainerUsername(), "updateTrainingSummary", e);
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

        if (request.trainingDuration() != null && request.trainingDuration() < 0) {
            throw new IllegalArgumentException("Training duration must be positive");
        }
    }

    private void saveTrainingSession(TrainerWorkloadRequest request) {
        TrainingSession trainingSession = trainingSessionMapper.toTrainingSession(request);
        trainingSession.setCreatedAt(LocalDateTime.now());
        trainingSession.setUpdatedAt(LocalDateTime.now());
        trainingSessionRepository.save(trainingSession);
    }

    private void updateExistingTrainingSession(TrainerTrainingSummary trainerSummary, TrainerWorkloadRequest request, MonthSummary monthSummary) {
        Optional<TrainingSession> existingSession = trainingSessionRepository
                .findByTrainerUsernameAndTrainingDate(request.trainerUsername(), request.trainingDate());
        
        if (existingSession.isPresent()) {
            Integer previousDuration = existingSession.get().getTrainingDuration();
            
            Integer newDuration = monthSummary.getTrainingsSummaryDuration() - previousDuration + request.trainingDuration();
            
            monthSummary.setTrainingsSummaryDuration(newDuration);
        } else {
            monthSummary.setTrainingsSummaryDuration(request.trainingDuration());
        }
    }
}