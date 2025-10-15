package com.example.goalsmanager.repository;

import com.example.goalsmanager.model.Habit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HabitRepository extends JpaRepository<Habit, Long> {

    /**
     * Find all habits for a specific user
     */
    List<Habit> findByUserId(Long userId);

    /**
     * Find all habits for a specific goal
     */
    List<Habit> findByGoalId(Long goalId);

    /**
     * Find habits by user ID and goal ID
     */
    List<Habit> findByUserIdAndGoalId(Long userId, Long goalId);

    /**
     * Check if a user has any habits
     */
    boolean existsByUserId(Long userId);

    /**
     * Count habits by user ID
     */
    long countByUserId(Long userId);

    /**
     * Count habits by goal ID
     */
    long countByGoalId(Long goalId);
}

