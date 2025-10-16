package com.example.goalsmanager.controller;

import com.example.goalsmanager.dto.CreateHabitRequest;
import com.example.goalsmanager.dto.UpdateHabitRequest;
import com.example.goalsmanager.goalutils.GoalStatus;
import com.example.goalsmanager.model.Goal;
import com.example.goalsmanager.model.Habit;
import com.example.goalsmanager.model.User;
import com.example.goalsmanager.repository.GoalRepository;
import com.example.goalsmanager.repository.HabitRepository;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
@DisplayName("Habit Controller Integration Tests")
class HabitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HabitRepository habitRepository;

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Goal testGoal;
    private Habit testHabit;

    @BeforeEach
    void setUp() {
        // Clean up before each test
        habitRepository.deleteAll();
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
        testGoal.setTitle("Get Fit");
        testGoal.setDescription("Improve overall fitness");
        testGoal.setStartDate(LocalDate.of(2025, 1, 1));
        testGoal.setEndDate(LocalDate.of(2025, 12, 31));
        testGoal.setStatus(GoalStatus.IN_PROGRESS);
        testGoal.setUser(testUser);
        testGoal = goalRepository.save(testGoal);

        // Create and save test habit
        testHabit = new Habit();
        testHabit.setName("Morning Run");
        testHabit.setDescription("Run 5km every morning");
        testHabit.setDaysOfWeek("Mon,Wed,Fri");
        testHabit.setStartDate(LocalDate.of(2025, 1, 1));
        testHabit.setEndDate(LocalDate.of(2025, 12, 31));
        testHabit.setStreakStatus(5);
        testHabit.setColor("#FF5733");
        testHabit.setGoal(testGoal);
        testHabit.setUser(testUser);
        testHabit = habitRepository.save(testHabit);
    }

    @Test
    @DisplayName("POST /api/habits - Should create habit successfully")
    void shouldCreateHabitSuccessfullyTest() throws Exception {
        // Given
        CreateHabitRequest request = new CreateHabitRequest();
        request.setName("Meditation");
        request.setDescription("Meditate for 20 minutes");
        request.setDaysOfWeek("Mon,Tue,Wed,Thu,Fri");
        request.setStartDate(LocalDate.of(2025, 1, 1));
        request.setEndDate(LocalDate.of(2025, 12, 31));
        request.setColor("#33C9FF");
        request.setGoalId(testGoal.getId());
        request.setUserId(testUser.getId());

        // When & Then
        mockMvc.perform(post("/api/habits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Meditation"))
                .andExpect(jsonPath("$.description").value("Meditate for 20 minutes"))
                .andExpect(jsonPath("$.daysOfWeek").value("Mon,Tue,Wed,Thu,Fri"))
                .andExpect(jsonPath("$.color").value("#33C9FF"))
                .andExpect(jsonPath("$.goalId").value(testGoal.getId()))
                .andExpect(jsonPath("$.userId").value(testUser.getId()))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @DisplayName("POST /api/habits - Should return 400 when goal not found")
    void shouldReturn400WhenGoalNotFoundTest() throws Exception {
        // Given
        CreateHabitRequest request = new CreateHabitRequest();
        request.setName("Meditation");
        request.setDaysOfWeek("Mon,Wed,Fri");
        request.setStartDate(LocalDate.of(2025, 1, 1));
        request.setEndDate(LocalDate.of(2025, 12, 31));
        request.setGoalId(999L); // Non-existent goal
        request.setUserId(testUser.getId());

        // When & Then
        mockMvc.perform(post("/api/habits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(containsString("Goal not found")));
    }

    @Test
    @DisplayName("POST /api/habits - Should return 400 when dates are invalid")
    void shouldReturn400WhenDatesAreInvalidTest() throws Exception {
        // Given
        CreateHabitRequest request = new CreateHabitRequest();
        request.setName("Meditation");
        request.setDaysOfWeek("Mon,Wed,Fri");
        request.setStartDate(LocalDate.of(2025, 12, 31));
        request.setEndDate(LocalDate.of(2025, 1, 1)); // End before start
        request.setGoalId(testGoal.getId());
        request.setUserId(testUser.getId());

        // When & Then
        mockMvc.perform(post("/api/habits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(containsString("End date must be after start date")));
    }

    @Test
    @DisplayName("GET /api/habits/{id} - Should get habit by ID")
    void shouldGetHabitByIdTest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/habits/{id}", testHabit.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testHabit.getId()))
                .andExpect(jsonPath("$.name").value("Morning Run"))
                .andExpect(jsonPath("$.description").value("Run 5km every morning"))
                .andExpect(jsonPath("$.daysOfWeek").value("Mon,Wed,Fri"))
                .andExpect(jsonPath("$.streakStatus").value(5));
    }

    @Test
    @DisplayName("GET /api/habits/{id} - Should return 404 when habit not found")
    void shouldReturn404WhenHabitNotFoundTest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/habits/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(containsString("Habit not found")));
    }

    @Test
    @DisplayName("GET /api/habits - Should get all habits")
    void shouldGetAllHabitsTest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/habits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].name").value("Morning Run"));
    }

    @Test
    @DisplayName("GET /api/habits/user/{userId} - Should get all habits for user")
    void shouldGetAllHabitsForUserTest() throws Exception {
        // Given - Create another habit for the user
        Habit habit2 = new Habit();
        habit2.setName("Evening Yoga");
        habit2.setDescription("30 minutes of yoga");
        habit2.setDaysOfWeek("Tue,Thu,Sat");
        habit2.setStartDate(LocalDate.of(2025, 1, 1));
        habit2.setEndDate(LocalDate.of(2025, 12, 31));
        habit2.setStreakStatus(3);
        habit2.setColor("#33FF57");
        habit2.setGoal(testGoal);
        habit2.setUser(testUser);
        habitRepository.save(habit2);

        // When & Then
        mockMvc.perform(get("/api/habits/user/{userId}", testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Morning Run", "Evening Yoga")));
    }

    @Test
    @DisplayName("GET /api/habits/goal/{goalId} - Should get all habits for goal")
    void shouldGetAllHabitsForGoalTest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/habits/goal/{goalId}", testGoal.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Morning Run"));
    }

    @Test
    @DisplayName("PUT /api/habits/{id} - Should update habit successfully")
    void shouldUpdateHabitSuccessfullyTest() throws Exception {
        // Given
        UpdateHabitRequest updateRequest = new UpdateHabitRequest();
        updateRequest.setName("Evening Run");
        updateRequest.setStreakStatus(10);
        updateRequest.setDaysOfWeek("Mon,Wed,Fri,Sun");

        // When & Then
        mockMvc.perform(put("/api/habits/{id}", testHabit.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Evening Run"))
                .andExpect(jsonPath("$.streakStatus").value(10))
                .andExpect(jsonPath("$.daysOfWeek").value("Mon,Wed,Fri,Sun"));
    }

    @Test
    @DisplayName("DELETE /api/habits/{id} - Should delete habit successfully")
    void shouldDeleteHabitSuccessfullyTest() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/habits/{id}", testHabit.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Habit deleted successfully"));

        // Verify habit was deleted
        mockMvc.perform(get("/api/habits/{id}", testHabit.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/habits/{id} - Should return 404 when deleting non-existent habit")
    void shouldReturn404WhenDeletingNonExistentHabitTest() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/habits/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(containsString("Habit not found")));
    }

    @Test
    @DisplayName("GET /api/habits/exists/{id} - Should check if habit exists")
    void shouldCheckIfHabitExistsTest() throws Exception {
        // When & Then - Habit exists
        mockMvc.perform(get("/api/habits/exists/{id}", testHabit.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists").value(true));

        // Habit doesn't exist
        mockMvc.perform(get("/api/habits/exists/{id}", 999L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists").value(false));
    }

    @Test
    @DisplayName("GET /api/habits/user/{userId}/count - Should count habits by user")
    void shouldCountHabitsByUserTest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/habits/user/{userId}/count", testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1));
    }

    @Test
    @DisplayName("GET /api/habits/goal/{goalId}/count - Should count habits by goal")
    void shouldCountHabitsByGoalTest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/habits/goal/{goalId}/count", testGoal.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1));
    }

    @Test
    @DisplayName("Should perform partial update on habit")
    void shouldPerformPartialUpdateTest() throws Exception {
        // Given - Only update the streak status
        UpdateHabitRequest updateRequest = new UpdateHabitRequest();
        updateRequest.setStreakStatus(15);

        // When & Then
        mockMvc.perform(put("/api/habits/{id}", testHabit.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.streakStatus").value(15))
                .andExpect(jsonPath("$.name").value("Morning Run")) // Unchanged
                .andExpect(jsonPath("$.daysOfWeek").value("Mon,Wed,Fri")); // Unchanged
    }
}

