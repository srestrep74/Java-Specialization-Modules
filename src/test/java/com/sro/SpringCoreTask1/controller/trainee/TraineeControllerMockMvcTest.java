package com.sro.SpringCoreTask1.controller.trainee;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sro.SpringCoreTask1.dtos.v1.request.auth.LoginRequest;
import com.sro.SpringCoreTask1.dtos.v1.request.trainee.*;
import com.sro.SpringCoreTask1.dtos.v1.request.training.TraineeTrainingResponse;
import com.sro.SpringCoreTask1.dtos.v1.response.trainee.*;
import com.sro.SpringCoreTask1.util.response.ApiStandardResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TraineeControllerMockMvcTest {

    private static final String BASE_URL = "/api/v1/trainees";
    private static String createdTraineeUsername;
    private static String createdTraineePassword;

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Test
    @Order(1)
    void registerTrainee_ShouldCreateNewTrainee() throws Exception {
        RegisterTraineeRequest request = new RegisterTraineeRequest(
            "Sebas", "Rpo", LocalDate.of(1990, 5, 15), "123 Main St, New York");

        MvcResult result = mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status", is(201)))
            .andReturn();

        ApiStandardResponse<RegisterTraineeResponse> response = objectMapper.readValue(
            result.getResponse().getContentAsString(), 
            new TypeReference<ApiStandardResponse<RegisterTraineeResponse>>() {});
        
        assertNotNull(response);
        assertNotNull(response.data());
        assertNotNull(response.data().username());
        assertNotNull(response.data().password());
        
        createdTraineeUsername = response.data().username();
        createdTraineePassword = response.data().password();
        authenticate(createdTraineeUsername, createdTraineePassword);
    }

    @Test
    @Order(2)
    void getProfile_ShouldReturnTraineeProfile() throws Exception {
        MvcResult result = mockMvc.perform(get(BASE_URL + "/{username}", createdTraineeUsername))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status", is(200)))
            .andReturn();

        ApiStandardResponse<TraineeProfileResponse> apiResponse = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            new TypeReference<ApiStandardResponse<TraineeProfileResponse>>() {});
        
        TraineeProfileResponse profile = apiResponse.data();
        assertEquals("Sebas", profile.firstName());
        assertEquals("Rpo", profile.lastName());
        assertEquals(LocalDate.of(1990, 5, 15), profile.dateOfBirth());
        assertEquals("123 Main St, New York", profile.address());
        assertTrue(profile.active());
        assertNotNull(profile.trainers());
    }

    @Test
    @Order(3)
    void updateProfile_ShouldUpdateTraineeDetails() throws Exception {
        UpdateTraineeProfileRequest updateRequest = new UpdateTraineeProfileRequest(
            "Sebas Updated", "Rpo Updated", 
            LocalDate.of(1991, 6, 16), "456 Updated St, Boston", true);

        MvcResult result = mockMvc.perform(put(BASE_URL + "/{username}", createdTraineeUsername)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status", is(200)))
            .andReturn();

        ApiStandardResponse<TraineeProfileResponse> apiResponse = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            new TypeReference<ApiStandardResponse<TraineeProfileResponse>>() {});
        
        TraineeProfileResponse updatedProfile = apiResponse.data();
        assertEquals("Sebas Updated", updatedProfile.firstName());
        assertEquals("Rpo Updated", updatedProfile.lastName());
        assertEquals(LocalDate.of(1991, 6, 16), updatedProfile.dateOfBirth());
        assertEquals("456 Updated St, Boston", updatedProfile.address());
    }

    @Test
    @Order(4)
    void getTraineeTrainings_ShouldReturnTrainingList() throws Exception {
        MvcResult result = mockMvc.perform(get(BASE_URL + "/{username}/trainings", createdTraineeUsername)
                .param("fromDate", "2023-01-01")
                .param("toDate", "2023-12-31")
                .param("sortField", "trainingDate")
                .param("sortDirection", "DESC"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status", is(200)))
            .andReturn();

        ApiStandardResponse<List<TraineeTrainingResponse>> response = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            new TypeReference<ApiStandardResponse<List<TraineeTrainingResponse>>>() {});
        
        assertNotNull(response.data());
    }

    @Test
    @Order(5)
    void deleteProfile_ShouldRemoveTrainee() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/{username}", createdTraineeUsername))
            .andExpect(status().isOk());

        mockMvc.perform(get(BASE_URL + "/{username}", createdTraineeUsername))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    @Order(6)
    void registerTrainee_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new RegisterTraineeRequest("", "", null, ""))))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    @Order(7)
    void getProfile_WithNonExistentUsername_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get(BASE_URL + "/nonexistentuser"))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status", is(404)));
    }

    private void authenticate(String username, String password) throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new LoginRequest(username, password))))
            .andExpect(status().isOk());
    }
}