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
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Create a new task
     * POST /api/tasks
     */
    @PostMapping
    public ResponseEntity<?> createTask(@Valid @RequestBody CreateTaskRequest request) {
        try {
            TaskDTO task = taskService.createTask(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(task);
        } catch (RuntimeException e) {
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
        List<TaskDTO> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get task by ID
     * GET /api/tasks/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Long id) {
        try {
            TaskDTO task = taskService.getTaskById(id);
            return ResponseEntity.ok(task);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get all tasks for a specific user
     * GET /api/tasks/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getTasksByUserId(@PathVariable Long userId) {
        try {
            List<TaskDTO> tasks = taskService.getTasksByUserId(userId);
            return ResponseEntity.ok(tasks);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get all tasks for a specific habit
     * GET /api/tasks/habit/{habitId}
     */
    @GetMapping("/habit/{habitId}")
    public ResponseEntity<?> getTasksByHabitId(@PathVariable Long habitId) {
        try {
            List<TaskDTO> tasks = taskService.getTasksByHabitId(habitId);
            return ResponseEntity.ok(tasks);
        } catch (RuntimeException e) {
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
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<TaskDTO> tasks = taskService.getTasksByUserIdAndDateRange(userId, startDate, endDate);
            return ResponseEntity.ok(tasks);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get completed tasks for a user
     * GET /api/tasks/user/{userId}/completed
     */
    @GetMapping("/user/{userId}/completed")
    public ResponseEntity<?> getCompletedTasksByUserId(@PathVariable Long userId) {
        try {
            List<TaskDTO> tasks = taskService.getCompletedTasksByUserId(userId);
            return ResponseEntity.ok(tasks);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get pending tasks for a user
     * GET /api/tasks/user/{userId}/pending
     */
    @GetMapping("/user/{userId}/pending")
    public ResponseEntity<?> getPendingTasksByUserId(@PathVariable Long userId) {
        try {
            List<TaskDTO> tasks = taskService.getPendingTasksByUserId(userId);
            return ResponseEntity.ok(tasks);
        } catch (RuntimeException e) {
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
            @PathVariable Long id,
            @RequestBody UpdateTaskRequest request) {
        try {
            TaskDTO task = taskService.updateTask(id, request);
            return ResponseEntity.ok(task);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Toggle task completion status
     * PATCH /api/tasks/{id}/toggle
     */
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<?> toggleTaskCompletion(@PathVariable Long id) {
        try {
            TaskDTO task = taskService.toggleTaskCompletion(id);
            return ResponseEntity.ok(task);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Delete a task
     * DELETE /api/tasks/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        try {
            taskService.deleteTask(id);
            return ResponseEntity.ok(createSuccessResponse("Task deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Check if a task exists
     * GET /api/tasks/exists/{id}
     */
    @GetMapping("/exists/{id}")
    public ResponseEntity<Map<String, Boolean>> checkTaskExists(@PathVariable Long id) {
        boolean exists = taskService.existsById(id);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

    /**
     * Count tasks by user
     * GET /api/tasks/user/{userId}/count
     */
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Map<String, Long>> countTasksByUserId(@PathVariable Long userId) {
        long count = taskService.countTasksByUserId(userId);
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    /**
     * Count tasks by habit
     * GET /api/tasks/habit/{habitId}/count
     */
    @GetMapping("/habit/{habitId}/count")
    public ResponseEntity<Map<String, Long>> countTasksByHabitId(@PathVariable Long habitId) {
        long count = taskService.countTasksByHabitId(habitId);
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    // Helper methods for creating response objects
    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> response = new HashMap<>();
        response.put("error", message);
        return response;
    }

    private Map<String, String> createSuccessResponse(String message) {
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return response;
    }
}

