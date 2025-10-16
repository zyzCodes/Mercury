package com.example.goalsmanager.service;

import com.example.goalsmanager.dto.CreateTaskRequest;
import com.example.goalsmanager.dto.TaskDTO;
import com.example.goalsmanager.dto.UpdateTaskRequest;
import com.example.goalsmanager.goalutils.GoalStatus;
import com.example.goalsmanager.model.Goal;
import com.example.goalsmanager.model.Habit;
import com.example.goalsmanager.model.Task;
import com.example.goalsmanager.model.User;
import com.example.goalsmanager.repository.HabitRepository;
import com.example.goalsmanager.repository.TaskRepository;
import com.example.goalsmanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Task Service Tests")
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private HabitRepository habitRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    private User testUser;
    private Goal testGoal;
    private Habit testHabit;
    private Task testTask;
    private CreateTaskRequest createRequest;

    @BeforeEach
    void setUp() {
        // Set up test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setProvider("github");
        testUser.setProviderId("12345");
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");

        // Set up test goal
        testGoal = new Goal();
        testGoal.setId(1L);
        testGoal.setTitle("Get Fit");
        testGoal.setDescription("Improve overall fitness");
        testGoal.setStartDate(LocalDate.of(2025, 1, 1));
        testGoal.setEndDate(LocalDate.of(2025, 12, 31));
        testGoal.setStatus(GoalStatus.IN_PROGRESS);
        testGoal.setUser(testUser);

        // Set up test habit
        testHabit = new Habit();
        testHabit.setId(1L);
        testHabit.setName("Morning Run");
        testHabit.setDescription("Run 5km every morning");
        testHabit.setDaysOfWeek("Mon,Wed,Fri");
        testHabit.setStartDate(LocalDate.of(2025, 1, 1));
        testHabit.setEndDate(LocalDate.of(2025, 12, 31));
        testHabit.setStreakStatus(0);
        testHabit.setGoal(testGoal);
        testHabit.setUser(testUser);

        // Set up test task
        testTask = new Task();
        testTask.setId(1L);
        testTask.setName("Monday Morning Run");
        testTask.setDate(LocalDate.of(2025, 10, 20));
        testTask.setCompleted(false);
        testTask.setHabit(testHabit);
        testTask.setUser(testUser);
        testTask.setCreatedAt(LocalDateTime.now());
        testTask.setUpdatedAt(LocalDateTime.now());

        // Set up create request
        createRequest = new CreateTaskRequest();
        createRequest.setName("Monday Morning Run");
        createRequest.setDate(LocalDate.of(2025, 10, 20));
        createRequest.setHabitId(1L);
        createRequest.setUserId(1L);
    }

    @Test
    @DisplayName("Should create task successfully")
    void shouldCreateTaskSuccessfully() {
        // Given
        when(habitRepository.findById(1L)).thenReturn(Optional.of(testHabit));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // When
        TaskDTO result = taskService.createTask(createRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Monday Morning Run");
        assertThat(result.getDate()).isEqualTo(LocalDate.of(2025, 10, 20));
        assertThat(result.getCompleted()).isFalse();
        assertThat(result.getHabitId()).isEqualTo(1L);
        assertThat(result.getUserId()).isEqualTo(1L);
        
        verify(habitRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Should throw exception when creating task for non-existent habit")
    void shouldThrowExceptionWhenHabitNotFound() {
        // Given
        when(habitRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> taskService.createTask(createRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Habit not found");
        
        verify(habitRepository, times(1)).findById(1L);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("Should throw exception when creating task for non-existent user")
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        when(habitRepository.findById(1L)).thenReturn(Optional.of(testHabit));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> taskService.createTask(createRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
        
        verify(habitRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("Should get task by ID successfully")
    void shouldGetTaskByIdSuccessfully() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        // When
        TaskDTO result = taskService.getTaskById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Monday Morning Run");
        
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when task not found by ID")
    void shouldThrowExceptionWhenTaskNotFoundById() {
        // Given
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> taskService.getTaskById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Task not found");
        
        verify(taskRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should get all tasks for user")
    void shouldGetAllTasksForUser() {
        // Given
        Task task2 = new Task();
        task2.setId(2L);
        task2.setName("Wednesday Morning Run");
        task2.setDate(LocalDate.of(2025, 10, 22));
        task2.setCompleted(true);
        task2.setHabit(testHabit);
        task2.setUser(testUser);
        task2.setCreatedAt(LocalDateTime.now());
        task2.setUpdatedAt(LocalDateTime.now());

        when(userRepository.existsById(1L)).thenReturn(true);
        when(taskRepository.findByUserId(1L)).thenReturn(Arrays.asList(testTask, task2));

        // When
        List<TaskDTO> results = taskService.getTasksByUserId(1L);

        // Then
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getName()).isEqualTo("Monday Morning Run");
        assertThat(results.get(1).getName()).isEqualTo("Wednesday Morning Run");
        
        verify(userRepository, times(1)).existsById(1L);
        verify(taskRepository, times(1)).findByUserId(1L);
    }

    @Test
    @DisplayName("Should get all tasks for habit")
    void shouldGetAllTasksForHabit() {
        // Given
        when(habitRepository.existsById(1L)).thenReturn(true);
        when(taskRepository.findByHabitId(1L)).thenReturn(Arrays.asList(testTask));

        // When
        List<TaskDTO> results = taskService.getTasksByHabitId(1L);

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Monday Morning Run");
        
        verify(habitRepository, times(1)).existsById(1L);
        verify(taskRepository, times(1)).findByHabitId(1L);
    }

    @Test
    @DisplayName("Should get tasks by user and date range")
    void shouldGetTasksByUserAndDateRange() {
        // Given
        LocalDate startDate = LocalDate.of(2025, 10, 19);
        LocalDate endDate = LocalDate.of(2025, 10, 23);
        
        when(userRepository.existsById(1L)).thenReturn(true);
        when(taskRepository.findByUserIdAndDateBetween(1L, startDate, endDate))
                .thenReturn(Arrays.asList(testTask));

        // When
        List<TaskDTO> results = taskService.getTasksByUserIdAndDateRange(1L, startDate, endDate);

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getDate()).isEqualTo(LocalDate.of(2025, 10, 20));
        
        verify(userRepository, times(1)).existsById(1L);
        verify(taskRepository, times(1)).findByUserIdAndDateBetween(1L, startDate, endDate);
    }

    @Test
    @DisplayName("Should get completed tasks for user")
    void shouldGetCompletedTasksForUser() {
        // Given
        testTask.setCompleted(true);
        
        when(userRepository.existsById(1L)).thenReturn(true);
        when(taskRepository.findByUserIdAndCompleted(1L, true))
                .thenReturn(Arrays.asList(testTask));

        // When
        List<TaskDTO> results = taskService.getCompletedTasksByUserId(1L);

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getCompleted()).isTrue();
        
        verify(userRepository, times(1)).existsById(1L);
        verify(taskRepository, times(1)).findByUserIdAndCompleted(1L, true);
    }

    @Test
    @DisplayName("Should get pending tasks for user")
    void shouldGetPendingTasksForUser() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(true);
        when(taskRepository.findByUserIdAndCompleted(1L, false))
                .thenReturn(Arrays.asList(testTask));

        // When
        List<TaskDTO> results = taskService.getPendingTasksByUserId(1L);

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getCompleted()).isFalse();
        
        verify(userRepository, times(1)).existsById(1L);
        verify(taskRepository, times(1)).findByUserIdAndCompleted(1L, false);
    }

    @Test
    @DisplayName("Should update task successfully")
    void shouldUpdateTaskSuccessfully() {
        // Given
        UpdateTaskRequest updateRequest = new UpdateTaskRequest();
        updateRequest.setName("Monday Evening Run");
        updateRequest.setCompleted(true);
        updateRequest.setDate(LocalDate.of(2025, 10, 21));

        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // When
        TaskDTO result = taskService.updateTask(1L, updateRequest);

        // Then
        assertThat(result).isNotNull();
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(testTask);
        assertThat(testTask.getName()).isEqualTo("Monday Evening Run");
        assertThat(testTask.getCompleted()).isTrue();
        assertThat(testTask.getDate()).isEqualTo(LocalDate.of(2025, 10, 21));
    }

    @Test
    @DisplayName("Should toggle task completion")
    void shouldToggleTaskCompletion() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // When - First toggle (false -> true)
        taskService.toggleTaskCompletion(1L);
        assertThat(testTask.getCompleted()).isTrue();

        // When - Second toggle (true -> false)
        taskService.toggleTaskCompletion(1L);
        assertThat(testTask.getCompleted()).isFalse();

        // Then
        verify(taskRepository, times(2)).findById(1L);
        verify(taskRepository, times(2)).save(testTask);
    }

    @Test
    @DisplayName("Should delete task successfully")
    void shouldDeleteTaskSuccessfully() {
        // Given
        when(taskRepository.existsById(1L)).thenReturn(true);
        doNothing().when(taskRepository).deleteById(1L);

        // When
        taskService.deleteTask(1L);

        // Then
        verify(taskRepository, times(1)).existsById(1L);
        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent task")
    void shouldThrowExceptionWhenDeletingNonExistentTask() {
        // Given
        when(taskRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> taskService.deleteTask(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Task not found");
        
        verify(taskRepository, times(1)).existsById(999L);
        verify(taskRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should count tasks by user ID")
    void shouldCountTasksByUserId() {
        // Given
        when(taskRepository.countByUserId(1L)).thenReturn(10L);

        // When
        long count = taskService.countTasksByUserId(1L);

        // Then
        assertThat(count).isEqualTo(10L);
        verify(taskRepository, times(1)).countByUserId(1L);
    }

    @Test
    @DisplayName("Should count tasks by habit ID")
    void shouldCountTasksByHabitId() {
        // Given
        when(taskRepository.countByHabitId(1L)).thenReturn(5L);

        // When
        long count = taskService.countTasksByHabitId(1L);

        // Then
        assertThat(count).isEqualTo(5L);
        verify(taskRepository, times(1)).countByHabitId(1L);
    }

    @Test
    @DisplayName("Should check if task exists")
    void shouldCheckIfTaskExists() {
        // Given
        when(taskRepository.existsById(1L)).thenReturn(true);
        when(taskRepository.existsById(999L)).thenReturn(false);

        // When
        boolean exists = taskService.existsById(1L);
        boolean notExists = taskService.existsById(999L);

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
        
        verify(taskRepository, times(1)).existsById(1L);
        verify(taskRepository, times(1)).existsById(999L);
    }
}

