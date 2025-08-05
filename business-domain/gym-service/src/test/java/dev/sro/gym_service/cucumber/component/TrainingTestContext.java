package dev.sro.gym_service.cucumber.component;

import dev.sro.gym_service.dtos.v1.request.training.CreateTrainingRequest;
import dev.sro.gym_service.dtos.v1.request.training.DeleteTrainingRequest;
import dev.sro.gym_service.dtos.v1.request.training.UpdateTrainingRequest;
import dev.sro.gym_service.util.response.ApiStandardResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Component
public class TrainingTestContext {
    private String accessToken;
    private String currentUsername;
    private String currentPassword;
    private CreateTrainingRequest currentCreateRequest;
    private UpdateTrainingRequest currentUpdateRequest;
    private DeleteTrainingRequest currentDeleteRequest;
    private ApiStandardResponse<?> lastResponse;
    private int lastResponseStatus;
    private Map<String, Object> testData = new HashMap<>();

    public void clear() {
        this.accessToken = null;
        this.currentUsername = null;
        this.currentPassword = null;
        this.currentCreateRequest = null;
        this.currentUpdateRequest = null;
        this.currentDeleteRequest = null;
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

    public CreateTrainingRequest getCurrentCreateRequest() {
        return currentCreateRequest;
    }

    public void setCurrentCreateRequest(CreateTrainingRequest request) {
        this.currentCreateRequest = request;
    }

    public UpdateTrainingRequest getCurrentUpdateRequest() {
        return currentUpdateRequest;
    }

    public void setCurrentUpdateRequest(UpdateTrainingRequest request) {
        this.currentUpdateRequest = request;
    }

    public DeleteTrainingRequest getCurrentDeleteRequest() {
        return currentDeleteRequest;
    }

    public void setCurrentDeleteRequest(DeleteTrainingRequest request) {
        this.currentDeleteRequest = request;
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

    public CreateTrainingRequest createValidCreateRequest() {
        return new CreateTrainingRequest(
                "test.trainee",
                "test.trainer",
                "Zumba",
                LocalDate.now(),
                60);
    }

    public UpdateTrainingRequest createValidUpdateRequest() {
        return new UpdateTrainingRequest(
                "Zumba Updated",
                LocalDate.now(),
                90,
                "test.trainer",
                "test.trainee",
                "Zumba");
    }

    public DeleteTrainingRequest createValidDeleteRequest() {
        return new DeleteTrainingRequest(
                "test.trainee",
                "test.trainer",
                LocalDate.now());
    }
}