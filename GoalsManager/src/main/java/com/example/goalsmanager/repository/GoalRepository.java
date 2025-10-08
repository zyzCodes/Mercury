package com.example.goalsmanager.repository;

import com.example.goalsmanager.goalutils.GoalStatus;
import com.example.goalsmanager.model.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {

    /**
     * Find all goals for a specific user
     */
    List<Goal> findByUserId(Long userId);

    /**
     * Find goals by user ID and status
     */
    List<Goal> findByUserIdAndStatus(Long userId, GoalStatus status);

    /**
     * Find goals by status
     */
    List<Goal> findByStatus(GoalStatus status);

    /**
     * Find goals ending before a specific date
     */
    List<Goal> findByEndDateBefore(LocalDate date);

    /**
     * Find goals ending after a specific date
     */
    List<Goal> findByEndDateAfter(LocalDate date);

    /**
     * Find goals for a user within a date range
     */
    List<Goal> findByUserIdAndStartDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * Check if a user has any goals
     */
    boolean existsByUserId(Long userId);

    /**
     * Count goals by user ID
     */
    long countByUserId(Long userId);

    /**
     * Count goals by user ID and status
     */
    long countByUserIdAndStatus(Long userId, GoalStatus status);
}

