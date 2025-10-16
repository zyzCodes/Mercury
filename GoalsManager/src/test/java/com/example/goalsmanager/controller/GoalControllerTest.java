package com.example.goalsmanager.controller;

import com.example.goalsmanager.dto.CreateGoalRequest;
import com.example.goalsmanager.dto.UpdateGoalRequest;
import com.example.goalsmanager.goalutils.GoalStatus;
import com.example.goalsmanager.model.Goal;
import com.example.goalsmanager.model.User;
import com.example.goalsmanager.repository.GoalRepository;
import com.example.goalsmanager.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
@DisplayName("Goal Controller Integration Tests")
class GoalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Goal testGoal;

    @BeforeEach
    void setUp() {
        // Clean up before each test
        goalRepository.deleteAll();
        userRepository.deleteAll();

        // Create and save test user
        testUser = new User();
        testUser.setProvider("github");
        testUser.setProviderId("12345");
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");
        testUser = userRepository.save(testUser);

        // Create and save test goal
        testGoal = new Goal();
        testGoal.setTitle("Learn Spring Boot");
        testGoal.setDescription("Master Spring Boot framework");
        testGoal.setStartDate(LocalDate.of(2025, 1, 1));
        testGoal.setEndDate(LocalDate.of(2025, 12, 31));
        testGoal.setStatus(GoalStatus.NOT_STARTED);
        testGoal.setUser(testUser);
        testGoal = goalRepository.save(testGoal);
    }

    @Test
    @DisplayName("POST /api/goals - Should create goal successfully")
    void shouldCreateGoalSuccessfullyTest() throws Exception {
        // Given
        CreateGoalRequest request = new CreateGoalRequest();
        request.setTitle("Learn React");
        request.setDescription("Master React framework");
        request.setStartDate(LocalDate.of(2025, 1, 1));
        request.setEndDate(LocalDate.of(2025, 6, 30));
        request.setUserId(testUser.getId());

        // When & Then
        mockMvc.perform(post("/api/goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Learn React"))
                .andExpect(jsonPath("$.description").value("Master React framework"))
                .andExpect(jsonPath("$.userId").value(testUser.getId()))
                .andExpect(jsonPath("$.status").value("NOT_STARTED"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @DisplayName("POST /api/goals - Should return 400 when user not found")
    void shouldReturn400WhenUserNotFoundTest() throws Exception {
        // Given
        CreateGoalRequest request = new CreateGoalRequest();
        request.setTitle("Learn React");
        request.setStartDate(LocalDate.of(2025, 1, 1));
        request.setEndDate(LocalDate.of(2025, 6, 30));
        request.setUserId(999L); // Non-existent user

        // When & Then
        mockMvc.perform(post("/api/goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(containsString("User not found")));
    }

    @Test
    @DisplayName("POST /api/goals - Should return 400 when dates are invalid")
    void shouldReturn400WhenDatesAreInvalidTest() throws Exception {
        // Given
        CreateGoalRequest request = new CreateGoalRequest();
        request.setTitle("Learn React");
        request.setStartDate(LocalDate.of(2025, 12, 31));
        request.setEndDate(LocalDate.of(2025, 1, 1)); // End before start
        request.setUserId(testUser.getId());

        // When & Then
        mockMvc.perform(post("/api/goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(containsString("End date must be after start date")));
    }

    @Test
    @DisplayName("GET /api/goals/{id} - Should get goal by ID")
    void shouldGetGoalByIdTest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/goals/{id}", testGoal.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testGoal.getId()))
                .andExpect(jsonPath("$.title").value("Learn Spring Boot"))
                .andExpect(jsonPath("$.description").value("Master Spring Boot framework"))
                .andExpect(jsonPath("$.status").value("NOT_STARTED"));
    }

    @Test
    @DisplayName("GET /api/goals/{id} - Should return 404 when goal not found")
    void shouldReturn404WhenGoalNotFoundTest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/goals/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(containsString("Goal not found")));
    }

    @Test
    @DisplayName("GET /api/goals - Should get all goals")
    void shouldGetAllGoalsTest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/goals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].title").value("Learn Spring Boot"));
    }

    @Test
    @DisplayName("GET /api/goals/user/{userId} - Should get all goals for user")
    void shouldGetAllGoalsForUserTest() throws Exception {
        // Given - Create another goal for the user
        Goal goal2 = new Goal();
        goal2.setTitle("Learn Docker");
        goal2.setDescription("Master containerization");
        goal2.setStartDate(LocalDate.of(2025, 1, 1));
        goal2.setEndDate(LocalDate.of(2025, 12, 31));
        goal2.setStatus(GoalStatus.IN_PROGRESS);
        goal2.setUser(testUser);
        goalRepository.save(goal2);

        // When & Then
        mockMvc.perform(get("/api/goals/user/{userId}", testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].title", containsInAnyOrder("Learn Spring Boot", "Learn Docker")));
    }

    @Test
    @DisplayName("GET /api/goals/user/{userId}/status/{status} - Should get goals by user and status")
    void shouldGetGoalsByUserAndStatusTest() throws Exception {
        // Given
        testGoal.setStatus(GoalStatus.IN_PROGRESS);
        goalRepository.save(testGoal);

        // When & Then
        mockMvc.perform(get("/api/goals/user/{userId}/status/{status}",
                        testUser.getId(), GoalStatus.IN_PROGRESS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status").value("IN_PROGRESS"));
    }

    @Test
    @DisplayName("PUT /api/goals/{id} - Should update goal successfully")
    void shouldUpdateGoalSuccessfullyTest() throws Exception {
        // Given
        UpdateGoalRequest updateRequest = new UpdateGoalRequest();
        updateRequest.setTitle("Learn Spring Boot Advanced");
        updateRequest.setStatus(GoalStatus.IN_PROGRESS);

        // When & Then
        mockMvc.perform(put("/api/goals/{id}", testGoal.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Learn Spring Boot Advanced"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    @DisplayName("PATCH /api/goals/{id}/status - Should update goal status")
    void shouldUpdateGoalStatusTest() throws Exception {
        // Given
        Map<String, String> statusUpdate = new HashMap<>();
        statusUpdate.put("status", "COMPLETED");

        // When & Then
        mockMvc.perform(patch("/api/goals/{id}/status", testGoal.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    @DisplayName("DELETE /api/goals/{id} - Should delete goal successfully")
    void shouldDeleteGoalSuccessfullyTest() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/goals/{id}", testGoal.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Goal deleted successfully"));

        // Verify goal was deleted
        mockMvc.perform(get("/api/goals/{id}", testGoal.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/goals/{id} - Should return 404 when deleting non-existent goal")
    void shouldReturn404WhenDeletingNonExistentGoalTest() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/goals/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(containsString("Goal not found")));
    }

    @Test
    @DisplayName("GET /api/goals/exists/{id} - Should check if goal exists")
    void shouldCheckIfGoalExistsTest() throws Exception {
        // When & Then - Goal exists
        mockMvc.perform(get("/api/goals/exists/{id}", testGoal.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists").value(true));

        // Goal doesn't exist
        mockMvc.perform(get("/api/goals/exists/{id}", 999L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists").value(false));
    }

    @Test
    @DisplayName("GET /api/goals/user/{userId}/count - Should count goals by user")
    void shouldCountGoalsByUserTest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/goals/user/{userId}/count", testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1));
    }

    @Test
    @DisplayName("GET /api/goals/user/{userId}/active - Should get active goals")
    void shouldGetActiveGoalsTest() throws Exception {
        // Given - Set goal as active and not overdue
        testGoal.setStatus(GoalStatus.IN_PROGRESS);
        testGoal.setEndDate(LocalDate.now().plusMonths(6));
        goalRepository.save(testGoal);

        // When & Then
        mockMvc.perform(get("/api/goals/user/{userId}/active", testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status").value("IN_PROGRESS"));
    }

    @Test
    @DisplayName("GET /api/goals/user/{userId}/completed - Should get completed goals")
    void shouldGetCompletedGoalsTest() throws Exception {
        // Given
        testGoal.setStatus(GoalStatus.COMPLETED);
        goalRepository.save(testGoal);

        // When & Then
        mockMvc.perform(get("/api/goals/user/{userId}/completed", testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status").value("COMPLETED"));
    }

    @Test
    @DisplayName("GET /api/goals/user/{userId}/overdue - Should get overdue goals")
    void shouldGetOverdueGoalsTest() throws Exception {
        // Given - Set goal as overdue
        testGoal.setStatus(GoalStatus.IN_PROGRESS);
        testGoal.setEndDate(LocalDate.now().minusDays(10));
        goalRepository.save(testGoal);

        // When & Then
        mockMvc.perform(get("/api/goals/user/{userId}/overdue", testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(testGoal.getId()));
    }

    @Test
    @DisplayName("GET /api/goals/status/{status} - Should get all goals by status")
    void shouldGetAllGoalsByStatusTest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/goals/status/{status}", GoalStatus.NOT_STARTED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].status").value("NOT_STARTED"));
    }

    @Test
    @DisplayName("Should create goal with image URL")
    void shouldCreateGoalWithImageUrlTest() throws Exception {
        // Given
        CreateGoalRequest request = new CreateGoalRequest();
        request.setTitle("Complete Marathon");
        request.setDescription("Run a full marathon");
        request.setImageUrl("https://example.com/marathon.jpg");
        request.setStartDate(LocalDate.of(2025, 1, 1));
        request.setEndDate(LocalDate.of(2025, 10, 31));
        request.setUserId(testUser.getId());

        // When & Then
        mockMvc.perform(post("/api/goals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Complete Marathon"))
                .andExpect(jsonPath("$.imageUrl").value("https://example.com/marathon.jpg"));
    }

    @Test
    @DisplayName("Should perform partial update on goal")
    void shouldPerformPartialUpdateTest() throws Exception {
        // Given - Only update the title
        UpdateGoalRequest updateRequest = new UpdateGoalRequest();
        updateRequest.setTitle("Updated Title Only");

        // When & Then
        mockMvc.perform(put("/api/goals/{id}", testGoal.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title Only"))
                .andExpect(jsonPath("$.description").value("Master Spring Boot framework")) // Unchanged
                .andExpect(jsonPath("$.status").value("NOT_STARTED")); // Unchanged
    }
}

