package com.example.goalsmanager.service;

import com.example.goalsmanager.dto.CreateTaskRequest;
import com.example.goalsmanager.dto.TaskDTO;
import com.example.goalsmanager.dto.UpdateTaskRequest;
import com.example.goalsmanager.model.Habit;
import com.example.goalsmanager.model.Task;
import com.example.goalsmanager.model.User;
import com.example.goalsmanager.repository.HabitRepository;
import com.example.goalsmanager.repository.TaskRepository;
import com.example.goalsmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final HabitRepository habitRepository;
    private final UserRepository userRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository,
                       HabitRepository habitRepository,
                       UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.habitRepository = habitRepository;
        this.userRepository = userRepository;
    }

    /**
     * Create a new task
     */
    public TaskDTO createTask(CreateTaskRequest request) {
        // Validate habit exists
        Habit habit = habitRepository.findById(request.getHabitId())
                .orElseThrow(() -> new RuntimeException("Habit not found with id: " + request.getHabitId()));

        // Validate user exists
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));

        // Create task
        Task task = new Task();
        task.setName(request.getName());
        task.setDate(request.getDate());
        task.setCompleted(false);
        task.setHabit(habit);
        task.setUser(user);

        Task savedTask = taskRepository.save(task);
        return convertToDTO(savedTask);
    }

    /**
     * Get task by ID
     */
    @Transactional(readOnly = true)
    public TaskDTO getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        return convertToDTO(task);
    }

    /**
     * Get all tasks
     */
    @Transactional(readOnly = true)
    public List<TaskDTO> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get all tasks for a specific user
     */
    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksByUserId(Long userId) {
        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        return taskRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get all tasks for a specific habit
     */
    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksByHabitId(Long habitId) {
        // Verify habit exists
        if (!habitRepository.existsById(habitId)) {
            throw new RuntimeException("Habit not found with id: " + habitId);
        }
        return taskRepository.findByHabitId(habitId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get tasks by user ID and date range
     */
    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksByUserIdAndDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        return taskRepository.findByUserIdAndDateBetween(userId, startDate, endDate).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get completed tasks for a user
     */
    @Transactional(readOnly = true)
    public List<TaskDTO> getCompletedTasksByUserId(Long userId) {
        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        return taskRepository.findByUserIdAndCompleted(userId, true).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get pending tasks for a user
     */
    @Transactional(readOnly = true)
    public List<TaskDTO> getPendingTasksByUserId(Long userId) {
        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        return taskRepository.findByUserIdAndCompleted(userId, false).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Update task
     */
    public TaskDTO updateTask(Long id, UpdateTaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

        // Update only non-null fields
        if (request.getName() != null) {
            task.setName(request.getName());
        }
        if (request.getCompleted() != null) {
            task.setCompleted(request.getCompleted());
        }
        if (request.getDate() != null) {
            task.setDate(request.getDate());
        }

        Task updatedTask = taskRepository.save(task);
        return convertToDTO(updatedTask);
    }

    /**
     * Toggle task completion status
     */
    public TaskDTO toggleTaskCompletion(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        
        task.setCompleted(!task.getCompleted());
        Task updatedTask = taskRepository.save(task);
        return convertToDTO(updatedTask);
    }

    /**
     * Delete task
     */
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new RuntimeException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }

    /**
     * Check if a task exists
     */
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return taskRepository.existsById(id);
    }

    /**
     * Count tasks by user
     */
    @Transactional(readOnly = true)
    public long countTasksByUserId(Long userId) {
        return taskRepository.countByUserId(userId);
    }

    /**
     * Count tasks by habit
     */
    @Transactional(readOnly = true)
    public long countTasksByHabitId(Long habitId) {
        return taskRepository.countByHabitId(habitId);
    }

    /**
     * Convert Task entity to TaskDTO
     */
    private TaskDTO convertToDTO(Task task) {
        return new TaskDTO(
                task.getId(),
                task.getName(),
                task.getCompleted(),
                task.getDate(),
                task.getHabit().getId(),
                task.getHabit().getName(),
                task.getUser().getId(),
                task.getUser().getUsername(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}

