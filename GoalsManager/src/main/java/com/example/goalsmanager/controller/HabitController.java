package com.example.goalsmanager.controller;

import com.example.goalsmanager.dto.CreateHabitRequest;
import com.example.goalsmanager.dto.HabitDTO;
import com.example.goalsmanager.dto.UpdateHabitRequest;
import com.example.goalsmanager.service.HabitService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/habits")
public class HabitController {

    private final HabitService habitService;

    @Autowired
    public HabitController(final HabitService habitService) {
        this.habitService = habitService;
    }

    /**
     * Create a new habit
     * POST /api/habits
     */
    @PostMapping
    public ResponseEntity<?> createHabit(@Valid @RequestBody final CreateHabitRequest request) {
        try {
            final HabitDTO habit = habitService.createHabit(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(habit);
        } catch (final RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get all habits
     * GET /api/habits
     */
    @GetMapping
    public ResponseEntity<List<HabitDTO>> getAllHabits() {
        final List<HabitDTO> habits = habitService.getAllHabits();
        return ResponseEntity.ok(habits);
    }

    /**
     * Get habit by ID
     * GET /api/habits/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getHabitById(@PathVariable final Long id) {
        try {
            final HabitDTO habit = habitService.getHabitById(id);
            return ResponseEntity.ok(habit);
        } catch (final RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get all habits for a specific user
     * GET /api/habits/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getHabitsByUserId(@PathVariable final Long userId) {
        try {
            final List<HabitDTO> habits = habitService.getHabitsByUserId(userId);
            return ResponseEntity.ok(habits);
        } catch (final RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get all habits for a specific goal
     * GET /api/habits/goal/{goalId}
     */
    @GetMapping("/goal/{goalId}")
    public ResponseEntity<?> getHabitsByGoalId(@PathVariable final Long goalId) {
        try {
            final List<HabitDTO> habits = habitService.getHabitsByGoalId(goalId);
            return ResponseEntity.ok(habits);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Update a habit
     * PUT /api/habits/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateHabit(
            @PathVariable final Long id,
            @RequestBody final UpdateHabitRequest request) {
        try {
            final HabitDTO habit = habitService.updateHabit(id, request);
            return ResponseEntity.ok(habit);
        } catch (final RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Delete a habit
     * DELETE /api/habits/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHabit(@PathVariable final Long id) {
        try {
            habitService.deleteHabit(id);
            return ResponseEntity.ok(createSuccessResponse("Habit deleted successfully"));
        } catch (final RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Check if a habit exists
     * GET /api/habits/exists/{id}
     */
    @GetMapping("/exists/{id}")
    public ResponseEntity<Map<String, Boolean>> checkHabitExists(@PathVariable final Long id) {
        final boolean exists = habitService.existsById(id);
        final Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

    /**
     * Count habits by user
     * GET /api/habits/user/{userId}/count
     */
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Map<String, Long>> countHabitsByUserId(@PathVariable final Long userId) {
        final long count = habitService.countHabitsByUserId(userId);
        final Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    /**
     * Count habits by goal
     * GET /api/habits/goal/{goalId}/count
     */
    @GetMapping("/goal/{goalId}/count")
    public ResponseEntity<Map<String, Long>> countHabitsByGoalId(@PathVariable final Long goalId) {
        final long count = habitService.countHabitsByGoalId(goalId);
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

