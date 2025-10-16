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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
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
     * Automatically generates missing tasks for the user's active habits
     */
    public List<TaskDTO> getTasksByUserIdAndDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with id: " + userId);
        }

        // Generate missing tasks for all user's habits in this date range
        generateMissingTasksForUser(userId, startDate, endDate);

        // Return all tasks in the range (now including generated ones)
        return taskRepository.findByUserIdAndDateBetween(userId, startDate, endDate).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Generate missing tasks for all habits of a user within a date range
     * This method is idempotent - it only creates tasks that don't already exist
     */
    private void generateMissingTasksForUser(Long userId, LocalDate startDate, LocalDate endDate) {
        // Get all habits for the user
        List<Habit> userHabits = habitRepository.findByUserId(userId);

        for (Habit habit : userHabits) {
            generateTasksForHabit(habit, startDate, endDate);
        }
    }

    /**
     * Generate tasks for a specific habit within a date range
     * Only generates tasks for dates that match the habit's daysOfWeek pattern
     */
    private void generateTasksForHabit(Habit habit, LocalDate rangeStart, LocalDate rangeEnd) {
        // Parse the habit's days of week (e.g., "Mon, Wed, Fri")
        String[] selectedDays = habit.getDaysOfWeek().split(",\\s*");
        List<DayOfWeek> habitDays = parseDaysOfWeek(selectedDays);

        if (habitDays.isEmpty()) {
            return; // No days selected, nothing to generate
        }

        // Determine the actual start and end dates for task generation
        // Tasks should only be generated within the habit's active period
        LocalDate effectiveStart = rangeStart.isBefore(habit.getStartDate()) ? habit.getStartDate() : rangeStart;
        LocalDate effectiveEnd = rangeEnd.isAfter(habit.getEndDate()) ? habit.getEndDate() : rangeEnd;

        if (effectiveStart.isAfter(effectiveEnd)) {
            return; // No overlap between range and habit period
        }

        // Generate tasks for each matching day
        List<Task> tasksToCreate = new ArrayList<>();
        LocalDate currentDate = effectiveStart;

        while (!currentDate.isAfter(effectiveEnd)) {
            // Check if this day matches the habit's schedule
            if (habitDays.contains(currentDate.getDayOfWeek())) {
                // Check if task already exists for this date
                boolean taskExists = taskRepository.existsByHabitIdAndDate(habit.getId(), currentDate);

                if (!taskExists) {
                    // Create new task
                    Task task = new Task();
                    task.setName(habit.getName());
                    task.setDate(currentDate);
                    task.setCompleted(false);
                    task.setHabit(habit);
                    task.setUser(habit.getUser());
                    tasksToCreate.add(task);
                }
            }
            currentDate = currentDate.plusDays(1);
        }

        // Bulk save all tasks
        if (!tasksToCreate.isEmpty()) {
            taskRepository.saveAll(tasksToCreate);
        }
    }

    /**
     * Parse day of week strings (Mon, Tue, etc.) into DayOfWeek enum values
     */
    private List<DayOfWeek> parseDaysOfWeek(String[] dayStrings) {
        List<DayOfWeek> days = new ArrayList<>();
        for (String dayStr : dayStrings) {
            switch (dayStr.trim()) {
                case "Mon": days.add(DayOfWeek.MONDAY); break;
                case "Tue": days.add(DayOfWeek.TUESDAY); break;
                case "Wed": days.add(DayOfWeek.WEDNESDAY); break;
                case "Thu": days.add(DayOfWeek.THURSDAY); break;
                case "Fri": days.add(DayOfWeek.FRIDAY); break;
                case "Sat": days.add(DayOfWeek.SATURDAY); break;
                case "Sun": days.add(DayOfWeek.SUNDAY); break;
            }
        }
        return days;
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
     * Toggle task completion status and update habit streak
     */
    public TaskDTO toggleTaskCompletion(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

        task.setCompleted(!task.getCompleted());
        Task updatedTask = taskRepository.save(task);
        
        // Update habit streak after toggling task
        updateHabitStreak(task.getHabit());
        
        return convertToDTO(updatedTask);
    }
    
    /**
     * Calculate and update habit streak based on completed tasks
     * Only looks at actual scheduled tasks, not arbitrary days
     */
    private void updateHabitStreak(Habit habit) {
        LocalDate today = LocalDate.now();
        
        // Get all past tasks (from start date to today) ordered by date descending (newest first)
        List<Task> pastTasks = taskRepository.findByHabitIdAndDateLessThanEqualOrderByDateDesc(
            habit.getId(), today
        );
        
        if (pastTasks.isEmpty()) {
            habit.setStreakStatus(0);
            habitRepository.save(habit);
            return;
        }
        
        // Check the most recent task
        Task mostRecentTask = pastTasks.get(0);
        
        // If the last task was not completed, streak is 0
        if (!mostRecentTask.getCompleted()) {
            habit.setStreakStatus(0);
            habitRepository.save(habit);
            return;
        }
        
        // Count backwards from the most recent task
        // Keep counting as long as tasks are completed
        int streak = 0;
        for (Task task : pastTasks) {
            if (task.getCompleted()) {
                streak++;
            } else {
                // Found an incomplete task, stop counting
                break;
            }
        }
        
        habit.setStreakStatus(streak);
        habitRepository.save(habit);
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
                task.getHabit().getColor(),
                task.getUser().getId(),
                task.getUser().getUsername(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}

