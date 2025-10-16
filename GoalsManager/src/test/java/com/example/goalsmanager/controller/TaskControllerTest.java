package com.example.goalsmanager.controller;

import com.example.goalsmanager.dto.CreateTaskRequest;
import com.example.goalsmanager.dto.UpdateTaskRequest;
import com.example.goalsmanager.goalutils.GoalStatus;
import com.example.goalsmanager.model.Goal;
import com.example.goalsmanager.model.Habit;
import com.example.goalsmanager.model.Task;
import com.example.goalsmanager.model.User;
import com.example.goalsmanager.repository.GoalRepository;
import com.example.goalsmanager.repository.HabitRepository;
import com.example.goalsmanager.repository.TaskRepository;
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
@DisplayName("Task Controller Integration Tests")
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private HabitRepository habitRepository;

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Goal testGoal;
    private Habit testHabit;
    private Task testTask;

    @BeforeEach
    void setUp() {
        // Clean up before each test
        taskRepository.deleteAll();
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
        testHabit.setGoal(testGoal);
        testHabit.setUser(testUser);
        testHabit = habitRepository.save(testHabit);

        // Create and save test task
        testTask = new Task();
        testTask.setName("Monday Morning Run");
        testTask.setDate(LocalDate.of(2025, 10, 20));
        testTask.setCompleted(false);
        testTask.setHabit(testHabit);
        testTask.setUser(testUser);
        testTask = taskRepository.save(testTask);
    }

    @Test
    @DisplayName("POST /api/tasks - Should create task successfully")
    void shouldCreateTaskSuccessfullyTest() throws Exception {
        // Given
        CreateTaskRequest request = new CreateTaskRequest();
        request.setName("Wednesday Morning Run");
        request.setDate(LocalDate.of(2025, 10, 22));
        request.setHabitId(testHabit.getId());
        request.setUserId(testUser.getId());

        // When & Then
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Wednesday Morning Run"))
                .andExpect(jsonPath("$.date").value("2025-10-22"))
                .andExpect(jsonPath("$.completed").value(false))
                .andExpect(jsonPath("$.habitId").value(testHabit.getId()))
                .andExpect(jsonPath("$.userId").value(testUser.getId()))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @DisplayName("POST /api/tasks - Should return 400 when habit not found")
    void shouldReturn400WhenHabitNotFoundTest() throws Exception {
        // Given
        CreateTaskRequest request = new CreateTaskRequest();
        request.setName("Wednesday Morning Run");
        request.setDate(LocalDate.of(2025, 10, 22));
        request.setHabitId(999L); // Non-existent habit
        request.setUserId(testUser.getId());

        // When & Then
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(containsString("Habit not found")));
    }

    @Test
    @DisplayName("GET /api/tasks/{id} - Should get task by ID")
    void shouldGetTaskByIdTest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/tasks/{id}", testTask.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testTask.getId()))
                .andExpect(jsonPath("$.name").value("Monday Morning Run"))
                .andExpect(jsonPath("$.date").value("2025-10-20"))
                .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    @DisplayName("GET /api/tasks/{id} - Should return 404 when task not found")
    void shouldReturn404WhenTaskNotFoundTest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/tasks/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(containsString("Task not found")));
    }

    @Test
    @DisplayName("GET /api/tasks - Should get all tasks")
    void shouldGetAllTasksTest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].name").value("Monday Morning Run"));
    }

    @Test
    @DisplayName("GET /api/tasks/user/{userId} - Should get all tasks for user")
    void shouldGetAllTasksForUserTest() throws Exception {
        // Given - Create another task for the user
        Task task2 = new Task();
        task2.setName("Friday Morning Run");
        task2.setDate(LocalDate.of(2025, 10, 24));
        task2.setCompleted(true);
        task2.setHabit(testHabit);
        task2.setUser(testUser);
        taskRepository.save(task2);

        // When & Then
        mockMvc.perform(get("/api/tasks/user/{userId}", testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Monday Morning Run", "Friday Morning Run")));
    }

    @Test
    @DisplayName("GET /api/tasks/habit/{habitId} - Should get all tasks for habit")
    void shouldGetAllTasksForHabitTest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/tasks/habit/{habitId}", testHabit.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Monday Morning Run"));
    }

    @Test
    @DisplayName("GET /api/tasks/user/{userId}/week - Should get tasks by user and date range")
    void shouldGetTasksByUserAndDateRangeTest() throws Exception {
        // Given
        LocalDate startDate = LocalDate.of(2025, 10, 19);
        LocalDate endDate = LocalDate.of(2025, 10, 23);

        // When & Then
        mockMvc.perform(get("/api/tasks/user/{userId}/week", testUser.getId())
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].date", containsInAnyOrder("2025-10-20", "2025-10-22")));
    }

    @Test
    @DisplayName("GET /api/tasks/user/{userId}/completed - Should get completed tasks")
    void shouldGetCompletedTasksTest() throws Exception {
        // Given - Mark task as completed
        testTask.setCompleted(true);
        taskRepository.save(testTask);

        // When & Then
        mockMvc.perform(get("/api/tasks/user/{userId}/completed", testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].completed").value(true));
    }

    @Test
    @DisplayName("GET /api/tasks/user/{userId}/pending - Should get pending tasks")
    void shouldGetPendingTasksTest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/tasks/user/{userId}/pending", testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].completed").value(false));
    }

    @Test
    @DisplayName("PUT /api/tasks/{id} - Should update task successfully")
    void shouldUpdateTaskSuccessfullyTest() throws Exception {
        // Given
        UpdateTaskRequest updateRequest = new UpdateTaskRequest();
        updateRequest.setName("Monday Evening Run");
        updateRequest.setCompleted(true);
        updateRequest.setDate(LocalDate.of(2025, 10, 21));

        // When & Then
        mockMvc.perform(put("/api/tasks/{id}", testTask.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Monday Evening Run"))
                .andExpect(jsonPath("$.completed").value(true))
                .andExpect(jsonPath("$.date").value("2025-10-21"));
    }

    @Test
    @DisplayName("PATCH /api/tasks/{id}/toggle - Should toggle task completion")
    void shouldToggleTaskCompletionTest() throws Exception {
        // When & Then - First toggle (false -> true)
        mockMvc.perform(patch("/api/tasks/{id}/toggle", testTask.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(true));

        // Second toggle (true -> false)
        mockMvc.perform(patch("/api/tasks/{id}/toggle", testTask.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    @DisplayName("DELETE /api/tasks/{id} - Should delete task successfully")
    void shouldDeleteTaskSuccessfullyTest() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/tasks/{id}", testTask.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Task deleted successfully"));

        // Verify task was deleted
        mockMvc.perform(get("/api/tasks/{id}", testTask.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/tasks/{id} - Should return 404 when deleting non-existent task")
    void shouldReturn404WhenDeletingNonExistentTaskTest() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/tasks/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(containsString("Task not found")));
    }

    @Test
    @DisplayName("GET /api/tasks/exists/{id} - Should check if task exists")
    void shouldCheckIfTaskExistsTest() throws Exception {
        // When & Then - Task exists
        mockMvc.perform(get("/api/tasks/exists/{id}", testTask.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists").value(true));

        // Task doesn't exist
        mockMvc.perform(get("/api/tasks/exists/{id}", 999L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists").value(false));
    }

    @Test
    @DisplayName("GET /api/tasks/user/{userId}/count - Should count tasks by user")
    void shouldCountTasksByUserTest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/tasks/user/{userId}/count", testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1));
    }

    @Test
    @DisplayName("GET /api/tasks/habit/{habitId}/count - Should count tasks by habit")
    void shouldCountTasksByHabitTest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/tasks/habit/{habitId}/count", testHabit.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1));
    }

    @Test
    @DisplayName("Should perform partial update on task")
    void shouldPerformPartialUpdateTest() throws Exception {
        // Given - Only update completed status
        UpdateTaskRequest updateRequest = new UpdateTaskRequest();
        updateRequest.setCompleted(true);

        // When & Then
        mockMvc.perform(put("/api/tasks/{id}", testTask.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(true))
                .andExpect(jsonPath("$.name").value("Monday Morning Run")) // Unchanged
                .andExpect(jsonPath("$.date").value("2025-10-20")); // Unchanged
    }
}

