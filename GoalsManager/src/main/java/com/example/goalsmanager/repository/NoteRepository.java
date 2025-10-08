package com.example.goalsmanager.repository;

import com.example.goalsmanager.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    /**
     * Find all notes for a specific goal, ordered by creation date descending
     */
    List<Note> findByGoalIdOrderByCreatedAtDesc(Long goalId);

    /**
     * Find all notes for a specific goal
     */
    List<Note> findByGoalId(Long goalId);

    /**
     * Count notes for a specific goal
     */
    long countByGoalId(Long goalId);

    /**
     * Delete all notes for a specific goal
     */
    void deleteByGoalId(Long goalId);
}
