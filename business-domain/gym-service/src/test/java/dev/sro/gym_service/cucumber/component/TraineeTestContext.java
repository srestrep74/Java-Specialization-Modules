package dev.sro.gym_service.cucumber.component;

import dev.sro.gym_service.dtos.v1.request.auth.LoginRequest;
import dev.sro.gym_service.dtos.v1.request.trainee.RegisterTraineeRequest;
import dev.sro.gym_service.dtos.v1.request.trainee.UpdateTraineeProfileRequest;
import dev.sro.gym_service.util.response.ApiStandardResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Component
public class TraineeTestContext {

    private String accessToken;
    private String currentUsername;
    private String currentPassword;
    private RegisterTraineeRequest currentRegistrationRequest;
    private UpdateTraineeProfileRequest currentUpdateRequest;
    private ApiStandardResponse<?> lastResponse;
    private int lastResponseStatus;
    private Map<String, Object> testData = new HashMap<>();

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public boolean hasValidToken() {
        return accessToken != null && !accessToken.isEmpty();
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

    public void setCurrentRegistrationRequest(RegisterTraineeRequest request) {
        this.currentRegistrationRequest = request;
    }

    public RegisterTraineeRequest getCurrentRegistrationRequest() {
        return currentRegistrationRequest;
    }

    public void setCurrentUpdateRequest(UpdateTraineeProfileRequest request) {
        this.currentUpdateRequest = request;
    }

    public UpdateTraineeProfileRequest getCurrentUpdateRequest() {
        return currentUpdateRequest;
    }

    public void setLastResponse(ApiStandardResponse<?> response) {
        this.lastResponse = response;
    }

    public ApiStandardResponse<?> getLastResponse() {
        return lastResponse;
    }

    public void setLastResponseStatus(int status) {
        this.lastResponseStatus = status;
    }

    public int getLastResponseStatus() {
        return lastResponseStatus;
    }

    public void setTestData(String key, Object value) {
        testData.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getTestData(String key) {
        return (T) testData.get(key);
    }

    public RegisterTraineeRequest createValidRegistrationRequest() {
        return new RegisterTraineeRequest(
                "John",
                "Doe",
                LocalDate.of(1990, 5, 15),
                "123 Main St, New York");
    }

    public RegisterTraineeRequest createInvalidRegistrationRequest() {
        return new RegisterTraineeRequest(
                "",
                "",
                null,
                "");
    }

    public UpdateTraineeProfileRequest createValidUpdateRequest() {
        return new UpdateTraineeProfileRequest(
                "John Updated",
                "Doe Updated",
                LocalDate.of(1991, 6, 16),
                "456 Updated St, Boston",
                true);
    }

    public LoginRequest createLoginRequest() {
        return new LoginRequest(currentUsername, currentPassword);
    }

    public void clear() {
        accessToken = null;
        currentUsername = null;
        currentPassword = null;
        currentRegistrationRequest = null;
        currentUpdateRequest = null;
        lastResponse = null;
        lastResponseStatus = 0;
        testData.clear();
    }
}