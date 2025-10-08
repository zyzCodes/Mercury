package com.example.goalsmanager.service;

import com.example.goalsmanager.dto.CreateNoteRequest;
import com.example.goalsmanager.dto.NoteDTO;
import com.example.goalsmanager.dto.UpdateNoteRequest;
import com.example.goalsmanager.model.Goal;
import com.example.goalsmanager.model.Note;
import com.example.goalsmanager.repository.GoalRepository;
import com.example.goalsmanager.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class NoteService {

    private final NoteRepository noteRepository;
    private final GoalRepository goalRepository;

    @Autowired
    public NoteService(NoteRepository noteRepository, GoalRepository goalRepository) {
        this.noteRepository = noteRepository;
        this.goalRepository = goalRepository;
    }

    /**
     * Create a new note
     */
    public NoteDTO createNote(CreateNoteRequest request) {
        // Validate goal exists
        Goal goal = goalRepository.findById(request.getGoalId())
                .orElseThrow(() -> new RuntimeException("Goal not found with id: " + request.getGoalId()));

        // Create note
        Note note = new Note();
        note.setContent(request.getContent());
        note.setGoal(goal);

        Note savedNote = noteRepository.save(note);
        return convertToDTO(savedNote);
    }

    /**
     * Get note by ID
     */
    @Transactional(readOnly = true)
    public NoteDTO getNoteById(Long id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found with id: " + id));
        return convertToDTO(note);
    }

    /**
     * Get all notes for a specific goal, ordered by creation date descending
     */
    @Transactional(readOnly = true)
    public List<NoteDTO> getNotesByGoalId(Long goalId) {
        // Verify goal exists
        if (!goalRepository.existsById(goalId)) {
            throw new RuntimeException("Goal not found with id: " + goalId);
        }
        return noteRepository.findByGoalIdOrderByCreatedAtDesc(goalId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Update a note
     */
    public NoteDTO updateNote(Long id, UpdateNoteRequest request) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found with id: " + id));

        // Update only if content is provided
        if (request.getContent() != null && !request.getContent().trim().isEmpty()) {
            note.setContent(request.getContent());
        }

        Note updatedNote = noteRepository.save(note);
        return convertToDTO(updatedNote);
    }

    /**
     * Delete a note
     */
    public void deleteNote(Long id) {
        if (!noteRepository.existsById(id)) {
            throw new RuntimeException("Note not found with id: " + id);
        }
        noteRepository.deleteById(id);
    }

    /**
     * Delete all notes for a specific goal
     */
    public void deleteNotesByGoalId(Long goalId) {
        noteRepository.deleteByGoalId(goalId);
    }

    /**
     * Count notes for a goal
     */
    @Transactional(readOnly = true)
    public long countNotesByGoalId(Long goalId) {
        return noteRepository.countByGoalId(goalId);
    }

    /**
     * Convert Note entity to NoteDTO
     */
    private NoteDTO convertToDTO(Note note) {
        return new NoteDTO(
                note.getId(),
                note.getContent(),
                note.getGoal().getId(),
                note.getCreatedAt()
        );
    }
}
