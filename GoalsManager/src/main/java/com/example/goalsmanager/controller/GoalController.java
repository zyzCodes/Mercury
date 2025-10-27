package com.example.goalsmanager.controller;

import com.example.goalsmanager.dto.CreateGoalRequest;
import com.example.goalsmanager.dto.GoalDTO;
import com.example.goalsmanager.dto.UpdateGoalRequest;
import com.example.goalsmanager.goalutils.GoalStatus;
import com.example.goalsmanager.service.GoalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalService goalService;

    @Autowired
    public GoalController(final GoalService goalService) {
        this.goalService = goalService;
    }

    /**
     * Create a new goal
     * POST /api/goals
     */
    @PostMapping
    public ResponseEntity<?> createGoal(@Valid @RequestBody final CreateGoalRequest request) {
        try {
            final GoalDTO goal = goalService.createGoal(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(goal);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get all goals
     * GET /api/goals
     */
    @GetMapping
    public ResponseEntity<List<GoalDTO>> getAllGoals() {
        final List<GoalDTO> goals = goalService.getAllGoals();
        return ResponseEntity.ok(goals);
    }

    /**
     * Get goal by ID
     * GET /api/goals/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getGoalById(@PathVariable final Long id) {
        try {
            final GoalDTO goal = goalService.getGoalById(id);
            return ResponseEntity.ok(goal);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get all goals for a specific user
     * GET /api/goals/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getGoalsByUserId(@PathVariable final Long userId) {
        try {
            final List<GoalDTO> goals = goalService.getGoalsByUserId(userId);
            return ResponseEntity.ok(goals);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get goals by user ID and status
     * GET /api/goals/user/{userId}/status/{status}
     */
    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<?> getGoalsByUserIdAndStatus(
            @PathVariable final Long userId,
            @PathVariable final GoalStatus status) {
        try {
            final List<GoalDTO> goals = goalService.getGoalsByUserIdAndStatus(userId, status);
            return ResponseEntity.ok(goals);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get goals by status
     * GET /api/goals/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<GoalDTO>> getGoalsByStatus(@PathVariable final GoalStatus status) {
        final List<GoalDTO> goals = goalService.getGoalsByStatus(status);
        return ResponseEntity.ok(goals);
    }

    /**
     * Get active goals for a user
     * GET /api/goals/user/{userId}/active
     */
    @GetMapping("/user/{userId}/active")
    public ResponseEntity<?> getActiveGoalsByUserId(@PathVariable final Long userId) {
        try {
            final List<GoalDTO> goals = goalService.getActiveGoalsByUserId(userId);
            return ResponseEntity.ok(goals);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get completed goals for a user
     * GET /api/goals/user/{userId}/completed
     */
    @GetMapping("/user/{userId}/completed")
    public ResponseEntity<?> getCompletedGoalsByUserId(@PathVariable final Long userId) {
        try {
            final List<GoalDTO> goals = goalService.getCompletedGoalsByUserId(userId);
            return ResponseEntity.ok(goals);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get overdue goals for a user
     * GET /api/goals/user/{userId}/overdue
     */
    @GetMapping("/user/{userId}/overdue")
    public ResponseEntity<?> getOverdueGoalsByUserId(@PathVariable final Long userId) {
        try {
            final List<GoalDTO> goals = goalService.getOverdueGoalsByUserId(userId);
            return ResponseEntity.ok(goals);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Update a goal
     * PUT /api/goals/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateGoal(
            @PathVariable final Long id,
            @RequestBody final UpdateGoalRequest request) {
        try {
            final GoalDTO goal = goalService.updateGoal(id, request);
            return ResponseEntity.ok(goal);
        } catch (final RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Update goal status
     * PATCH /api/goals/{id}/status
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateGoalStatus(
            @PathVariable final Long id,
            @RequestBody final Map<String, String> request) {
        try {
            final GoalStatus status = GoalStatus.valueOf(request.get("status"));
            final GoalDTO goal = goalService.updateGoalStatus(id, status);
            return ResponseEntity.ok(goal);
        } catch (final IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Invalid status value"));
        } catch (final RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Delete a goal
     * DELETE /api/goals/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGoal(@PathVariable final Long id) {
        try {
            goalService.deleteGoal(id);
            return ResponseEntity.ok(createSuccessResponse("Goal deleted successfully"));
        } catch (final RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Check if a goal exists
     * GET /api/goals/exists/{id}
     */
    @GetMapping("/exists/{id}")
    public ResponseEntity<Map<String, Boolean>> checkGoalExists(@PathVariable final Long id) {
        final boolean exists = goalService.existsById(id);
        final Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

    /**
     * Count goals by user
     * GET /api/goals/user/{userId}/count
     */
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Map<String, Long>> countGoalsByUserId(@PathVariable final Long userId) {
        final long count = goalService.countGoalsByUserId(userId);
        final Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    /**
     * Count goals by user and status
     * GET /api/goals/user/{userId}/count/{status}
     */
    @GetMapping("/user/{userId}/count/{status}")
    public ResponseEntity<Map<String, Long>> countGoalsByUserIdAndStatus(
            @PathVariable final Long userId,
            @PathVariable final GoalStatus status) {
        final long count = goalService.countGoalsByUserIdAndStatus(userId, status);
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

