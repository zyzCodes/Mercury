package com.example.goalsmanager.repository;

import com.example.goalsmanager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Find all tasks for a specific user
     */
    List<Task> findByUserId(Long userId);

    /**
     * Find all tasks for a specific habit
     */
    List<Task> findByHabitId(Long habitId);

    /**
     * Find tasks for a habit up to and including a date, ordered by date descending
     */
    List<Task> findByHabitIdAndDateLessThanEqualOrderByDateDesc(Long habitId, LocalDate date);

    /**
     * Find tasks by user ID and habit ID
     */
    List<Task> findByUserIdAndHabitId(Long userId, Long habitId);

    /**
     * Find tasks by user ID and completion status
     */
    List<Task> findByUserIdAndCompleted(Long userId, Boolean completed);

    /**
     * Find tasks by habit ID and completion status
     */
    List<Task> findByHabitIdAndCompleted(Long habitId, Boolean completed);

    /**
     * Find tasks by user ID within a date range
     */
    List<Task> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * Count tasks by user ID
     */
    long countByUserId(Long userId);

    /**
     * Count tasks by habit ID
     */
    long countByHabitId(Long habitId);
}

