package dev.sro.workload_service.cucumber.component;

import dev.sro.workload_service.dtos.v1.request.TrainerWorkloadRequest;
import dev.sro.workload_service.dtos.v1.response.TrainerWorkloadResponse;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class WorkloadTestContext {

    private String accessToken;
    private String username;
    private String password;
    private TrainerWorkloadRequest currentWorkloadRequest;
    private TrainerWorkloadResponse lastResponse;
    private int lastResponseStatus;
    private final Map<String, Object> testData = new HashMap<>();

    public void clear() {
        this.accessToken = null;
        this.username = null;
        this.password = null;
        this.currentWorkloadRequest = null;
        this.lastResponse = null;
        this.lastResponseStatus = 0;
        this.testData.clear();
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCurrentCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public TrainerWorkloadRequest getCurrentWorkloadRequest() {
        return currentWorkloadRequest;
    }

    public void setCurrentWorkloadRequest(TrainerWorkloadRequest currentWorkloadRequest) {
        this.currentWorkloadRequest = currentWorkloadRequest;
    }

    public TrainerWorkloadResponse getLastResponse() {
        return lastResponse;
    }

    public void setLastResponse(TrainerWorkloadResponse lastResponse) {
        this.lastResponse = lastResponse;
    }

    public int getLastResponseStatus() {
        return lastResponseStatus;
    }

    public void setLastResponseStatus(int lastResponseStatus) {
        this.lastResponseStatus = lastResponseStatus;
    }

    public Object getTestData(String key) {
        return testData.get(key);
    }

    public void setTestData(String key, Object value) {
        this.testData.put(key, value);
    }

    public TrainerWorkloadRequest createValidAddWorkloadRequest() {
        return new TrainerWorkloadRequest(
                "test.trainer",
                "Test",
                "Trainer",
                true,
                java.time.LocalDate.now(),
                60,
                dev.sro.workload_service.entity.enums.ActionType.ADD);
    }

    public TrainerWorkloadRequest createValidUpdateWorkloadRequest() {
        return new TrainerWorkloadRequest(
                "test.trainer",
                "Test",
                "Trainer",
                true,
                java.time.LocalDate.now(),
                90,
                dev.sro.workload_service.entity.enums.ActionType.UPDATE);
    }

    public TrainerWorkloadRequest createValidDeleteWorkloadRequest() {
        return new TrainerWorkloadRequest(
                "test.trainer",
                "Test",
                "Trainer",
                true,
                java.time.LocalDate.now(),
                60,
                dev.sro.workload_service.entity.enums.ActionType.DELETE);
    }
}