package dev.sro.gym_service.cucumber.component.context;

import dev.sro.gym_service.dtos.v1.request.trainer.RegisterTrainerRequest;
import dev.sro.gym_service.dtos.v1.request.trainer.UpdateTrainerActivation;
import dev.sro.gym_service.dtos.v1.request.trainer.UpdateTrainerProfileRequest;
import dev.sro.gym_service.util.response.ApiStandardResponse;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TrainerTestContext {
    private String accessToken;
    private String currentUsername;
    private String currentPassword;
    private RegisterTrainerRequest currentRegistrationRequest;
    private UpdateTrainerProfileRequest currentUpdateRequest;
    private UpdateTrainerActivation currentActivationRequest;
    private ApiStandardResponse<?> lastResponse;
    private int lastResponseStatus;
    private Map<String, Object> testData = new HashMap<>();

    public void clear() {
        this.accessToken = null;
        this.currentUsername = null;
        this.currentPassword = null;
        this.currentRegistrationRequest = null;
        this.currentUpdateRequest = null;
        this.currentActivationRequest = null;
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

    public void setCurrentCredentials(String username, String password) {
        this.currentUsername = username;
        this.currentPassword = password;
    }

    public String getCurrentUsername() {
        return currentUsername;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public RegisterTrainerRequest getCurrentRegistrationRequest() {
        return currentRegistrationRequest;
    }

    public void setCurrentRegistrationRequest(RegisterTrainerRequest request) {
        this.currentRegistrationRequest = request;
    }

    public UpdateTrainerProfileRequest getCurrentUpdateRequest() {
        return currentUpdateRequest;
    }

    public void setCurrentUpdateRequest(UpdateTrainerProfileRequest request) {
        this.currentUpdateRequest = request;
    }

    public UpdateTrainerActivation getCurrentActivationRequest() {
        return currentActivationRequest;
    }

    public void setCurrentActivationRequest(UpdateTrainerActivation request) {
        this.currentActivationRequest = request;
    }

    public ApiStandardResponse<?> getLastResponse() {
        return lastResponse;
    }

    public void setLastResponse(ApiStandardResponse<?> response) {
        this.lastResponse = response;
    }

    public int getLastResponseStatus() {
        return lastResponseStatus;
    }

    public void setLastResponseStatus(int status) {
        this.lastResponseStatus = status;
    }

    public void setTestData(String key, Object value) {
        this.testData.put(key, value);
    }

    public Object getTestData(String key) {
        return this.testData.get(key);
    }

    public RegisterTrainerRequest createValidRegistrationRequest() {
        return new RegisterTrainerRequest(
                "Test",
                "Trainer",
                1L);
    }

    public UpdateTrainerProfileRequest createValidUpdateRequest() {
        return new UpdateTrainerProfileRequest(
                "Test Updated",
                "Trainer Updated",
                1L,
                true);
    }

    public UpdateTrainerActivation createValidActivationRequest() {
        return new UpdateTrainerActivation(false);
    }
}