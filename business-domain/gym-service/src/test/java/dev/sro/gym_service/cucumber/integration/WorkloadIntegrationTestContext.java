package dev.sro.gym_service.cucumber.integration;

import dev.sro.gym_service.dtos.v1.request.training.CreateTrainingRequest;
import dev.sro.gym_service.dtos.v1.request.workload.TrainerWorkloadRequest;
import dev.sro.gym_service.dtos.v1.response.workload.TrainerWorkloadResponse;
import dev.sro.gym_service.entity.Training;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class WorkloadIntegrationTestContext {
    
    private CreateTrainingRequest currentTrainingRequest;
    private TrainerWorkloadResponse currentResponse;
    private Training currentTraining;
    private Exception currentException;
    private boolean activeMQUnavailable = false;
    private Map<String, Object> testData = new HashMap<>();

    public void clear() {
        this.currentTrainingRequest = null;
        this.currentResponse = null;
        this.currentTraining = null;
        this.currentException = null;
        this.activeMQUnavailable = false;
        this.testData.clear();
    }

    public CreateTrainingRequest getCurrentTrainingRequest() {
        return currentTrainingRequest;
    }

    public void setCurrentTrainingRequest(CreateTrainingRequest currentTrainingRequest) {
        this.currentTrainingRequest = currentTrainingRequest;
    }

    public TrainerWorkloadResponse getCurrentResponse() {
        return currentResponse;
    }

    public void setCurrentResponse(TrainerWorkloadResponse currentResponse) {
        this.currentResponse = currentResponse;
    }

    public Training getCurrentTraining() {
        return currentTraining;
    }

    public void setCurrentTraining(Training currentTraining) {
        this.currentTraining = currentTraining;
    }

    public Exception getCurrentException() {
        return currentException;
    }

    public void setCurrentException(Exception currentException) {
        this.currentException = currentException;
    }

    public boolean isActiveMQUnavailable() {
        return activeMQUnavailable;
    }

    public void setActiveMQUnavailable(boolean activeMQUnavailable) {
        this.activeMQUnavailable = activeMQUnavailable;
    }

    public void setTestData(String key, Object value) {
        this.testData.put(key, value);
    }

    public Object getTestData(String key) {
        return this.testData.get(key);
    }

    public <T> T getTestData(String key, Class<T> type) {
        return type.cast(this.testData.get(key));
    }
} 