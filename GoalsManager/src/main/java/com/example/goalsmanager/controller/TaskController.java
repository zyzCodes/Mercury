package com.example.goalsmanager.controller;

import com.example.goalsmanager.dto.CreateTaskRequest;
import com.example.goalsmanager.dto.TaskDTO;
import com.example.goalsmanager.dto.UpdateTaskRequest;
import com.example.goalsmanager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(final TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Create a new task
     * POST /api/tasks
     */
    @PostMapping
    public ResponseEntity<?> createTask(@Valid @RequestBody final CreateTaskRequest request) {
        try {
            final TaskDTO task = taskService.createTask(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(task);
        } catch (final RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get all tasks
     * GET /api/tasks
     */
    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        final List<TaskDTO> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get task by ID
     * GET /api/tasks/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable final Long id) {
        try {
            final TaskDTO task = taskService.getTaskById(id);
            return ResponseEntity.ok(task);
        } catch (final RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get all tasks for a specific user
     * GET /api/tasks/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getTasksByUserId(@PathVariable final Long userId) {
        try {
            final List<TaskDTO> tasks = taskService.getTasksByUserId(userId);
            return ResponseEntity.ok(tasks);
        } catch (final RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get all tasks for a specific habit
     * GET /api/tasks/habit/{habitId}
     */
    @GetMapping("/habit/{habitId}")
    public ResponseEntity<?> getTasksByHabitId(@PathVariable final Long habitId) {
        try {
            final List<TaskDTO> tasks = taskService.getTasksByHabitId(habitId);
            return ResponseEntity.ok(tasks);
        } catch (final RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get tasks for a user within a date range
     * GET /api/tasks/user/{userId}/week?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD
     */
    @GetMapping("/user/{userId}/week")
    public ResponseEntity<?> getTasksByUserIdAndDateRange(
            @PathVariable final Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate endDate) {
        try {
            final List<TaskDTO> tasks = taskService.getTasksByUserIdAndDateRange(userId, startDate, endDate);
            return ResponseEntity.ok(tasks);
        } catch (final RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get completed tasks for a user
     * GET /api/tasks/user/{userId}/completed
     */
    @GetMapping("/user/{userId}/completed")
    public ResponseEntity<?> getCompletedTasksByUserId(@PathVariable final Long userId) {
        try {
            final List<TaskDTO> tasks = taskService.getCompletedTasksByUserId(userId);
            return ResponseEntity.ok(tasks);
        } catch (final RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get pending tasks for a user
     * GET /api/tasks/user/{userId}/pending
     */
    @GetMapping("/user/{userId}/pending")
    public ResponseEntity<?> getPendingTasksByUserId(@PathVariable final Long userId) {
        try {
            final List<TaskDTO> tasks = taskService.getPendingTasksByUserId(userId);
            return ResponseEntity.ok(tasks);
        } catch (final RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Update a task
     * PUT /api/tasks/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(
            @PathVariable final Long id,
            @RequestBody final UpdateTaskRequest request) {
        try {
            final TaskDTO task = taskService.updateTask(id, request);
            return ResponseEntity.ok(task);
        } catch (final RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Toggle task completion status
     * PATCH /api/tasks/{id}/toggle
     */
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<?> toggleTaskCompletion(@PathVariable final Long id) {
        try {
            final TaskDTO task = taskService.toggleTaskCompletion(id);
            return ResponseEntity.ok(task);
        } catch (final RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Delete a task
     * DELETE /api/tasks/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable final Long id) {
        try {
            taskService.deleteTask(id);
            return ResponseEntity.ok(createSuccessResponse("Task deleted successfully"));
        } catch (final RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Check if a task exists
     * GET /api/tasks/exists/{id}
     */
    @GetMapping("/exists/{id}")
    public ResponseEntity<Map<String, Boolean>> checkTaskExists(@PathVariable final Long id) {
        final boolean exists = taskService.existsById(id);
        final Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

    /**
     * Count tasks by user
     * GET /api/tasks/user/{userId}/count
     */
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Map<String, Long>> countTasksByUserId(@PathVariable final Long userId) {
        final long count = taskService.countTasksByUserId(userId);
        final Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    /**
     * Count tasks by habit
     * GET /api/tasks/habit/{habitId}/count
     */
    @GetMapping("/habit/{habitId}/count")
    public ResponseEntity<Map<String, Long>> countTasksByHabitId(@PathVariable final Long habitId) {
        final long count = taskService.countTasksByHabitId(habitId);
        final Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    // Helper methods for creating response objects
    private Map<String, String> createErrorResponse(final String message) {
        final Map<String, String> response = new HashMap<>();
        response.put("error", message);
        return response;
    }

    private Map<String, String> createSuccessResponse(final String message) {
        final Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return response;
    }
}

