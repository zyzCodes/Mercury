package com.example.goalsmanager.service;

import com.example.goalsmanager.dto.CreateGoalRequest;
import com.example.goalsmanager.dto.GoalDTO;
import com.example.goalsmanager.dto.UpdateGoalRequest;
import com.example.goalsmanager.goalutils.GoalStatus;
import com.example.goalsmanager.model.Goal;
import com.example.goalsmanager.model.User;
import com.example.goalsmanager.repository.GoalRepository;
import com.example.goalsmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    @Autowired
    public GoalService(final GoalRepository goalRepository, final UserRepository userRepository) {
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
    }

    /**
     * Create a new goal
     */
    public GoalDTO createGoal(final CreateGoalRequest request) {
        // Validate user exists
        final User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));

        // Validate dates
        validateDates(request.getStartDate(), request.getEndDate());

        // Create goal
        final Goal goal = new Goal();
        goal.setTitle(request.getTitle());
        goal.setDescription(request.getDescription());
        goal.setImageUrl(request.getImageUrl());
        goal.setEmoji(request.getEmoji());
        goal.setStartDate(request.getStartDate());
        goal.setEndDate(request.getEndDate());
        goal.setStatus(request.getStatus() != null ? request.getStatus() : GoalStatus.NOT_STARTED);
        goal.setUser(user);

        final Goal savedGoal = goalRepository.save(goal);
        return convertToDTO(savedGoal);
    }

    /**
     * Get goal by ID
     */
    @Transactional(readOnly = true)
    public GoalDTO getGoalById(final Long id) {
        final Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found with id: " + id));
        return convertToDTO(goal);
    }

    /**
     * Get all goals
     */
    @Transactional(readOnly = true)
    public List<GoalDTO> getAllGoals() {
        return goalRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get all goals for a specific user
     */
    @Transactional(readOnly = true)
    public List<GoalDTO> getGoalsByUserId(final Long userId) {
        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        return goalRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get goals by user ID and status
     */
    @Transactional(readOnly = true)
    public List<GoalDTO> getGoalsByUserIdAndStatus(final Long userId, final GoalStatus status) {
        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        return goalRepository.findByUserIdAndStatus(userId, status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get goals by status
     */
    @Transactional(readOnly = true)
    public List<GoalDTO> getGoalsByStatus(final GoalStatus status) {
        return goalRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Update goal
     */
    public GoalDTO updateGoal(final Long id, final UpdateGoalRequest request) {
        final Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found with id: " + id));

        // Update only non-null fields
        if (request.getTitle() != null) {
            goal.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            goal.setDescription(request.getDescription());
        }
        if (request.getImageUrl() != null) {
            goal.setImageUrl(request.getImageUrl());
        }
        if (request.getEmoji() != null) {
            goal.setEmoji(request.getEmoji());
        }
        if (request.getStartDate() != null) {
            goal.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            goal.setEndDate(request.getEndDate());
        }
        if (request.getStatus() != null) {
            goal.setStatus(request.getStatus());
        }

        // Validate dates if both are present
        if (goal.getStartDate() != null && goal.getEndDate() != null) {
            validateDates(goal.getStartDate(), goal.getEndDate());
        }

        final Goal updatedGoal = goalRepository.save(goal);
        return convertToDTO(updatedGoal);
    }

    /**
     * Update goal status
     */
    public GoalDTO updateGoalStatus(final Long id, final GoalStatus status) {
        final Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found with id: " + id));

        goal.setStatus(status);
        final Goal updatedGoal = goalRepository.save(goal);
        return convertToDTO(updatedGoal);
    }

    /**
     * Delete goal
     */
    public void deleteGoal(final Long id) {
        if (!goalRepository.existsById(id)) {
            throw new RuntimeException("Goal not found with id: " + id);
        }
        goalRepository.deleteById(id);
    }

    /**
     * Check if a goal exists
     */
    @Transactional(readOnly = true)
    public boolean existsById(final Long id) {
        return goalRepository.existsById(id);
    }

    /**
     * Count goals by user
     */
    @Transactional(readOnly = true)
    public long countGoalsByUserId(final Long userId) {
        return goalRepository.countByUserId(userId);
    }

    /**
     * Count goals by user and status
     */
    @Transactional(readOnly = true)
    public long countGoalsByUserIdAndStatus(final Long userId, final GoalStatus status) {
        return goalRepository.countByUserIdAndStatus(userId, status);
    }

    /**
     * Get overdue goals (end date has passed and status is not completed)
     */
    @Transactional(readOnly = true)
    public List<GoalDTO> getOverdueGoalsByUserId(final Long userId) {
        final List<Goal> userGoals = goalRepository.findByUserId(userId);
        final LocalDate today = LocalDate.now();

        return userGoals.stream()
                .filter(goal -> goal.getEndDate().isBefore(today)
                        && goal.getStatus() != GoalStatus.COMPLETED
                        && goal.getStatus() != GoalStatus.CANCELLED)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get active goals (in progress or not started, within date range)
     */
    @Transactional(readOnly = true)
    public List<GoalDTO> getActiveGoalsByUserId(final Long userId) {
        final List<Goal> userGoals = goalRepository.findByUserId(userId);
        final LocalDate today = LocalDate.now();

        return userGoals.stream()
                .filter(goal -> (goal.getStatus() == GoalStatus.IN_PROGRESS
                        || goal.getStatus() == GoalStatus.NOT_STARTED)
                        && !goal.getEndDate().isBefore(today))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get completed goals for a user
     */
    @Transactional(readOnly = true)
    public List<GoalDTO> getCompletedGoalsByUserId(final Long userId) {
        return goalRepository.findByUserIdAndStatus(userId, GoalStatus.COMPLETED).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Validate that end date is after start date
     */
    private void validateDates(final LocalDate startDate, final LocalDate endDate) {
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new RuntimeException("End date must be after start date");
        }
    }

    /**
     * Convert Goal entity to GoalDTO
     */
    private GoalDTO convertToDTO(final Goal goal) {
        return new GoalDTO(
                goal.getId(),
                goal.getTitle(),
                goal.getDescription(),
                goal.getImageUrl(),
                goal.getEmoji(),
                goal.getStartDate(),
                goal.getEndDate(),
                goal.getStatus(),
                goal.getUser().getId(),
                goal.getUser().getUsername(),
                goal.getCreatedAt(),
                goal.getUpdatedAt()
        );
    }
}

