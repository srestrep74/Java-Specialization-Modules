package dev.sro.workload_service.controller.v1;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sro.workload_service.dtos.v1.request.TrainerWorkloadRequest;
import dev.sro.workload_service.dtos.v1.response.TrainerMonthlySummaryResponse;
import dev.sro.workload_service.entity.enums.ActionType;
import dev.sro.workload_service.exception.TrainerNotFoundException;
import dev.sro.workload_service.service.TrainerWorkloadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TrainerWorkloadControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockitoBean
        private TrainerWorkloadService trainerWorkloadService;

        private TrainerWorkloadRequest validRequest;
        private TrainerMonthlySummaryResponse summaryResponse;

        @BeforeEach
        void setUp() {
                validRequest = new TrainerWorkloadRequest(
                                "test.trainer",
                                "Test",
                                "Trainer",
                                true,
                                LocalDate.of(2024, 7, 21),
                                60,
                                ActionType.ADD);

                TrainerMonthlySummaryResponse.MonthData monthData = TrainerMonthlySummaryResponse.MonthData.builder()
                                .month(7)
                                .trainingsSummaryDuration(300)
                                .build();

                TrainerMonthlySummaryResponse.YearData yearData = TrainerMonthlySummaryResponse.YearData.builder()
                                .year(2024)
                                .months(List.of(monthData))
                                .build();

                summaryResponse = TrainerMonthlySummaryResponse.builder()
                                .trainerUsername("test.trainer")
                                .trainerFirstName("Test")
                                .trainerLastName("Trainer")
                                .trainerStatus(true)
                                .years(List.of(yearData))
                                .build();
        }

        @Test
        @WithMockUser(roles = "TRAINER")
        void processTrainerWorkload_WithValidRequest_ShouldReturnOk() throws Exception {
                doNothing().when(trainerWorkloadService).processTrainerWorkload(any(TrainerWorkloadRequest.class));

                mockMvc.perform(post("/api/v1/workloads")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status", is(200)))
                                .andExpect(jsonPath("$.message", is("Workload processed successfully")));
        }

        @Test
        @WithMockUser(roles = "TRAINER")
        void processTrainerWorkload_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
                TrainerWorkloadRequest invalidRequest = new TrainerWorkloadRequest(
                                "", null, null, true, null, 0, null);

                mockMvc.perform(post("/api/v1/workloads")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void processTrainerWorkload_WithoutAuth_ShouldReturnUnauthorized() throws Exception {
                mockMvc.perform(post("/api/v1/workloads")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validRequest)))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void processTrainerWorkload_WithAdminRole_ShouldReturnOk() throws Exception {
                doNothing().when(trainerWorkloadService).processTrainerWorkload(any(TrainerWorkloadRequest.class));

                mockMvc.perform(post("/api/v1/workloads")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validRequest)))
                                .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = "USER")
        void processTrainerWorkload_WithWrongRole_ShouldReturnForbidden() throws Exception {
                mockMvc.perform(post("/api/v1/workloads")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(validRequest)))
                                .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "TRAINER")
        void getTrainerMonthlySummary_ShouldReturnSummary() throws Exception {
                when(trainerWorkloadService.getTrainerMonthlySummary(anyString())).thenReturn(summaryResponse);

                mockMvc.perform(get("/api/v1/workloads/trainers/{username}/monthly-summary", "test.trainer"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status", is(200)))
                                .andExpect(jsonPath("$.data.trainerFirstName", is("Test")));
        }

        @Test
        @WithMockUser(roles = "TRAINER")
        void getTrainerMonthlySummaryByYear_ShouldReturnSummary() throws Exception {
                when(trainerWorkloadService.getTrainerMonthlySummary(anyString(), anyInt()))
                                .thenReturn(summaryResponse);

                mockMvc.perform(get("/api/v1/workloads/trainers/{username}/monthly-summary/{year}", "test.trainer",
                                2024))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status", is(200)))
                                .andExpect(jsonPath("$.data.trainerLastName", is("Trainer")));
        }

        @Test
        @WithMockUser(roles = "TRAINER")
        void getTrainerMonthlySummaryByMonth_ShouldReturnSummary() throws Exception {
                when(trainerWorkloadService.getTrainerMonthlySummary(anyString(), anyInt(), anyInt()))
                                .thenReturn(summaryResponse);

                mockMvc.perform(get("/api/v1/workloads/trainers/{username}/monthly-summary/{year}/{month}",
                                "test.trainer", 2024, 7))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status", is(200)))
                                .andExpect(jsonPath("$.data.trainerStatus", is(true)));
        }

        @Test
        void getTrainerMonthlySummary_WithoutAuth_ShouldReturnUnauthorized() throws Exception {
                mockMvc.perform(get("/api/v1/workloads/trainers/{username}/monthly-summary", "test.trainer"))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(roles = "USER")
        void getTrainerMonthlySummary_WithWrongRole_ShouldReturnForbidden() throws Exception {
                mockMvc.perform(get("/api/v1/workloads/trainers/{username}/monthly-summary", "test.trainer"))
                                .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "TRAINER")
        void getTrainerMonthlySummary_WhenTrainerNotFound_ShouldReturnNotFound() throws Exception {
                when(trainerWorkloadService.getTrainerMonthlySummary(anyString()))
                                .thenThrow(new TrainerNotFoundException("Trainer not found"));

                mockMvc.perform(get("/api/v1/workloads/trainers/{username}/monthly-summary", "test.trainer"))
                                .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(roles = "TRAINER")
        void getTrainerMonthlySummaryByYear_WhenInvalidYear_ShouldReturnBadRequest() throws Exception {
                mockMvc.perform(get("/api/v1/workloads/trainers/{username}/monthly-summary/{year}", "test.trainer",
                                "invalid-year"))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = "TRAINER")
        void getTrainerMonthlySummaryByMonth_WhenTrainerNotFound_ShouldReturnNotFound() throws Exception {
                when(trainerWorkloadService.getTrainerMonthlySummary(anyString(), anyInt(), anyInt()))
                                .thenThrow(new TrainerNotFoundException("Trainer not found"));

                mockMvc.perform(get("/api/v1/workloads/trainers/{username}/monthly-summary/{year}/{month}",
                                "test.trainer", 2024, 7))
                                .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(roles = "TRAINER")
        void processTrainerWorkload_WithUpdateAction_ShouldReturnOk() throws Exception {
                TrainerWorkloadRequest updateRequest = new TrainerWorkloadRequest(
                                "test.trainer",
                                "Test",
                                "Trainer",
                                true,
                                LocalDate.of(2024, 7, 21),
                                90,
                                ActionType.UPDATE);

                doNothing().when(trainerWorkloadService).processTrainerWorkload(any(TrainerWorkloadRequest.class));

                mockMvc.perform(post("/api/v1/workloads")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status", is(200)))
                                .andExpect(jsonPath("$.message", is("Workload processed successfully")));
        }

        @Test
        @WithMockUser(roles = "TRAINER")
        void processTrainerWorkload_WithDeleteAction_ShouldReturnOk() throws Exception {
                TrainerWorkloadRequest deleteRequest = new TrainerWorkloadRequest(
                                "test.trainer",
                                "Test",
                                "Trainer",
                                true,
                                LocalDate.of(2024, 7, 21),
                                60,
                                ActionType.DELETE);

                doNothing().when(trainerWorkloadService).processTrainerWorkload(any(TrainerWorkloadRequest.class));

                mockMvc.perform(post("/api/v1/workloads")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(deleteRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status", is(200)))
                                .andExpect(jsonPath("$.message", is("Workload processed successfully")));
        }
}