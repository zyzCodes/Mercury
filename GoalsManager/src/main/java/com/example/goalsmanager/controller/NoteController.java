package com.example.goalsmanager.controller;

import com.example.goalsmanager.dto.CreateNoteRequest;
import com.example.goalsmanager.dto.NoteDTO;
import com.example.goalsmanager.dto.UpdateNoteRequest;
import com.example.goalsmanager.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;

    @Autowired
    public NoteController(final NoteService noteService) {
        this.noteService = noteService;
    }

    /**
     * Create a new note
     * POST /api/notes
     */
    @PostMapping
    public ResponseEntity<?> createNote(@Valid @RequestBody final CreateNoteRequest request) {
        try {
            final NoteDTO note = noteService.createNote(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(note);
        } catch (final RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get note by ID
     * GET /api/notes/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getNoteById(@PathVariable final Long id) {
        try {
            final NoteDTO note = noteService.getNoteById(id);
            return ResponseEntity.ok(note);
        } catch (final RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Get all notes for a specific goal
     * GET /api/notes/goal/{goalId}
     */
    @GetMapping("/goal/{goalId}")
    public ResponseEntity<?> getNotesByGoalId(@PathVariable final Long goalId) {
        try {
            final List<NoteDTO> notes = noteService.getNotesByGoalId(goalId);
            return ResponseEntity.ok(notes);
        } catch (final RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Update a note
     * PUT /api/notes/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateNote(
            @PathVariable final Long id,
            @RequestBody final UpdateNoteRequest request) {
        try {
            final NoteDTO note = noteService.updateNote(id, request);
            return ResponseEntity.ok(note);
        } catch (final RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Delete a note
     * DELETE /api/notes/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNote(@PathVariable final Long id) {
        try {
            noteService.deleteNote(id);
            return ResponseEntity.ok(createSuccessResponse("Note deleted successfully"));
        } catch (final RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Count notes for a goal
     * GET /api/notes/goal/{goalId}/count
     */
    @GetMapping("/goal/{goalId}/count")
    public ResponseEntity<Map<String, Long>> countNotesByGoalId(@PathVariable final Long goalId) {
        final long count = noteService.countNotesByGoalId(goalId);
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
