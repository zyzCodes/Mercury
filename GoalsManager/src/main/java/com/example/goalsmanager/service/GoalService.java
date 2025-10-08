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
    public GoalService(GoalRepository goalRepository, UserRepository userRepository) {
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
    }

    /**
     * Create a new goal
     */
    public GoalDTO createGoal(CreateGoalRequest request) {
        // Validate user exists
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));

        // Validate dates
        validateDates(request.getStartDate(), request.getEndDate());

        // Create goal
        Goal goal = new Goal();
        goal.setTitle(request.getTitle());
        goal.setDescription(request.getDescription());
        goal.setImageUrl(request.getImageUrl());
        goal.setStartDate(request.getStartDate());
        goal.setEndDate(request.getEndDate());
        goal.setStatus(request.getStatus() != null ? request.getStatus() : GoalStatus.NOT_STARTED);
        goal.setNotes(request.getNotes());
        goal.setUser(user);

        Goal savedGoal = goalRepository.save(goal);
        return convertToDTO(savedGoal);
    }

    /**
     * Get goal by ID
     */
    @Transactional(readOnly = true)
    public GoalDTO getGoalById(Long id) {
        Goal goal = goalRepository.findById(id)
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
    public List<GoalDTO> getGoalsByUserId(Long userId) {
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
    public List<GoalDTO> getGoalsByUserIdAndStatus(Long userId, GoalStatus status) {
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
    public List<GoalDTO> getGoalsByStatus(GoalStatus status) {
        return goalRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Update goal
     */
    public GoalDTO updateGoal(Long id, UpdateGoalRequest request) {
        Goal goal = goalRepository.findById(id)
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
        if (request.getStartDate() != null) {
            goal.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            goal.setEndDate(request.getEndDate());
        }
        if (request.getStatus() != null) {
            goal.setStatus(request.getStatus());
        }
        if (request.getNotes() != null) {
            goal.setNotes(request.getNotes());
        }

        // Validate dates if both are present
        if (goal.getStartDate() != null && goal.getEndDate() != null) {
            validateDates(goal.getStartDate(), goal.getEndDate());
        }

        Goal updatedGoal = goalRepository.save(goal);
        return convertToDTO(updatedGoal);
    }

    /**
     * Update goal status
     */
    public GoalDTO updateGoalStatus(Long id, GoalStatus status) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found with id: " + id));
        
        goal.setStatus(status);
        Goal updatedGoal = goalRepository.save(goal);
        return convertToDTO(updatedGoal);
    }

    /**
     * Add or update notes for a goal
     */
    public GoalDTO updateGoalNotes(Long id, String notes) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found with id: " + id));
        
        goal.setNotes(notes);
        Goal updatedGoal = goalRepository.save(goal);
        return convertToDTO(updatedGoal);
    }

    /**
     * Delete goal
     */
    public void deleteGoal(Long id) {
        if (!goalRepository.existsById(id)) {
            throw new RuntimeException("Goal not found with id: " + id);
        }
        goalRepository.deleteById(id);
    }

    /**
     * Check if a goal exists
     */
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return goalRepository.existsById(id);
    }

    /**
     * Count goals by user
     */
    @Transactional(readOnly = true)
    public long countGoalsByUserId(Long userId) {
        return goalRepository.countByUserId(userId);
    }

    /**
     * Count goals by user and status
     */
    @Transactional(readOnly = true)
    public long countGoalsByUserIdAndStatus(Long userId, GoalStatus status) {
        return goalRepository.countByUserIdAndStatus(userId, status);
    }

    /**
     * Get overdue goals (end date has passed and status is not completed)
     */
    @Transactional(readOnly = true)
    public List<GoalDTO> getOverdueGoalsByUserId(Long userId) {
        List<Goal> userGoals = goalRepository.findByUserId(userId);
        LocalDate today = LocalDate.now();
        
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
    public List<GoalDTO> getActiveGoalsByUserId(Long userId) {
        List<Goal> userGoals = goalRepository.findByUserId(userId);
        LocalDate today = LocalDate.now();
        
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
    public List<GoalDTO> getCompletedGoalsByUserId(Long userId) {
        return goalRepository.findByUserIdAndStatus(userId, GoalStatus.COMPLETED).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Validate that end date is after start date
     */
    private void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new RuntimeException("End date must be after start date");
        }
    }

    /**
     * Convert Goal entity to GoalDTO
     */
    private GoalDTO convertToDTO(Goal goal) {
        return new GoalDTO(
                goal.getId(),
                goal.getTitle(),
                goal.getDescription(),
                goal.getImageUrl(),
                goal.getStartDate(),
                goal.getEndDate(),
                goal.getStatus(),
                goal.getNotes(),
                goal.getUser().getId(),
                goal.getUser().getUsername(),
                goal.getCreatedAt(),
                goal.getUpdatedAt()
        );
    }
}

